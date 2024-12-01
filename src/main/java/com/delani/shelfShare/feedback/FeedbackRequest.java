package com.delani.shelfShare.feedback;

import jakarta.validation.constraints.*;

public record FeedbackRequest(

  @Positive(message = "Note must be positive")
  @Min(value = 0, message = "Note shouldn't be less than 0")
  @Max(value = 5, message = "Shouldn't be more than 5")
  Double note,

  @NotNull(message = "Comment is mandatory")
  @NotEmpty(message = "Comment is mandatory")
  @NotBlank(message = "Comment is mandatory")
  String comment,

  @NotNull(message = "BookId is mandatory")
  Integer bookId
) {}
