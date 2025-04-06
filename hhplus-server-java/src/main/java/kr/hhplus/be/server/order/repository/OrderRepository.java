package kr.hhplus.be.server.order.repository;

import kr.hhplus.be.server.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<User, Long> {
}
