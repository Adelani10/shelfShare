package com.delani.shelfShare.auth;


import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("auth")
@CrossOrigin
public class AuthenticationController {

  @Autowired
  AuthenticationService authenticationService;

  @PostMapping("/register")
  @ResponseStatus(HttpStatus.ACCEPTED)
  public ResponseEntity<?> register
      (@RequestBody @Valid RegistrationRequest request) throws MessagingException {
    authenticationService.register(request);
    return ResponseEntity.accepted().build();
  }

  @GetMapping("/activate-account")
  public void activateAccount (@RequestParam String token) throws MessagingException {
    authenticationService.activateAccount(token);
  }

  @PostMapping("/authenticate")
  public ResponseEntity<AuthenticationResponse> authenticate
      (@RequestBody @Valid AuthenticationRequest request) {
    return ResponseEntity.ok(authenticationService.authenticate(request));
  }
}
