package com.eventty.eventtynextgen.certification;

import com.eventty.eventtynextgen.certification.authentication.AuthenticationProvider;
import com.eventty.eventtynextgen.certification.core.JwtTokenProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("CertificationServiceImpl 단위 테스트")
public class CertificationServiceImplTest {

    @Mock
    private AuthenticationProvider authenticationProvider;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

}
