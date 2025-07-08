package com.eventty.eventtynextgen.auth.refreshtoken;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.eventty.eventtynextgen.auth.refreshtoken.entity.RefreshToken;
import java.util.Date;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("RefreshTokenServiceTest 단위 테스트")
class RefreshTokenServiceTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Nested
    @DisplayName("saveOrUpdate 테스트")
    class SaveOrUpdate {

        @Test
        @DisplayName("저장되어 있는 RefreshToken이 존재하지 않을 경우 저장한다.")
        void 저장되어_있는_RefreshToken이_존재하지_않을_경우_저장한다() {
            // given
            String refreshToken = "saved_refresh_token";
            Long userId = 1L;
            Date expiredAt = new Date(System.currentTimeMillis() + 1000L * 60 * 7);

            when(refreshTokenRepository.findByUserId(userId)).thenReturn(Optional.empty());

            RefreshTokenService refreshTokenService = new RefreshTokenService(refreshTokenRepository);

            // when
            refreshTokenService.saveOrUpdate(refreshToken, userId, expiredAt);

            // then
            verify(refreshTokenRepository, times(1)).save(any(RefreshToken.class));
        }

        @Test
        @DisplayName("RefreshToken이 저장되어 있지 않은 경우 업데이트를 한다.")
        void RefreshToken이_저장되어_있지_않은_경우_업데이트를_한다() {
            // given
            String refreshTokenValue = "saved_refresh_token";
            Long userId = 1L;
            Date expiredAt = new Date(System.currentTimeMillis() + 1000L * 60 * 7);
            RefreshToken refreshToken = mock(RefreshToken.class);

            when(refreshTokenRepository.findByUserId(userId)).thenReturn(Optional.of(refreshToken));

            RefreshTokenService refreshTokenService = new RefreshTokenService(refreshTokenRepository);

            // when
            refreshTokenService.saveOrUpdate(refreshTokenValue, userId, expiredAt);

            // then
            verify(refreshToken, times(1)).updateRefreshToken(refreshTokenValue);
        }

    }

}