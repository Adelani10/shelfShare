package com.delani.shelfShare.config;

import com.delani.shelfShare.user.User;
import com.delani.shelfShare.user.UserPrincipal;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public class ApplicationAuditAware implements AuditorAware<String> {
  @Override
  public Optional<String> getCurrentAuditor() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (authentication == null ||
        !authentication.isAuthenticated() ||
        authentication instanceof AnonymousAuthenticationToken) {
      return Optional.empty();
    }

    var user = ((UserPrincipal) authentication.getPrincipal()).getUser();
    return Optional.ofNullable(authentication.getName());
  }
}
