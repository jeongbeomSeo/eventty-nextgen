package com.eventty.eventtynextgen.config.provider;

import com.eventty.eventtynextgen.shared.context.SessionContext;
import com.eventty.eventtynextgen.shared.context.SessionContextHolder;
import java.util.Optional;
import org.springframework.data.domain.AuditorAware;

public class SessionContextAuditorProvider implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        SessionContext context = SessionContextHolder.getContext();
        String userId = null;
        if (context.getUserId() != null) {
            userId = context.getUserId().toString();
        }

        return Optional.ofNullable(userId);
    }
}
