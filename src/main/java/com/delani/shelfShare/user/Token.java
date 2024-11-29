package com.delani.shelfShare.user;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Builder
public class Token {

  @Id
  @GeneratedValue
  private String id;

  @Column(unique = true)
  private String token;

  private LocalDateTime createdAt;
  private LocalDateTime expiresAt;
  private LocalDateTime validatedAt;

  @ManyToOne
  @JoinColumn(name = "userId", nullable = false)
  private User user;
}
