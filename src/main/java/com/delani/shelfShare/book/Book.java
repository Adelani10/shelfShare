package com.delani.shelfShare.book;

import com.delani.shelfShare.common.BaseEntity;
import com.delani.shelfShare.feedback.Feedback;
import com.delani.shelfShare.history.BookTransactionHistory;
import com.delani.shelfShare.user.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Book extends BaseEntity {
  private String title;
  private String author;
  private String isbn;
  private String synopsis;
  private String bookCover;
  private boolean archived;
  private boolean shareable;
  @ManyToOne
  @JoinColumn(name = "owner_id")
  private User owner;
  @OneToMany(mappedBy = "book")
  private List<Feedback> feedbacks;
  @OneToMany(mappedBy = "book")
  private List<BookTransactionHistory> histories;

  @Transient
  public double getRate() {
    if (feedbacks == null || feedbacks.isEmpty()) {
      return 0.0;
    }
    var rate = this.feedbacks.stream()
        .mapToDouble(Feedback::getNote)
        .average()
        .orElse(0.0);
    double roundedRate = Math.round(rate * 10.0) / 10.0;

    // Return 4.0 if roundedRate is less than 4.5, otherwise return 4.5
    return roundedRate < 4.5 ? 4.0 : 4.5;
  }
}
