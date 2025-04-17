package kr.hhplus.be.server.infra.order;

import kr.hhplus.be.server.domain.order.entity.OrderAddress;
import kr.hhplus.be.server.interfaces.order.OrderAddressDTO;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface JpaOrderAddressRepository extends JpaRepository<OrderAddress, Long> {

    @NonNull
    Optional<OrderAddress> findByOrderId(@NonNull Long orderId);

    @Query("SELECT new kr.hhplus.be.server.interfaces.order.OrderAddressDTO(A.order.id, A.receiverName, A.phone, A.address1, A.address2, A.zipcode, A.memo, A.sysCretDt, A.sysChgDt) " +
            "FROM OrderAddress A WHERE A.order.id = :orderId")
    Optional<OrderAddressDTO> findDTOByOrderId(Long orderId);
}
