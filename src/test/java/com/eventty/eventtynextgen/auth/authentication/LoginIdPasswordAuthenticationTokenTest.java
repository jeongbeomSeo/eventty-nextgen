package com.eventty.eventtynextgen.auth.authentication;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.eventty.eventtynextgen.auth.core.Authentication;
import com.eventty.eventtynextgen.auth.core.GrantedAuthority;
import com.eventty.eventtynextgen.auth.core.autority.SimpleGrantedAuthority;
import com.eventty.eventtynextgen.auth.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.Collections;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("Authentication 생성 단위 테스트")
class LoginIdPasswordAuthenticationTokenTest {

    @Nested
    @DisplayName("unauthenticated 팩토리 메서드 테스트")
    class Unauthenticated {

        @Test
        @DisplayName("사용자 인자에 null이 들어올 경우 미인증 객체 생성에 실패한다")
        void 사용자_인자에_null이_들어올_경우_미인증_객체_생성에_실패한다() {
            // given
            UserDetails userDetails = null;

            // when & then
            assertThatThrownBy(() ->
                LoginIdPasswordAuthenticationToken.authenticated(userDetails))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("userDetails must not be null");
        }
    }

    @Nested
    @DisplayName("authenticated 팩토리 메서드 테스트")
    class Authenticated {

        @Test
        @DisplayName("사용자 신원 증명이 되어 있는 경우 인증 객체 생성에 성공한다.")
        void 사용자_신원_증명이_되어_있는_경우_인증_객체_생성에_성공한다() {
            // given
            UserDetails userDetails = mock(UserDetails.class);

            when(userDetails.isIdentified()).thenReturn(true);

            // when
            Authentication authentication = LoginIdPasswordAuthenticationToken.authenticated(userDetails);

            // then
            assertThat(authentication.isAuthenticated()).isTrue();
        }

        @Test
        @DisplayName("사용자 인자에 null이 들어올 경우 인증 객체 생성에 실패한다")
        void 사용자_인자에_null이_들어올_경우_인증_객체_생성에_실패한다() {
            // given
            UserDetails userDetails = null;

            // when & then
            assertThatThrownBy(() ->
                LoginIdPasswordAuthenticationToken.authenticated(userDetails))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("userDetails must not be null");
        }

        @Test
        @DisplayName("사용자의 신원 증명이 되지 않은 경우 인증 객체 생성에 실패한다.")
        void 사용자_신원_증명이_되지_않은_경우_인증_객체_생성에_실패한다() {
            // given
            UserDetails userDetails = mock(UserDetails.class);

            when(userDetails.isIdentified()).thenReturn(false);

            // when & then
            assertThatThrownBy(() ->
                LoginIdPasswordAuthenticationToken.authenticated(userDetails))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("An unidentified user cannot create a token.");
        }
    }

    @Nested
    @DisplayName("authorized 팩토리 메서드 테스트")
    class Authorized {

        @Test
        @DisplayName("인증되어 있는 객체와 권한들이 인자로 들어올 경우 권한이 할당된 객체 생성에 성공한다")
        void 인증되어_있는_객체와_권한들이_인자로_들어올_경우_권한이_할당된_객체_생성에_성공한다() {
            // given
            Authentication authentication = mock(Authentication.class);
            UserDetails userDetails = mock(UserDetails.class);
            when(userDetails.isIdentified()).thenReturn(true);
            when(authentication.getUserDetails()).thenReturn(userDetails);
            when(authentication.isAuthenticated()).thenReturn(true);

            Collection<? extends GrantedAuthority> authorities = Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"));

            // when
            Authentication authorized = LoginIdPasswordAuthenticationToken.authorized(authentication, authorities);

            // then
            assertThat(authorized.isAuthenticated()).isEqualTo(true);
            assertThat(authorized.getAuthorities().isEmpty()).isFalse();
        }

        @Test
        @DisplayName("인증되어 있지 않은 객체가 들어올 경우 권한이 할당된 객체 생성에 실패한다")
        void 인증되어_있지_않은_객체가_들어올_경우_권한이_할당된_객체_생성에_실패한다() {
            Authentication authentication = mock(Authentication.class);
            when(authentication.isAuthenticated()).thenReturn(false);

            Collection<? extends GrantedAuthority> authorities = Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"));

            // when & then
            assertThatThrownBy(() ->
                LoginIdPasswordAuthenticationToken.authorized(authentication, authorities))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Only an authenticated user can create an authorized token.");
        }

        @Test
        @DisplayName("권한 인자에 null이 들어올 경우 권한이 할당된 객체 생성에 실패한다")
        void 권한_인자에_null이_들어올_경우_권한이_할당된_객체_생성에_실패한다() {
            Authentication authentication = null;

            Collection<? extends GrantedAuthority> authorities = Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"));

            // when & then
            assertThatThrownBy(() ->
                LoginIdPasswordAuthenticationToken.authorized(authentication, authorities))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("authentication must not be null");
        }

        @Test
        @DisplayName("이미 권한 할당에 성공한 객체는 권한 할당된 객체 생성에 실패한다")
        void 권한_인자에_빈_Collection이_들어올_경우_권한이_할당된_객체_생성에_실패한다() {
            // given
            Authentication authentication = mock(Authentication.class);
            when(authentication.isAuthenticated()).thenReturn(true);

            Collection<GrantedAuthority> auths =
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"));
            doReturn(auths).when(authentication).getAuthorities();

            Collection<GrantedAuthority> authorities = Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"));

            // when & then
            assertThatThrownBy(() ->
                LoginIdPasswordAuthenticationToken.authorized(authentication, authorities))
                .isInstanceOf(IllegalArgumentException.class);
        }
    }


}