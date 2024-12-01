package com.delani.shelfShare.history;


import com.delani.shelfShare.book.Book;
import com.delani.shelfShare.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookTransactionHistory {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  @ManyToOne
  @JoinColumn(name = "_user_id")
  private User user;
  private String userId;
  @ManyToOne
  @JoinColumn(name = "book_id")
  private Book book;
  private boolean returned;
  private boolean returnApproved;
}
