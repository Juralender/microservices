package ru.otus.hw.order.clients;

import lombok.RequiredArgsConstructor;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import ru.otus.hw.order.clients.dto.BillingUserResponse;
import ru.otus.hw.order.clients.dto.BillingWithdrawRequest;
import ru.otus.hw.order.clients.dto.BillingWithdrawResult;
import ru.otus.hw.order.exceptions.UpstreamServiceException;
import ru.otus.hw.order.exceptions.UserNotFoundException;

import java.math.BigDecimal;

@RequiredArgsConstructor
public class BillingClient {

    private final RestClient restClient;

    public BillingUserResponse getUser(Long userId) {
        try {
            return restClient.get()
                    .uri("/api/users/{userId}", userId)
                    .retrieve()
                    .body(BillingUserResponse.class);
        } catch (HttpClientErrorException.NotFound ex) {
            throw new UserNotFoundException("User with id " + userId + " not found in billing-service");
        } catch (Exception ex) {
            throw new UpstreamServiceException("Failed to fetch user " + userId + " from billing-service", ex);
        }
    }

    public BillingWithdrawResult withdraw(Long userId, BigDecimal amount) {
        try {
            return restClient.post()
                    .uri("/api/accounts/{userId}/withdraw", userId)
                    .body(new BillingWithdrawRequest(amount))
                    .retrieve()
                    .body(BillingWithdrawResult.class);
        } catch (HttpClientErrorException.NotFound ex) {
            throw new UserNotFoundException("User with id " + userId + " not found in billing-service");
        } catch (Exception ex) {
            throw new UpstreamServiceException("Failed to withdraw funds for user " + userId + " from billing-service", ex);
        }
    }
}
