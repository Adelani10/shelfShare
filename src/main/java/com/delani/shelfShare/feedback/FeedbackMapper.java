package com.delani.shelfShare.feedback;

import com.delani.shelfShare.book.Book;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class FeedbackMapper {

  public Feedback toFeedback(FeedbackRequest feedbackRequest) {
    return Feedback.builder()
        .note(feedbackRequest.note())
        .comment(feedbackRequest.comment())
        .book(Book.builder()
            .id(feedbackRequest.bookId())
            .shareable(false)
            .archived(false)
            .build()
        )
        .build();
  }

  public FeedbackResponse toFeedbackResponse(Feedback feedback, String userId) {
    return FeedbackResponse.builder()
        .note(feedback.getNote())
        .comment(feedback.getComment())
        .ownFeedback(Objects.equals(feedback.getCreatedBy(), userId))
        .build();
  }


}
