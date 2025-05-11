package com.eventty.eventtynextgen.certification;

import com.eventty.eventtynextgen.certification.response.CertificationLoginResponseView;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class CertificationServiceImpl implements CertificationService {

    @Override
    public CertificationLoginResponseView login(String loginId, String password) {
        return null;
    }
}
