package ru.otus.hw.order.services;

import ru.otus.hw.order.services.dto.OrderCreateRequest;
import ru.otus.hw.order.services.dto.OrderResponse;

import java.util.List;

public interface OrderService {
    OrderResponse create(OrderCreateRequest request);

    OrderResponse findById(Long id);

    List<OrderResponse> findAllByUserId(Long userId);
}
