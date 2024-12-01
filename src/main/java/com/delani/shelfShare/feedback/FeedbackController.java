package com.delani.shelfShare.feedback;


import com.delani.shelfShare.common.PageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("feedback")
@CrossOrigin
public class FeedbackController {

  @Autowired
  FeedbackService feedbackService;

  @PostMapping("/save")
  public ResponseEntity<Integer> save
      (@RequestBody FeedbackRequest feedbackRequest, Authentication currUser) {
    feedbackService.save(feedbackRequest, currUser);
    return ResponseEntity.accepted().build();
  }

  @GetMapping("/book/{bookId}")
  public ResponseEntity<PageResponse<FeedbackResponse>> getAllBookFeedbacks
      (@RequestParam(name = "page", defaultValue = "0", required = false) int page,
       @RequestParam(name = "size", defaultValue = "10", required = false) int size,
       @PathVariable int bookId,
       Authentication currUser) {
    return ResponseEntity.ok(feedbackService.getAllBookFeedbacks(page, size, bookId, currUser));
  }

}
