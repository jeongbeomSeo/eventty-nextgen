package com.eventty.eventtynextgen.auth.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.eventty.eventtynextgen.auth.model.UserRole;
import com.eventty.eventtynextgen.auth.model.entity.AuthUser;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;

@DataJpaTest
@DisplayName("JPA Auth Repository 단위 테스트")
class JpaAuthRepositoryTest {

    @Autowired
    private JpaAuthRepository jpaAuthRepository;

    @Test
    @Transactional
    @DisplayName("[GOOD] - authUser 저장에 성공한다.")
    void AUTH_USER_저장에_성공() {
        // given
        AuthUser authUser = new AuthUser("email@mm.mm", "12345678", UserRole.USER);

        // when
        AuthUser save = jpaAuthRepository.save(authUser);

        // then
        assertThat(save).isNotNull();
        assertThat(save.getId()).isNotNull();
    }
}