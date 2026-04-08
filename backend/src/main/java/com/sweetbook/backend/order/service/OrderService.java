package com.sweetbook.backend.order.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sweetbook.backend.common.exception.BadRequestException;
import com.sweetbook.backend.common.exception.NotFoundException;
import com.sweetbook.backend.integration.sweetbook.SweetBookClient;
import com.sweetbook.backend.order.dto.OrderCreateRequest;
import com.sweetbook.backend.order.dto.OrderEstimateRequest;
import com.sweetbook.backend.order.dto.OrderEstimateResponse;
import com.sweetbook.backend.order.dto.OrderResponse;
import com.sweetbook.backend.order.entity.OrderStatus;
import com.sweetbook.backend.order.entity.TravelOrder;
import com.sweetbook.backend.order.repository.TravelOrderRepository;
import com.sweetbook.backend.travel.entity.Travel;
import com.sweetbook.backend.travel.entity.TravelStatus;
import com.sweetbook.backend.travel.service.TravelService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

    private final TravelOrderRepository orderRepository;
    private final TravelService travelService;
    private final SweetBookClient sweetBookClient;
    private final ObjectMapper objectMapper;

    public OrderEstimateResponse estimate(UUID travelId, OrderEstimateRequest request) {
        Travel travel = travelService.getEntity(travelId);
        ensureBookReady(travel);

        Map<String, Object> payload = new HashMap<>();
        payload.put("items", List.of(Map.of(
                "bookUid", travel.getBookUid(),
                "quantity", 1
        )));
        if (request.extraOptions() != null) {
            payload.putAll(request.extraOptions());
        }

        JsonNode response = unwrapData(sweetBookClient.estimateOrder(payload));
        return new OrderEstimateResponse(
                travelId.toString(),
                extractInteger(response, "estimatedPrice", "price", "totalPrice"),
                toPlainObject(response)
        );
    }

    @Transactional
    public OrderResponse create(UUID travelId, OrderCreateRequest request) {
        Travel travel = travelService.getEntity(travelId);
        ensureBookReady(travel);

        Map<String, Object> payload = new HashMap<>();
        payload.put("items", List.of(Map.of(
                "bookUid", travel.getBookUid(),
                "quantity", 1
        )));
        payload.put("shipping", Map.of(
                "recipientName", request.recipientName(),
                "recipientPhone", request.recipientPhone(),
                "postalCode", request.postalCode(),
                "address1", request.address1(),
                "address2", request.address2() == null ? "" : request.address2(),
                "memo", request.memo() == null ? "" : request.memo()
        ));
        if (request.extraOptions() != null) {
            payload.putAll(request.extraOptions());
        }

        JsonNode response = unwrapData(sweetBookClient.createOrder(payload));

        TravelOrder order = new TravelOrder();
        order.setTravel(travel);
        order.setOrderUid(extractString(response, "orderUid", "uid", "id"));
        order.setRecipientName(request.recipientName());
        order.setAddress(request.address1()
                + (request.address2() == null || request.address2().isBlank() ? "" : " " + request.address2()));
        order.setPhone(request.recipientPhone());
        order.setEstimatedPrice(request.estimatedPrice());
        order.setStatus(OrderStatus.CREATED);

        travel.setStatus(TravelStatus.ORDERED);

        return OrderResponse.from(orderRepository.save(order));
    }

    public List<OrderResponse> findAll() {
        return orderRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(OrderResponse::from)
                .toList();
    }

    public Map<String, Object> findDetail(UUID orderId) {
        TravelOrder order = getEntity(orderId);
        JsonNode response = unwrapData(sweetBookClient.getOrder(order.getOrderUid()));
        return Map.of(
                "order", OrderResponse.from(order),
                "sweetBookOrder", toPlainObject(response)
        );
    }

    @Transactional
    public OrderResponse cancel(UUID orderId) {
        TravelOrder order = getEntity(orderId);
        sweetBookClient.cancelOrder(order.getOrderUid());
        order.setStatus(OrderStatus.CANCELED);
        return OrderResponse.from(order);
    }

    public TravelOrder getEntity(UUID orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("주문을 찾을 수 없습니다."));
    }

    private void ensureBookReady(Travel travel) {
        if (travel.getBookUid() == null || travel.getBookUid().isBlank()) {
            throw new BadRequestException("주문 전에 포토북 생성이 먼저 필요합니다.");
        }
    }

    private Integer extractInteger(JsonNode node, String... keys) {
        for (String key : keys) {
            JsonNode value = node.get(key);
            if (value != null && value.isNumber()) {
                return value.asInt();
            }
        }

        JsonNode totalAmount = node.get("totalAmount");
        if (totalAmount != null && totalAmount.isNumber()) {
            return totalAmount.asInt();
        }

        return 0;
    }

    private String extractString(JsonNode node, String... keys) {
        for (String key : keys) {
            JsonNode value = node.get(key);
            if (value != null && !value.isNull() && !value.asText().isBlank()) {
                return value.asText();
            }
        }

        throw new BadRequestException("SweetBook 주문 응답에서 주문 식별자를 찾지 못했습니다.");
    }

    private JsonNode unwrapData(JsonNode node) {
        return node.path("data");
    }

    private Object toPlainObject(JsonNode node) {
        return objectMapper.convertValue(node, new TypeReference<Object>() {
        });
    }
}
