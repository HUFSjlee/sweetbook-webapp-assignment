package com.sweetbook.backend.sweetbook.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.sweetbook.backend.common.exception.BadRequestException;
import com.sweetbook.backend.common.exception.ExternalApiException;
import com.sweetbook.backend.integration.sweetbook.SweetBookClient;
import com.sweetbook.backend.photo.entity.Photo;
import com.sweetbook.backend.photo.service.PhotoService;
import com.sweetbook.backend.sweetbook.dto.BookBuildRequest;
import com.sweetbook.backend.storage.StorageService;
import com.sweetbook.backend.travel.entity.Travel;
import com.sweetbook.backend.travel.entity.TravelStatus;
import com.sweetbook.backend.travel.service.TravelService;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BookBuildService {

    private static final String DEFAULT_BLANK_TEMPLATE_UID = "73dHSfBDtnwk";
    private static final DateTimeFormatter YEAR_FORMATTER = DateTimeFormatter.ofPattern("yyyy");
    private static final DateTimeFormatter MONTH_TWO_DIGITS = DateTimeFormatter.ofPattern("MM");
    private static final DateTimeFormatter DAY_TWO_DIGITS = DateTimeFormatter.ofPattern("dd");
    private static final DateTimeFormatter DATE_LABEL_FORMATTER = DateTimeFormatter.ofPattern("MM\ndd");
    private static final String DEFAULT_LINE_URL = "https://dummyimage.com/12x900/f97316/f97316.png";

    private final TravelService travelService;
    private final PhotoService photoService;
    private final SweetBookClient sweetBookClient;
    private final StorageService storageService;

    @Transactional
    public Map<String, Object> build(UUID travelId, BookBuildRequest request) {
        Travel travel = travelService.getEntity(travelId);
        List<Photo> photos = photoService.getEntitiesByTravel(travelId);

        if (photos.isEmpty()) {
            throw new BadRequestException("포토북을 만들려면 최소 1장의 사진이 필요합니다.");
        }

        travel.setStatus(TravelStatus.BUILDING);

        JsonNode createdBookData = unwrapData(sweetBookClient.createBook(Map.of(
                "title", travel.getTitle(),
                "bookSpecUid", request.bookSpecUid(),
                "externalRef", travel.getId().toString()
        )));
        String bookUid = requireText(createdBookData, "bookUid");
        travel.setBookUid(bookUid);
        travel.setCoverImageUrl(photos.get(0).getImageUrl());

        List<UploadedPhotoRef> uploadedPhotos = uploadPhotos(bookUid, photos);

        JsonNode coverTemplate = unwrapData(sweetBookClient.getTemplateDetail(request.coverTemplateUid()));
        Map<String, Object> coverParameters = buildCoverParameters(travel, uploadedPhotos, coverTemplate);
        sweetBookClient.createCover(bookUid, request.coverTemplateUid(), coverParameters);

        JsonNode contentTemplate = unwrapData(sweetBookClient.getTemplateDetail(request.contentTemplateUid()));
        List<Map<String, Object>> contentPages = buildContentPages(travel, uploadedPhotos, contentTemplate);
        for (Map<String, Object> page : contentPages) {
            sweetBookClient.addContents(bookUid, request.contentTemplateUid(), page, null);
        }

        int targetPages = resolveTargetPageCount(request.bookSpecUid());
        String blankTemplateUid = request.blankTemplateUid() == null || request.blankTemplateUid().isBlank()
                ? DEFAULT_BLANK_TEMPLATE_UID
                : request.blankTemplateUid();

        if (contentPages.size() < targetPages) {
            JsonNode blankTemplate = unwrapData(sweetBookClient.getTemplateDetail(blankTemplateUid));
            Map<String, Object> blankParameters = buildBlankParameters(travel, blankTemplate);
            for (int i = contentPages.size(); i < targetPages; i++) {
                sweetBookClient.addContents(bookUid, blankTemplateUid, blankParameters, "page");
            }
        }

        JsonNode finalizedData = unwrapData(sweetBookClient.finalizeBook(bookUid));
        travel.setStatus(TravelStatus.COMPLETED);

        return Map.of(
                "travelId", travel.getId(),
                "bookUid", bookUid,
                "pageCount", finalizedData.path("pageCount").asInt(targetPages),
                "finalizedAt", finalizedData.path("finalizedAt").asText(null)
        );
    }

    public JsonNode getBookSpecs() {
        return unwrapData(sweetBookClient.getBookSpecs());
    }

    public JsonNode getTemplates(String bookSpecUid) {
        return unwrapData(sweetBookClient.getTemplates(bookSpecUid));
    }

    private List<UploadedPhotoRef> uploadPhotos(String bookUid, List<Photo> photos) {
        List<UploadedPhotoRef> uploadedPhotos = new ArrayList<>();
        for (Photo photo : photos) {
            try {
                byte[] bytes = storageService.readBytes(photo.getStoragePath());
                JsonNode uploadedData = unwrapData(sweetBookClient.uploadPhoto(
                        bookUid,
                        photo.getId() + guessFileExtension(photo.getStoragePath()),
                        guessContentType(photo.getStoragePath()),
                        bytes
                ));
                uploadedPhotos.add(new UploadedPhotoRef(photo, requireText(uploadedData, "fileName")));
            } catch (IOException exception) {
                throw new ExternalApiException("업로드할 사진 파일을 읽지 못했습니다.", exception);
            }
        }
        return uploadedPhotos;
    }

    private Map<String, Object> buildCoverParameters(Travel travel, List<UploadedPhotoRef> uploadedPhotos, JsonNode templateDetail) {
        JsonNode definitions = templateDetail.path("parameters").path("definitions");
        Map<String, Object> parameters = new LinkedHashMap<>();

        definitions.fieldNames().forEachRemaining(name -> {
            String lowered = name.toLowerCase(Locale.ROOT);
            if (lowered.contains("photo")) {
                parameters.put(name, uploadedPhotos.get(0).fileName());
                return;
            }
            if (lowered.contains("title")) {
                parameters.put(name, travel.getTitle());
                return;
            }
            if (lowered.contains("subtitle")) {
                parameters.put(name, defaultSubtitle(travel));
                return;
            }
            if (lowered.contains("date") || lowered.contains("period")) {
                parameters.put(name, formatDateRange(travel));
                return;
            }
            if (lowered.contains("volume")) {
                parameters.put(name, travel.getMood() == null || travel.getMood().isBlank() ? "TRIP" : travel.getMood());
                return;
            }
            parameters.put(name, travel.getTitle());
        });

        return parameters;
    }

    private List<Map<String, Object>> buildContentPages(Travel travel, List<UploadedPhotoRef> uploadedPhotos, JsonNode templateDetail) {
        JsonNode definitions = templateDetail.path("parameters").path("definitions");
        boolean galleryTemplate = hasArrayFileBinding(definitions);
        List<Map<String, Object>> pages = new ArrayList<>();

        if (galleryTemplate) {
            for (List<UploadedPhotoRef> chunk : chunk(uploadedPhotos, 3)) {
                pages.add(buildGalleryPageParameters(travel, chunk, definitions));
            }
            return pages;
        }

        for (UploadedPhotoRef ref : uploadedPhotos) {
            pages.add(buildSinglePhotoPageParameters(travel, ref, definitions));
        }
        return pages;
    }

    private Map<String, Object> buildGalleryPageParameters(Travel travel, List<UploadedPhotoRef> chunk, JsonNode definitions) {
        Map<String, Object> parameters = new LinkedHashMap<>();
        LocalDate date = resolveDate(chunk.get(0).photo());

        definitions.fieldNames().forEachRemaining(name -> {
            String lowered = name.toLowerCase(Locale.ROOT);
            if (lowered.contains("collage") || lowered.contains("gallery") || lowered.contains("photos")) {
                parameters.put(name, chunk.stream().map(UploadedPhotoRef::fileName).toList());
                return;
            }
            if (lowered.contains("month")) {
                parameters.put(name, formatMonthField(name, date));
                return;
            }
            if (lowered.contains("day")) {
                parameters.put(name, DAY_TWO_DIGITS.format(date));
                return;
            }
            if (lowered.contains("title")) {
                parameters.put(name, travel.getTitle());
                return;
            }
            parameters.put(name, "");
        });

        return parameters;
    }

    private Map<String, Object> buildSinglePhotoPageParameters(Travel travel, UploadedPhotoRef ref, JsonNode definitions) {
        Map<String, Object> parameters = new LinkedHashMap<>();
        LocalDate date = resolveDate(ref.photo());
        String comment = ref.photo().getComment() == null || ref.photo().getComment().isBlank()
                ? defaultComment(travel, date)
                : ref.photo().getComment();

        definitions.fieldNames().forEachRemaining(name -> {
            String lowered = name.toLowerCase(Locale.ROOT);
            if (lowered.equals("photo") || lowered.endsWith("photo")) {
                parameters.put(name, ref.fileName());
                return;
            }
            if (lowered.contains("diary") || lowered.contains("comment")) {
                parameters.put(name, comment);
                return;
            }
            if (lowered.contains("daylabel")) {
                parameters.put(name, DATE_LABEL_FORMATTER.format(date));
                return;
            }
            if (lowered.contains("hasdaylabel")) {
                parameters.put(name, "true");
                return;
            }
            if (lowered.contains("monthname")) {
                parameters.put(name, date.getMonth().getDisplayName(java.time.format.TextStyle.FULL, Locale.ENGLISH));
                return;
            }
            if (lowered.contains("monthcolor")) {
                parameters.put(name, "#FFF97316");
                return;
            }
            if (lowered.contains("month")) {
                parameters.put(name, formatMonthField(name, date));
                return;
            }
            if (lowered.contains("dayofweekx")) {
                parameters.put(name, "0");
                return;
            }
            if (lowered.contains("dayofweek")) {
                parameters.put(name, date.getDayOfWeek().getDisplayName(java.time.format.TextStyle.SHORT, Locale.KOREAN));
                return;
            }
            if (lowered.contains("date")) {
                parameters.put(name, DAY_TWO_DIGITS.format(date));
                return;
            }
            if (lowered.contains("year")) {
                parameters.put(name, YEAR_FORMATTER.format(date));
                return;
            }
            if (lowered.contains("booktitle") || lowered.equals("title")) {
                parameters.put(name, travel.getTitle());
                return;
            }
            if (lowered.contains("weather")) {
                parameters.put(name, "Sunny");
                return;
            }
            if (lowered.contains("meal")) {
                parameters.put(name, "Cafe");
                return;
            }
            if (lowered.contains("nap")) {
                parameters.put(name, "Rest");
                return;
            }
            if (lowered.contains("parentcomment") || lowered.contains("teachercomment")) {
                parameters.put(name, comment);
                return;
            }
            if (lowered.contains("hasparentcomment") || lowered.contains("hasteachercomment")) {
                parameters.put(name, "true");
                return;
            }
            if (lowered.contains("linevertical")) {
                parameters.put(name, DEFAULT_LINE_URL);
                return;
            }
            parameters.put(name, "");
        });

        return parameters;
    }

    private Map<String, Object> buildBlankParameters(Travel travel, JsonNode templateDetail) {
        JsonNode definitions = templateDetail.path("parameters").path("definitions");
        Map<String, Object> parameters = new LinkedHashMap<>();

        definitions.fieldNames().forEachRemaining(name -> {
            String lowered = name.toLowerCase(Locale.ROOT);
            if (lowered.contains("title")) {
                parameters.put(name, travel.getTitle());
                return;
            }
            if (lowered.contains("line")) {
                parameters.put(name, DEFAULT_LINE_URL);
                return;
            }
            if (lowered.contains("icon")) {
                parameters.put(name, DEFAULT_LINE_URL);
                return;
            }
            parameters.put(name, "");
        });

        return parameters;
    }

    private int resolveTargetPageCount(String bookSpecUid) {
        JsonNode specs = unwrapData(sweetBookClient.getBookSpecs());
        for (JsonNode spec : specs) {
            if (bookSpecUid.equals(spec.path("bookSpecUid").asText())) {
                return spec.path("pageDefault").asInt(24);
            }
        }
        return 24;
    }

    private boolean hasArrayFileBinding(JsonNode definitions) {
        var fields = definitions.fields();
        while (fields.hasNext()) {
            var entry = fields.next();
            JsonNode value = entry.getValue();
            if ("array".equals(value.path("type").asText()) && value.path("itemType").asText().equalsIgnoreCase("file")) {
                return true;
            }
        }
        return false;
    }

    private List<List<UploadedPhotoRef>> chunk(List<UploadedPhotoRef> input, int size) {
        List<List<UploadedPhotoRef>> chunks = new ArrayList<>();
        for (int i = 0; i < input.size(); i += size) {
            chunks.add(input.subList(i, Math.min(i + size, input.size())));
        }
        return chunks;
    }

    private JsonNode unwrapData(JsonNode node) {
        return node.path("data");
    }

    private String requireText(JsonNode node, String field) {
        JsonNode value = node.path(field);
        if (value.isMissingNode() || value.isNull() || value.asText().isBlank()) {
            throw new BadRequestException("SweetBook 응답에서 " + field + " 값을 찾지 못했습니다.");
        }
        return value.asText();
    }

    private LocalDate resolveDate(Photo photo) {
        return photo.getTakenDate() != null ? photo.getTakenDate() : LocalDate.now();
    }

    private String defaultSubtitle(Travel travel) {
        if (travel.getDescription() != null && !travel.getDescription().isBlank()) {
            return travel.getDescription();
        }
        return "Collected on the road";
    }

    private String formatDateRange(Travel travel) {
        if (travel.getStartDate() != null && travel.getEndDate() != null) {
            return travel.getStartDate().format(DateTimeFormatter.ofPattern("yy.MM")) +
                    " - " +
                    travel.getEndDate().format(DateTimeFormatter.ofPattern("yy.MM"));
        }
        if (travel.getStartDate() != null) {
            return travel.getStartDate().format(DateTimeFormatter.ofPattern("yyyy.MM"));
        }
        return String.valueOf(LocalDate.now().getYear());
    }

    private String formatMonthField(String fieldName, LocalDate date) {
        String lowered = fieldName.toLowerCase(Locale.ROOT);
        if (lowered.contains("name")) {
            return date.getMonth().getDisplayName(java.time.format.TextStyle.FULL, Locale.ENGLISH);
        }
        return MONTH_TWO_DIGITS.format(date);
    }

    private String defaultComment(Travel travel, LocalDate date) {
        return travel.getTitle() + "의 " + MONTH_TWO_DIGITS.format(date) + "." + DAY_TWO_DIGITS.format(date) + " 기록";
    }

    private String guessContentType(String storagePath) {
        String lowered = storagePath.toLowerCase(Locale.ROOT);
        if (lowered.endsWith(".png")) {
            return "image/png";
        }
        if (lowered.endsWith(".webp")) {
            return "image/webp";
        }
        if (lowered.endsWith(".gif")) {
            return "image/gif";
        }
        return "image/jpeg";
    }

    private String guessFileExtension(String storagePath) {
        int index = storagePath.lastIndexOf('.');
        return index >= 0 ? storagePath.substring(index) : ".jpg";
    }

    private record UploadedPhotoRef(Photo photo, String fileName) {
    }
}
