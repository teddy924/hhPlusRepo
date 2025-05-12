package kr.hhplus.be.server.infra.order;

import kr.hhplus.be.server.domain.order.entity.OrderAddress;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JpaOrderAddressRepository extends JpaRepository<OrderAddress, Long> {

    @NonNull
    Optional<OrderAddress> findByOrderId(@NonNull Long orderId);

}
