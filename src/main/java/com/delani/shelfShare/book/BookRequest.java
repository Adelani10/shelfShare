package com.delani.shelfShare.book;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;


public record BookRequest (

  Integer id,

  @NotNull(message = "Title can't be empty")
  @NotEmpty(message = "Title can't be empty")
  String title,

  @NotNull(message = "Author can't be empty")
  @NotEmpty(message = "Author can't be empty")
  String author,

  @NotNull(message = "Isbn can't be empty")
  @NotEmpty(message = "Isbn can't be empty")
  String isbn,

  @NotNull(message = "synopsis can't be empty")
  @NotEmpty(message = "synopsis can't be empty")
  String synopsis,

  boolean shareable
){
}
