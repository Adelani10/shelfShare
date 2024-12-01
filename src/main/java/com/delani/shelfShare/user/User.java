package com.delani.shelfShare.user;

import com.delani.shelfShare.history.BookTransactionHistory;
import com.delani.shelfShare.role.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@SuperBuilder
@Table(name = "_user")
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;
  @Column(unique = true)
  private String email;
  private String firstname;
  private String lastname;
  private String password;
  private LocalDate dateOfBirth;
  private boolean accountLocked;
  private boolean enabled;
  @ManyToMany(fetch = FetchType.EAGER)
  private List<Role> roleList;
  @OneToMany(mappedBy = "user")
  private List<BookTransactionHistory> historyList;

  @CreatedDate
  @Column(nullable = false, updatable = false)
  private LocalDateTime createdDate;

  @LastModifiedDate
  @Column(nullable = false)
  private LocalDateTime lastModifiedDate;

  public String getFullName () {
    return firstname + " " + lastname;
  }

}
