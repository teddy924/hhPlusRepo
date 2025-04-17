package kr.hhplus.be.server.infra.user;

import kr.hhplus.be.server.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JpaUserRepository extends JpaRepository<User, Long> {

    Optional<User> findById(long id);

}
