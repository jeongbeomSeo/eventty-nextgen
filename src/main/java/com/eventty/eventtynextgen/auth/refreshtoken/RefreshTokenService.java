package com.eventty.eventtynextgen.auth.refreshtoken;


import com.eventty.eventtynextgen.auth.refreshtoken.entity.RefreshToken;
import com.eventty.eventtynextgen.shared.exception.CustomException;
import com.eventty.eventtynextgen.shared.exception.enums.AuthErrorType;
import com.eventty.eventtynextgen.shared.utils.DateUtils;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public RefreshToken saveOrUpdate(String refreshToken, Long userId, Date expiredAt) {
        return refreshTokenRepository.findByUserId(userId)
            .map(token -> {
                token.updateRefreshToken(refreshToken);
                return token;
            })
            .orElseGet(() -> refreshTokenRepository.save(RefreshToken.of(refreshToken, userId, DateUtils.convertFormatToLocalDateTime(expiredAt))));
    }

    @Transactional
    public void delete(Long userId) {
        refreshTokenRepository.findByUserId(userId)
            .ifPresent(refreshTokenRepository::delete);
    }

    public RefreshToken getRefreshToken(Long userId) {
        return refreshTokenRepository.findByUserId(userId)
            .orElseThrow(() -> CustomException.badRequest(AuthErrorType.NOT_FOUND_REFRESH_TOKEN));
    }
}
