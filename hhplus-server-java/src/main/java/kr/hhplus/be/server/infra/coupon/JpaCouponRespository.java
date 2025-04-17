package kr.hhplus.be.server.infra.coupon;

import kr.hhplus.be.server.domain.coupon.entity.Coupon;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface JpaCouponRespository extends JpaRepository<Coupon, Long> {

    @NonNull
    Optional<Coupon> findById(@NonNull Long couponId);

    List<Coupon> findByIdIn(List<Long> couponIds);

}
