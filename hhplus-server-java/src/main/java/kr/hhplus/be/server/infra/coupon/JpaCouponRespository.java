package kr.hhplus.be.server.infra.coupon;

import jakarta.persistence.LockModeType;
import kr.hhplus.be.server.domain.coupon.entity.Coupon;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.List;
import java.util.Optional;

public interface JpaCouponRespository extends JpaRepository<Coupon, Long> {

    @NonNull
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Coupon> findById(@NonNull Long couponId);

    List<Coupon> findByIdIn(List<Long> couponIds);

}
