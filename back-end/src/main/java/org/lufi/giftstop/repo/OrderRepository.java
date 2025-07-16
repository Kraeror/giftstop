package org.lufi.giftstop.repo;

import org.lufi.giftstop.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}