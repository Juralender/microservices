package ru.otus.hw.order.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.otus.hw.order.clients.BillingClient;
import ru.otus.hw.order.clients.NotificationClient;
import ru.otus.hw.order.clients.dto.BillingUserResponse;
import ru.otus.hw.order.clients.dto.BillingWithdrawResult;
import ru.otus.hw.order.clients.dto.NotificationEmailRequest;
import ru.otus.hw.order.models.Order;
import ru.otus.hw.order.models.OrderStatus;
import ru.otus.hw.order.repositories.OrderRepository;
import ru.otus.hw.order.services.dto.OrderCreateRequest;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private BillingClient billingClient;

    @Mock
    private NotificationClient notificationClient;

    private OrderServiceImpl orderService;

    @Test
    void successfulPaymentProducesSuccessOrderAndSuccessEmail() {
        orderService = new OrderServiceImpl(orderRepository, billingClient, notificationClient);
        var request = new OrderCreateRequest(1L, new BigDecimal("50.00"));
        when(billingClient.getUser(1L)).thenReturn(new BillingUserResponse(1L, "alice", "alice@example.com"));
        when(billingClient.withdraw(1L, request.getPrice()))
                .thenReturn(new BillingWithdrawResult(true, new BigDecimal("50.00"), "Withdrawal successful"));
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

        var response = orderService.create(request);

        assertThat(response.getStatus()).isEqualTo(OrderStatus.SUCCESS);
        var emailCaptor = ArgumentCaptor.forClass(NotificationEmailRequest.class);
        verify(notificationClient).sendEmail(emailCaptor.capture());
        assertThat(emailCaptor.getValue().getRecipient()).isEqualTo("alice@example.com");
        assertThat(emailCaptor.getValue().getSubject()).containsIgnoringCase("succeeded");
    }

    @Test
    void insufficientFundsProducesFailedOrderAndFailureEmail() {
        orderService = new OrderServiceImpl(orderRepository, billingClient, notificationClient);
        var request = new OrderCreateRequest(2L, new BigDecimal("1000.00"));
        when(billingClient.getUser(2L)).thenReturn(new BillingUserResponse(2L, "bob", "bob@example.com"));
        when(billingClient.withdraw(eq(2L), any(BigDecimal.class)))
                .thenReturn(new BillingWithdrawResult(false, new BigDecimal("10.00"), "Insufficient funds"));
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

        var response = orderService.create(request);

        assertThat(response.getStatus()).isEqualTo(OrderStatus.FAILED);
        var emailCaptor = ArgumentCaptor.forClass(NotificationEmailRequest.class);
        verify(notificationClient).sendEmail(emailCaptor.capture());
        assertThat(emailCaptor.getValue().getRecipient()).isEqualTo("bob@example.com");
        assertThat(emailCaptor.getValue().getSubject()).containsIgnoringCase("failed");
    }
}
