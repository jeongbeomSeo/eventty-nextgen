package com.eventty.eventtynextgen.config.provider;

import com.eventty.eventtynextgen.shared.context.AuthorizationContext;
import com.eventty.eventtynextgen.shared.context.AuthorizationContextHolder;
import java.util.Optional;
import org.springframework.data.domain.AuditorAware;

public class UserContextAuditorProvider implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        AuthorizationContext context = AuthorizationContextHolder.getContext();
        String userId = null;
        if (context.getUserId() != null) {
            userId = context.getUserId().toString();
        }

        return Optional.ofNullable(userId);
    }
}
