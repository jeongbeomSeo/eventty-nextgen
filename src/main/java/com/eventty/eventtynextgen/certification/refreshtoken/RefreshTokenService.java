package com.eventty.eventtynextgen.certification.refreshtoken;


import com.eventty.eventtynextgen.certification.refreshtoken.entity.RefreshToken;
import com.eventty.eventtynextgen.shared.exception.CustomException;
import com.eventty.eventtynextgen.shared.exception.enums.CertificationErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public RefreshToken saveOrUpdate(String refreshToken, Long userId) {
        return refreshTokenRepository.findByUserId(userId)
            .map(token -> {
                token.updateRefreshToken(refreshToken);
                return token;
            })
            .orElseGet(() -> refreshTokenRepository.save(RefreshToken.of(refreshToken, userId)));
    }

    @Transactional
    public void delete(Long userId) {
        refreshTokenRepository.findByUserId(userId)
            .ifPresent(refreshTokenRepository::delete);
    }

    public RefreshToken getRefreshToken(Long userId) {
        return refreshTokenRepository.findByUserId(userId)
            .orElseThrow(() -> CustomException.badRequest(CertificationErrorType.NOT_FOUND_REFRESH_TOKEN));
    }
}
