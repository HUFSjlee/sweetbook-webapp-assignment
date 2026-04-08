package com.sweetbook.backend.bootstrap;

import com.sweetbook.backend.photo.entity.Photo;
import com.sweetbook.backend.photo.repository.PhotoRepository;
import com.sweetbook.backend.storage.StorageService;
import com.sweetbook.backend.travel.entity.Travel;
import com.sweetbook.backend.travel.entity.TravelStatus;
import com.sweetbook.backend.travel.repository.TravelRepository;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import javax.imageio.ImageIO;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DummyDataInitializer implements ApplicationRunner {

    private final TravelRepository travelRepository;
    private final PhotoRepository photoRepository;
    private final StorageService storageService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (travelRepository.count() > 0) {
            return;
        }

        seedTravel(
                "Kyoto Golden Hour",
                "봄빛이 남은 골목과 정원의 결을 모은 여행",
                "Warm",
                LocalDate.of(2026, 3, 18),
                List.of(
                        "철길 옆 골목에 햇살이 눕던 오후",
                        "사찰 계단에서 내려다본 도시의 붉은 지붕",
                        "작은 찻집 유리창에 번진 노을빛",
                        "강가를 따라 천천히 걸었던 저녁",
                        "마지막 날, 비 냄새가 가볍게 스치던 거리"
                ),
                new Color(245, 124, 0),
                new Color(255, 224, 178)
        );

        seedTravel(
                "Busan Blue Notes",
                "파도, 네온, 회색 콘크리트가 섞인 해안 도시 기록",
                "Coastal",
                LocalDate.of(2026, 2, 9),
                List.of(
                        "바다 안개가 낮게 깔린 이른 아침의 방파제",
                        "골목 벽화 사이로 지나가던 푸른 그림자",
                        "해 질 무렵 광안대교가 켜지기 직전의 색",
                        "시장 골목에서 마주친 반짝이는 간판들",
                        "밤바람에 엷게 흔들리던 해변의 불빛"
                ),
                new Color(2, 132, 199),
                new Color(186, 230, 253)
        );
    }

    private void seedTravel(
            String title,
            String description,
            String mood,
            LocalDate startDate,
            List<String> comments,
            Color primary,
            Color secondary
    ) throws IOException {
        Travel travel = new Travel();
        travel.setTitle(title);
        travel.setDescription(description);
        travel.setMood(mood);
        travel.setStartDate(startDate);
        travel.setEndDate(startDate.plusDays(comments.size() - 1L));
        travel.setStatus(TravelStatus.DRAFT);
        travelRepository.save(travel);

        for (int i = 0; i < comments.size(); i++) {
            byte[] imageBytes = generateImage(title, comments.get(i), i + 1, primary, secondary);
            StorageService.StoredFile storedFile = storageService.storeBytes(
                    "travels/" + travel.getId(),
                    "seed-" + (i + 1) + ".png",
                    "image/png",
                    imageBytes
            );

            Photo photo = new Photo();
            photo.setTravel(travel);
            photo.setImageUrl(storedFile.publicUrl());
            photo.setStoragePath(storedFile.storagePath());
            photo.setComment(comments.get(i));
            photo.setLocation(title.contains("Kyoto") ? "Japan" : "Busan");
            photo.setTakenDate(startDate.plusDays(i));
            photo.setSortOrder(i + 1);
            photoRepository.save(photo);
        }
    }

    private byte[] generateImage(String title, String caption, int index, Color primary, Color secondary) throws IOException {
        BufferedImage image = new BufferedImage(1600, 1100, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = image.createGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setPaint(new GradientPaint(0, 0, primary, 1600, 1100, secondary));
        graphics.fillRect(0, 0, 1600, 1100);

        graphics.setColor(new Color(255, 255, 255, 48));
        graphics.fillRoundRect(120, 120, 1360, 860, 48, 48);

        graphics.setColor(Color.WHITE);
        graphics.setFont(new Font("Serif", Font.BOLD, 72));
        graphics.drawString(title, 160, 250);

        graphics.setFont(new Font("SansSerif", Font.PLAIN, 34));
        graphics.drawString("Travel Frame " + index, 160, 340);

        graphics.setFont(new Font("SansSerif", Font.PLAIN, 42));
        drawMultiline(graphics, caption, 160, 470, 1100, 58);

        graphics.setColor(new Color(255, 255, 255, 180));
        graphics.fillRoundRect(1080, 180, 240, 360, 28, 28);
        graphics.setColor(primary.darker());
        graphics.setFont(new Font("SansSerif", Font.BOLD, 32));
        graphics.drawString("POSTCARD", 1110, 260);
        graphics.setFont(new Font("SansSerif", Font.PLAIN, 24));
        graphics.drawString("TravelogBook", 1110, 320);
        graphics.drawString(OffsetDateTime.now().toLocalDate().toString(), 1110, 380);

        graphics.dispose();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(image, "png", outputStream);
        return outputStream.toByteArray();
    }

    private void drawMultiline(Graphics2D graphics, String text, int x, int y, int maxWidth, int lineHeight) {
        String[] words = text.split(" ");
        StringBuilder line = new StringBuilder();
        int currentY = y;

        for (String word : words) {
            String candidate = line.isEmpty() ? word : line + " " + word;
            if (graphics.getFontMetrics().stringWidth(candidate) > maxWidth) {
                graphics.drawString(line.toString(), x, currentY);
                line = new StringBuilder(word);
                currentY += lineHeight;
            } else {
                line = new StringBuilder(candidate);
            }
        }

        if (!line.isEmpty()) {
            graphics.drawString(line.toString(), x, currentY);
        }
    }
}
