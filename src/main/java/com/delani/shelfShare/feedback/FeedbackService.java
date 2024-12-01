package com.delani.shelfShare.feedback;

import com.delani.shelfShare.book.Book;
import com.delani.shelfShare.book.BookRepository;
import com.delani.shelfShare.common.PageResponse;
import com.delani.shelfShare.exception.OperationNotPermittedException;
import com.delani.shelfShare.user.UserPrincipal;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class FeedbackService {

  @Autowired
  FeedbackRepository feedbackRepository;

  @Autowired
  FeedbackMapper feedbackMapper;

  @Autowired
  BookRepository bookRepository;

  public Integer save(FeedbackRequest feedbackRequest, Authentication currUser) {
    var user = ((UserPrincipal) currUser.getPrincipal()).getUser();

    Book book = bookRepository.findById(feedbackRequest.bookId())
        .orElseThrow(() -> new EntityNotFoundException("No book found with id " + feedbackRequest.bookId()));

    if(!book.isShareable() || book.isArchived()) {
      throw new OperationNotPermittedException("You can't give a review an unsharable or archived book");
    }

    if(Objects.equals(book.getOwner().getId(), user.getId())){
      throw new OperationNotPermittedException("You can't give a feedback to your own book");
    }

    var feedback = feedbackMapper.toFeedback(feedbackRequest);
    return feedbackRepository.save(feedback).getId();
  }

  @Transactional
  public PageResponse<FeedbackResponse> getAllBookFeedbacks
      (int page, int size, int bookId, Authentication currUser) {
    var user = ((UserPrincipal) currUser.getPrincipal()).getUser();
    Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());

    Page<Feedback> pageOfFeedbacks = feedbackRepository.findByFeedbacks(pageable, bookId);

    List<FeedbackResponse> feedbackResponseList = pageOfFeedbacks.stream()
        .map(f -> feedbackMapper.toFeedbackResponse(f, user.getEmail()))
        .toList();

    return new PageResponse<>(
        feedbackResponseList,
        pageOfFeedbacks.getNumber(),
        pageOfFeedbacks.getSize(),
        pageOfFeedbacks.getTotalElements(),
        pageOfFeedbacks.getTotalPages(),
        pageOfFeedbacks.isFirst(),
        pageOfFeedbacks.isLast()
    );
  }
}
