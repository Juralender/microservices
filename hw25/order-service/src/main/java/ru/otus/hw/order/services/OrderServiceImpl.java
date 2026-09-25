package ru.otus.hw.order.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.order.clients.BillingClient;
import ru.otus.hw.order.clients.NotificationClient;
import ru.otus.hw.order.clients.dto.NotificationEmailRequest;
import ru.otus.hw.order.exceptions.OrderNotFoundException;
import ru.otus.hw.order.models.Order;
import ru.otus.hw.order.models.OrderStatus;
import ru.otus.hw.order.repositories.OrderRepository;
import ru.otus.hw.order.services.dto.OrderCreateRequest;
import ru.otus.hw.order.services.dto.OrderResponse;

import java.time.Instant;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final BillingClient billingClient;
    private final NotificationClient notificationClient;

    @Override
    @Transactional
    public OrderResponse create(OrderCreateRequest request) {
        log.info("Creating order for user {}, price {}", request.getUserId(), request.getPrice());

        // Step 0: resolve the recipient address for the result email.
        var user = billingClient.getUser(request.getUserId());

        // Step 1: deduct funds via billing-service.
        var withdrawal = billingClient.withdraw(request.getUserId(), request.getPrice());
        var status = withdrawal.isSuccess() ? OrderStatus.SUCCESS : OrderStatus.FAILED;
        log.info("Billing withdrawal for user {}: success={}, balance={}",
                request.getUserId(), withdrawal.isSuccess(), withdrawal.getBalance());

        // Step 2: notify the user of the outcome.
        notificationClient.sendEmail(buildEmail(user.getEmail(), request, status, withdrawal.getMessage()));

        var order = orderRepository.save(new Order(null, request.getUserId(), request.getPrice(), status, Instant.now()));
        log.info("Order {} for user {} completed with status {}", order.getId(), request.getUserId(), status);
        return toResponse(order);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse findById(Long id) {
        var order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Order with id " + id + " not found"));
        return toResponse(order);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> findAllByUserId(Long userId) {
        return orderRepository.findAllByUserIdOrderByIdDesc(userId).stream()
                .map(OrderServiceImpl::toResponse)
                .toList();
    }

    private static NotificationEmailRequest buildEmail(String recipient, OrderCreateRequest request,
                                                         OrderStatus status, String billingMessage) {
        var subject = status == OrderStatus.SUCCESS
                ? "Order for " + request.getPrice() + " succeeded"
                : "Order for " + request.getPrice() + " failed";
        var body = status == OrderStatus.SUCCESS
                ? "Your payment of " + request.getPrice() + " was successful."
                : "Your payment of " + request.getPrice() + " could not be completed: " + billingMessage;
        return new NotificationEmailRequest(recipient, subject, body);
    }

    private static OrderResponse toResponse(Order order) {
        return new OrderResponse(order.getId(), order.getUserId(), order.getPrice(), order.getStatus(), order.getCreatedAt());
    }
}
