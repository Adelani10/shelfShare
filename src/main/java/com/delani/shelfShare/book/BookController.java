package com.delani.shelfShare.book;


import com.delani.shelfShare.common.PageResponse;
import jakarta.validation.Valid;
import org.hibernate.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("book")
@CrossOrigin
public class BookController {

 @Autowired
 BookService bookService;


  @PostMapping("/save")
  public ResponseEntity<Integer> saveBook
      (@RequestBody @Valid BookRequest bookRequest) {
    return ResponseEntity.ok(bookService.saveBook(bookRequest));
  }

  @GetMapping("/{bookId}")
  public ResponseEntity<BookResponse> getBookById
      (@PathVariable Integer bookId) {
    return ResponseEntity.ok(bookService.getBookById(bookId));
  }

  @GetMapping
  public ResponseEntity<PageResponse<BookResponse>> getAllDisplayableBooks(
      @RequestParam(name = "page", defaultValue = "0", required = false) int page,
      @RequestParam(name = "page", defaultValue = "10", required = false)int size,
      Authentication currUser
  ) {
    return ResponseEntity.ok(bookService.getAllDisplayableBooks(page, size, currUser));
  }

  @GetMapping("/owner")
  public ResponseEntity<PageResponse<BookResponse>> getAllBooksByOwner(
      @RequestParam(name = "page", defaultValue = "0", required = false) int page,
      @RequestParam(name = "page", defaultValue = "10", required = false)int size,
      Authentication currUser
  ) {
    return ResponseEntity.ok(bookService.getAllBooksByOwner(page, size, currUser));
  }

  @GetMapping("/borrowed")
  public ResponseEntity<PageResponse<BorrowedBookResponse>> getAllBorrowedBooks(
      @RequestParam(name = "page", defaultValue = "0", required = false) int page,
      @RequestParam(name = "page", defaultValue = "10", required = false)int size,
      Authentication currUser
  ) {
    return ResponseEntity.ok(bookService.getAllBorrowedBooks(page, size, currUser));
  }

  @GetMapping("/returned")
  public ResponseEntity<PageResponse<BorrowedBookResponse>> getAllReturnedBooks(
      @RequestParam(name = "page", defaultValue = "0", required = false) int page,
      @RequestParam(name = "page", defaultValue = "10", required = false)int size,
      Authentication currUser
  ) {
    return ResponseEntity.ok(bookService.getAllReturnedBooks(page, size, currUser));
  }

  @PatchMapping("/update/shareable/{bookId}")
  public ResponseEntity<Integer> updateShareableStatus
      (@PathVariable int bookId, Authentication currUser) {
    return ResponseEntity.ok(bookService.updateShareableStatus(bookId, currUser));
  }

  @PatchMapping("/archived/{bookId}")
  public ResponseEntity<Integer> updateArchivedStatus
      (@PathVariable int bookId, Authentication currUser) {
    return ResponseEntity.ok(bookService.updateArchivedStatus(bookId, currUser));
  }

  @PostMapping("/borrow/{bookId}")
  public ResponseEntity<Integer> borrowBook
      (@PathVariable int bookId, Authentication currUser) {
    return ResponseEntity.ok(bookService.borrowBook(bookId, currUser));
  }

  @PatchMapping("borrow/return/{bookId}")
  public ResponseEntity<Integer> returnBorrowedBook
      (@PathVariable int bookId, Authentication currUser) {
    return ResponseEntity.ok(bookService.returnBorrowedBook(bookId, currUser));
  }

  @PatchMapping("borrow/return/approve/{bookId}")
  public ResponseEntity<Integer> approveReturnedBook
      (@PathVariable int bookId, Authentication currUser) {
    return ResponseEntity.ok(bookService.approveReturnedBook(bookId, currUser));
  }

  @PostMapping(value = "/cover/{bookId}", consumes = "multipart/form-data")
  public ResponseEntity<?> uploadBookCoverPicture(
      @PathVariable Integer bookId,
      @RequestPart("file") MultipartFile file,
      Authentication connectedUser
  ) {
    bookService.uploadCoverPicture(file, connectedUser, bookId);
    return ResponseEntity.accepted().build();
  }

}


