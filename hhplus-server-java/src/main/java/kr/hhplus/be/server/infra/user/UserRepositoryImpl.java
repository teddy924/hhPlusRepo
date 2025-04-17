package kr.hhplus.be.server.infra.user;

import kr.hhplus.be.server.common.exception.CustomException;
import kr.hhplus.be.server.domain.user.UserRepository;
import kr.hhplus.be.server.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import static kr.hhplus.be.server.config.swagger.ErrorCode.*;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final JpaUserRepository jpaUserRepository;

    @Override
    public User getById(long id) {
        return jpaUserRepository.findById(id)
                .orElseThrow(() -> new CustomException(NOT_EXIST_USER));
    }
}
