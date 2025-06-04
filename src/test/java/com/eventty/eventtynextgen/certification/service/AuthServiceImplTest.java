package com.eventty.eventtynextgen.certification.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.eventty.eventtynextgen.certification.authentication.AuthenticationProvider;
import com.eventty.eventtynextgen.certification.authorization.AuthorizationProvider;
import com.eventty.eventtynextgen.certification.authorization.enums.AuthorizationType;
import com.eventty.eventtynextgen.certification.core.GrantedAuthority;
import com.eventty.eventtynextgen.certification.core.autority.SimpleGrantedAuthority;
import com.eventty.eventtynextgen.shared.component.user.UserComponent;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthServiceImpl 단위 테스트")
class AuthServiceImplTest {

    @Mock
    private AuthenticationProvider authenticationProvider;

    @Mock
    private AuthorizationProvider authorizationProvider;

    @Mock
    private UserComponent userComponent;

    @Nested
    @DisplayName("권한 Join 처리")
    class JoinAuthorities {

        @Test
        @DisplayName("권한이 올바르게 담겨있을 경우 Join 처리에 성공한다.")
        void 권한이_올바르게_담겨있을_경우_JOIN_처리에_성공한다() {
            // given
            List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(AuthorizationType.ROLE_HOST.name()));

            AuthServiceImpl authService = new AuthServiceImpl(authenticationProvider, authorizationProvider, userComponent);

            // when
            String joinedAuthorities = authService.joinAuthorities(authorities);

            // then
            assertThat(joinedAuthorities).isEqualTo("ROLE_HOST");
        }

        @Test
        @DisplayName("권한이 비어있을 경우 Join 처리에 실패하고 null을 반환한다.")
        void 권한이_비어있을_경우_JOIN_처리에_실패하고_null을_반환한다() {
            List<GrantedAuthority> authorities = Collections.emptyList();

            AuthServiceImpl authService = new AuthServiceImpl(authenticationProvider, authorizationProvider, userComponent);

            // when
            String joinedAuthorities = authService.joinAuthorities(authorities);

            // then
            assertThat(joinedAuthorities).isNull();
        }

    }
}