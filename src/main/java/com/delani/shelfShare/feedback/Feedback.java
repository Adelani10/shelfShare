package com.delani.shelfShare.feedback;


import com.delani.shelfShare.book.Book;
import com.delani.shelfShare.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Feedback extends BaseEntity {
  @Column
  private Double note;
  private String comment;
  @ManyToOne
  @JoinColumn(name = "book_id")
  private Book book;
}
