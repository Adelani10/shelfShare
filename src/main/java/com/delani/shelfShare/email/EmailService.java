package com.delani.shelfShare.email;


import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.codec.Utf8;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@Data
public class EmailService {

  @Autowired
  JavaMailSender mailSender;

  @Autowired
  TemplateEngine templateEngine;

  @Value("${spring.mail.username}")
  String mailFrom;

  @Async
  public void sendEmail(
      String to,
      String username,
      EmailTemplateName emailTemplateName,
      String confirmationUrl,
      String activationCode,
      String subject
  ) throws MessagingException {
    String templateName = emailTemplateName == null ? "confirm-email" : emailTemplateName.name();

    MimeMessage mimeMessage = mailSender.createMimeMessage();
    MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(
        mimeMessage, MimeMessageHelper.MULTIPART_MODE_MIXED, StandardCharsets.UTF_8.name()
    );
    mimeMessageHelper.setTo(to);
    mimeMessageHelper.setFrom(mailFrom);
    mimeMessageHelper.setSubject(subject);

    Map<String, Object> properties = new HashMap<>();
    properties.put("username", username);
    properties.put("confirmationUrl", confirmationUrl);
    properties.put("activationCode", activationCode);

    Context context = new Context();
    context.setVariables(properties);

    var template = templateEngine.process(templateName, context);
    mimeMessageHelper.setText(template);

    mailSender.send(mimeMessage);

  }
}
