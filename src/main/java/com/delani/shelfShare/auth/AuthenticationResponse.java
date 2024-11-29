package com.delani.shelfShare.auth;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class AuthenticationResponse {

  private String token;
}
