package kr.hhplus.be.server.domain.user;

import kr.hhplus.be.server.domain.user.entity.User;

public interface UserRepository {

    User getById(long id);

}
