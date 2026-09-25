package ru.otus.hw.billing.services;

import org.junit.jupiter.api.RepeatedTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.otus.hw.billing.services.dto.UserCreateRequest;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class AccountServiceConcurrencyTest {

    private static final int THREADS = 20;
    private static final BigDecimal STARTING_BALANCE = new BigDecimal("1000.00");
    private static final BigDecimal WITHDRAW_AMOUNT = new BigDecimal("100.00");
    private static final int EXPECTED_SUCCESSES = 10;

    @Autowired
    private UserService userService;

    @Autowired
    private AccountService accountService;

    @RepeatedTest(3)
    void concurrentWithdrawalsNeverOverdraw() throws Exception {
        var user = userService.create(new UserCreateRequest(
                "racer" + System.nanoTime(), "racer" + System.nanoTime() + "@example.com"));
        accountService.deposit(user.getId(), STARTING_BALANCE);

        var barrier = new CyclicBarrier(THREADS);
        var executor = Executors.newFixedThreadPool(THREADS);
        try {
            List<Callable<Boolean>> tasks = java.util.stream.IntStream.range(0, THREADS)
                    .<Callable<Boolean>>mapToObj(i -> () -> {
                        barrier.await();
                        return accountService.withdraw(user.getId(), WITHDRAW_AMOUNT).isSuccess();
                    })
                    .toList();

            List<Future<Boolean>> futures = executor.invokeAll(tasks);
            var successCount = new AtomicInteger();
            for (var f : futures) {
                if (f.get()) {
                    successCount.incrementAndGet();
                }
            }

            assertThat(successCount.get()).isEqualTo(EXPECTED_SUCCESSES);
            assertThat(accountService.findByUserId(user.getId()).getBalance())
                    .isEqualByComparingTo(BigDecimal.ZERO);
        } finally {
            executor.shutdown();
            executor.awaitTermination(10, TimeUnit.SECONDS);
        }
    }
}
