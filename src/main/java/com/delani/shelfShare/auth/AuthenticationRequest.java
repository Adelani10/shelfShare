package com.delani.shelfShare.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthenticationRequest {

  @NotNull(message = "Email is mandatory")
  @NotEmpty(message = "Email is mandatory")
  @Email(message = "Email is not properly formatted")
  private String email;


  @NotNull(message = "Email is mandatory")
  @NotEmpty(message = "Email is mandatory")
  @Size(min = 8, message = "Password must be at least 8 chars long")
  private String password;
}
