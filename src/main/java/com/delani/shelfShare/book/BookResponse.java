package com.delani.shelfShare.book;


import com.delani.shelfShare.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookResponse {

  private Integer id;
  private String title;
  private String author;
  private String isbn;
  private String synopsis;
  private User owner;
  private byte[] cover;
  private double rate;
  private boolean archived;
  private boolean shareable;

}
