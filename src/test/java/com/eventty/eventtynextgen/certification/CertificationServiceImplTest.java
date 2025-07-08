package com.eventty.eventtynextgen.certification;

import com.eventty.eventtynextgen.base.manager.AppAuthorizationManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("CertificationServiceImpl 단위 테스트")
class CertificationServiceImplTest {

    @Mock
    private AppAuthorizationManager appAuthorizationManager;
}