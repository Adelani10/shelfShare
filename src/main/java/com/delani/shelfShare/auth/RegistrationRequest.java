package com.delani.shelfShare.auth;


import io.jsonwebtoken.security.Password;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class RegistrationRequest {

  @NotEmpty(message = "FirstName cannot be empty")
  @NotNull(message = "FirstName cannot be null")
  private String firstName;

  @NotEmpty(message = "LastName cannot be empty")
  @NotNull(message = "LastName cannot be null")
  private  String lastName;

  @NotEmpty(message = "Email cannot be empty")
  @NotNull(message = "Email cannot be null")
  @Column(unique = true)
  @Email(message = "Email is not properly formatted")
  private String email;

  @NotEmpty(message = "Password cannot be empty")
  @NotNull(message = "Password cannot be null")
  @Size(min = 8, message = "Passwords mus be at least 8 chars long")
  private String password;

}
