package kr.hhplus.be.server.user.repository;

import kr.hhplus.be.server.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
