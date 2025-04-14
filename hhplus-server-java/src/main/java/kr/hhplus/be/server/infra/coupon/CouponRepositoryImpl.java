package kr.hhplus.be.server.infra.coupon;

import kr.hhplus.be.server.domain.coupon.CouponRepository;
import kr.hhplus.be.server.domain.coupon.entity.Coupon;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class CouponRepositoryImpl implements CouponRepository {

    @Override
    public Optional<Coupon> findById(Long couponId) {
        return Optional.empty();
    }

    @Override
    public List<Coupon> findByCouponIds(List<Long> couponIds) {
        return List.of();
    }

    @Override
    public void save(Coupon coupon) {

    }
}
