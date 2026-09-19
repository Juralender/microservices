package ru.otus.hw.order.repositories;

import ru.otus.hw.order.models.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findAllByUserIdOrderByIdDesc(Long userId);
}
