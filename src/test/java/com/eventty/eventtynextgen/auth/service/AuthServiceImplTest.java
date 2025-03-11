package com.eventty.eventtynextgen.auth.service;

import static org.mockito.Mockito.*;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService 단위 테스트")
class AuthServiceImplTest {

    @InjectMocks
    private AuthService authService;

    @Nested
    class Signup {

        @Test
        @DisplayName("[GOOD] - 회원 가입에 성공합니다.")
        void 회원가입_성공() {
            // given
            AuthUser authUser = new AuthUser();


            // when
            when(authRepository())

            // then
        }
    }

}