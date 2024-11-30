package com.delani.shelfShare.auth;


import com.delani.shelfShare.email.EmailService;
import com.delani.shelfShare.email.EmailTemplateName;
import com.delani.shelfShare.role.RoleRepository;
import com.delani.shelfShare.security.JwtService;
import com.delani.shelfShare.user.*;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AuthenticationService {

  @Autowired
  AuthenticationRepository authenticationRepository;

  @Autowired
  RoleRepository roleRepository;

  @Autowired
  TokenRepository tokenRepository;

  @Autowired
  EmailService emailService;

  @Autowired
  UserRepository userRepository;

  @Autowired
  AuthenticationManager authenticationManager;

  @Autowired
  JwtService jwtService;


  @Value("${application.mailing.frontend.activation-url}")
  String activationUrl;

  BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

  public void register(RegistrationRequest request) throws MessagingException {
    var roleList = roleRepository.findByName("USER")
        .orElseThrow(() -> new IllegalStateException("Role USER not initialized"));


    User user = User.builder()
        .email(request.getEmail())
        .firstname(request.getFirstname())
        .lastname(request.getLastname())
        .password(encoder.encode(request.getPassword()))
        .accountLocked(false)
        .enabled(false)
        .roleList(List.of(roleList))
        .build();
     authenticationRepository.save(user);
   sendValidationEmail(user);
  }

  public void sendValidationEmail(User user) throws MessagingException {
    var newToken = generateAndSaveActivationToken(user);
    emailService.sendEmail(
        user.getEmail(),
        user.getFullName(),
        EmailTemplateName.ACTIVATE_ACCOUNT,
        activationUrl,
        newToken,
        "Activate Account"
    );
  }

  public String generateAndSaveActivationToken(User user) {
    var code = generateActivationCode(6);
    Token token = Token.builder()
        .token(code)
        .createdAt(LocalDateTime.now())
        .expiresAt(LocalDateTime.now().plusMinutes(15))
        .user(user)
        .build();
    tokenRepository.save(token);
    return code;
  }

  public String generateActivationCode(int length) {
    String characters = "9876543210";
    SecureRandom secureRandom = new SecureRandom();
    StringBuilder stringBuilder = new StringBuilder();

    for(int i = 0; i < length; i++){
      var xyz = secureRandom.nextInt(characters.length());
      stringBuilder.append(characters.charAt(xyz));
    }

    return stringBuilder.toString();
  }

  @Transactional
  public void activateAccount(String token) throws MessagingException {
    Token savedToken = tokenRepository.findByToken(token)
        .orElseThrow(() -> new IllegalArgumentException("Token cannot be found"));

    var user = savedToken.getUser();
    if(LocalDateTime.now().isAfter(savedToken.getExpiresAt())){
      sendValidationEmail(user);
      throw new RuntimeException("Token Expired: Another token has been sent to your mail");
    }

    user.setEnabled(true);
    userRepository.save(user);

    savedToken.setValidatedAt(LocalDateTime.now());
    tokenRepository.save(savedToken);
  }

  public AuthenticationResponse authenticate(AuthenticationRequest request) {
    Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

    User user = userRepository.findByEmail(request.getEmail())
        .orElseThrow(() -> new IllegalArgumentException("User not found"));

    var jwtToken = jwtService.generateToken(user.getEmail());

    return AuthenticationResponse.builder()
        .token(jwtToken)
        .build();
  }
}
