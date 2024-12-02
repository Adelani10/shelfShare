package com.delani.shelfShare.book;


import com.delani.shelfShare.file.FileUtils;
import com.delani.shelfShare.history.BookTransactionHistory;
import com.delani.shelfShare.user.User;
import org.springframework.stereotype.Service;


@Service
public class BookMapper {

  public Book toBook(BookRequest bookRequest) {
    return Book.builder()
        .title(bookRequest.title())
        .author(bookRequest.author())
        .isbn(bookRequest.isbn())
        .synopsis(bookRequest.synopsis())
        .archived(false)
        .shareable(bookRequest.shareable())
        .build();
  }

  public BookResponse toBookResponse (Book book) {
    return BookResponse.builder()
        .id(book.getId())
        .title(book.getTitle())
        .author(book.getAuthor())
        .isbn(book.getIsbn())
        .synopsis(book.getSynopsis())
        .archived(book.isArchived())
        .shareable(book.isShareable())
        .cover(FileUtils.readFileFromLocation(book.getBookCover()))
        .owner(book.getOwner())
        .rate(book.getRate())
        .build();
  }

  public BorrowedBookResponse toBorrowedBookResponse(BookTransactionHistory bookTransactionHistory) {
    return BorrowedBookResponse.builder()
        .id(bookTransactionHistory.getBook().getId())
        .title(bookTransactionHistory.getBook().getTitle())
        .author(bookTransactionHistory.getBook().getAuthor())
        .isbn(bookTransactionHistory.getBook().getIsbn())
        .rate(bookTransactionHistory.getBook().getRate())
        .returned(bookTransactionHistory.isReturned())
        .returnApproved(bookTransactionHistory.isReturnApproved())
        .build();
  }
}
