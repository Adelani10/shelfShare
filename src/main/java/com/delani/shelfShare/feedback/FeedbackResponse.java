package com.delani.shelfShare.feedback;

import com.delani.shelfShare.book.Book;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FeedbackResponse {

  private double note;
  private String comment;
  private boolean ownFeedback;

}
