package kr.hhplus.be.server.infra.coupon;

import kr.hhplus.be.server.common.exception.CustomException;
import kr.hhplus.be.server.domain.coupon.CouponRepository;
import kr.hhplus.be.server.domain.coupon.entity.Coupon;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static kr.hhplus.be.server.config.swagger.ErrorCode.*;

@Repository
@RequiredArgsConstructor
public class CouponRepositoryImpl implements CouponRepository {

    private final JpaCouponRespository jpaCouponRespository;

    @Override
    public Coupon getById(Long couponId) {
        return jpaCouponRespository.findById(couponId).orElseThrow(() -> new CustomException(NOT_EXIST_COUPON));
    }

    @Override
    public List<Coupon> getByCouponIds(List<Long> couponIds) {
        return jpaCouponRespository.findByIdIn(couponIds);
    }

    @Override
    public void save(Coupon coupon) {
        jpaCouponRespository.save(coupon);
    }
}
