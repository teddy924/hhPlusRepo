package kr.hhplus.be.server.domain.user.entity;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.account.entity.Account;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String email;

    private String password;

    private String name;

    private String phone;

    private LocalDateTime sysCretDt;

    private LocalDateTime sysChgDt;

    @OneToOne(mappedBy = "user")
    private Account account;

    public static User withId(Long id) {
        return User.builder().id(id).build();
    }
}