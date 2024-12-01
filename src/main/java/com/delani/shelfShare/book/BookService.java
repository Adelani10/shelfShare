package com.delani.shelfShare.book;

import com.delani.shelfShare.common.PageResponse;
import com.delani.shelfShare.exception.OperationNotPermittedException;
import com.delani.shelfShare.file.FileStorageService;
import com.delani.shelfShare.history.BookTransactionHistory;
import com.delani.shelfShare.history.BookTransactionHistoryRepository;
import com.delani.shelfShare.user.UserPrincipal;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;

import static com.delani.shelfShare.book.BookSpecification.withOwnerId;

@Service
public class BookService {
  @Autowired
  BookRepository bookRepository;

  @Autowired
  BookMapper bookMapper;
  
  @Autowired
  BookTransactionHistoryRepository bookTransactionHistoryRepository;

  @Autowired
  FileStorageService fileStorageService;

  public Integer saveBook(BookRequest bookRequest) {
    var book = bookMapper.toBook(bookRequest);
    return bookRepository.save(book).getId();
  }

  public BookResponse getBookById(Integer bookId) {
        return bookRepository.findById(bookId)
            .map(bookMapper::toBookResponse)
            .orElseThrow(() -> new IllegalStateException("No book with that Id in the database"));
  }

  public PageResponse<BookResponse> getAllDisplayableBooks(int page, int size, Authentication currUser) {
    var user = ((UserPrincipal) currUser.getPrincipal()).getUser();

    Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
    Page<Book> allDisplayableBooks = bookRepository.findAllDisplayableBooks(pageable, user.getEmail());
    List<BookResponse> books = allDisplayableBooks.stream()
        .map(bookMapper::toBookResponse)
        .toList();

    return new PageResponse<>(
        books, 
        allDisplayableBooks.getNumber(), 
        allDisplayableBooks.getSize(),
        allDisplayableBooks.getTotalElements(),
        allDisplayableBooks.getTotalPages(),
        allDisplayableBooks.isFirst(),
        allDisplayableBooks.isLast()
    );
    
  }

  public PageResponse<BookResponse> getAllBooksByOwner(int page, int size, Authentication currUser) {
    var user = ((UserPrincipal) currUser.getPrincipal()).getUser();
    
    Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
    Page<Book> allBooksByOwner = bookRepository.findAll(withOwnerId(user.getEmail()), pageable);
    List<BookResponse> bookResponses = allBooksByOwner.stream()
            .map(bookMapper::toBookResponse)
            .toList();
    return new PageResponse<>(
        bookResponses,
        allBooksByOwner.getNumber(),
        allBooksByOwner.getSize(),
        allBooksByOwner.getTotalElements(),
        allBooksByOwner.getTotalPages(),
        allBooksByOwner.isFirst(),
        allBooksByOwner.isLast()
    );
  }
  
  public PageResponse<BorrowedBookResponse> getAllBorrowedBooks(int page, int size, Authentication currUser) {
    var user = ((UserPrincipal) currUser.getPrincipal()).getUser();
    Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
    Page<BookTransactionHistory> pageOfBooks = bookTransactionHistoryRepository
        .findAllBorrowedBooks(pageable, user.getEmail());

    List<BorrowedBookResponse> borrowedBookResponses = pageOfBooks.stream()
        .map(bookMapper::toBorrowedBookResponse)
        .toList();

    return new PageResponse<>(
        borrowedBookResponses,
        pageOfBooks.getNumber(),
        pageOfBooks.getSize(),
        pageOfBooks.getTotalElements(),
        pageOfBooks.getTotalPages(),
        pageOfBooks.isFirst(),
        pageOfBooks.isLast()
    );
  }

  public PageResponse<BorrowedBookResponse> getAllReturnedBooks(int page, int size, Authentication currUser) {
    var user = ((UserPrincipal) currUser.getPrincipal()).getUser();
    Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());

    Page<BookTransactionHistory> pageOfBooks = bookTransactionHistoryRepository
        .findAllReturnedBooks(pageable, user.getEmail());

    List<BorrowedBookResponse> booksResponse = pageOfBooks.stream()
        .map(bookMapper::toBorrowedBookResponse)
        .toList();
    return new PageResponse<>(
        booksResponse,
        pageOfBooks.getNumber(),
        pageOfBooks.getSize(),
        pageOfBooks.getTotalElements(),
        pageOfBooks.getTotalPages(),
        pageOfBooks.isFirst(),
        pageOfBooks.isLast()
    );
  }

  public Integer updateShareableStatus(int bookId, Authentication currUser) {
    var user = ((UserPrincipal) currUser.getPrincipal()).getUser();

    Book book = bookRepository.findById(bookId)
        .orElseThrow(() -> new EntityNotFoundException("Book with Id:" + bookId + "not found"));

    if(!Objects.equals(book.getOwner().getId(), user.getId())) {
      throw new OperationNotPermittedException("Can't update book that isn't yours");
    }
    book.setShareable(!book.isShareable());
    bookRepository.save(book);
    return bookId;

  }

  public Integer updateArchivedStatus(int bookId, Authentication currUser) {
    var user = ((UserPrincipal) currUser.getPrincipal()).getUser();

    Book book = bookRepository.findById(bookId)
        .orElseThrow(() -> new EntityNotFoundException("Book with Id:" + bookId + "not found"));

    if(Objects.equals(book.getOwner().getId(), user.getId())) {
      throw new OperationNotPermittedException("Can't update book that isn't yours");
    }
    book.setArchived(!book.isArchived());
    bookRepository.save(book);
    return bookId;

  }

  public Integer borrowBook (int bookId, Authentication currUser)  {
    var user = ((UserPrincipal) currUser.getPrincipal()).getUser();

    Book book = bookRepository.findById(bookId)
        .orElseThrow(() -> new EntityNotFoundException("Book with Id:" + bookId + "not found"));

    if (book.isArchived() || !book.isShareable()) {
      throw new OperationNotPermittedException("Book is archived or not shareable");
    }

    if(Objects.equals(user.getId(), book.getOwner().getId())) {
      throw new OperationNotPermittedException("You cannot borrow your own book");
    }

    boolean isAlreadyBorrowedByYou = bookTransactionHistoryRepository.isAlreadyBorrowedByYou(bookId, user.getEmail());
    if (isAlreadyBorrowedByYou) {
      throw new OperationNotPermittedException("You already borrowed this book");
    }

    boolean isAlreadyBorrowed = bookTransactionHistoryRepository.isAlreadyBorrowed(bookId, user.getEmail());
    if (isAlreadyBorrowed) {
      throw new OperationNotPermittedException("The book is already borrowed");
    }

    BookTransactionHistory history = BookTransactionHistory.builder()
        .user(user)
        .userId(book.getOwner().getEmail())
        .book(book)
        .returned(false)
        .returnApproved(false)
        .build();
    bookTransactionHistoryRepository.save(history);
    return bookId;
  }

  public Integer returnBorrowedBook(int bookId, Authentication currUser) {
    var user = ((UserPrincipal) currUser.getPrincipal()).getUser();

    Book book = bookRepository.findById(bookId)
        .orElseThrow(() -> new EntityNotFoundException("Book with Id:" + bookId + "not found"));

    if (book.isArchived() || !book.isShareable()) {
      throw new OperationNotPermittedException("Book is archived or not shareable");
    }

    if(Objects.equals(user.getId(), book.getOwner().getId())) {
      throw new OperationNotPermittedException("You cannot return your own book");
    }

    BookTransactionHistory bookTransactionHistory = bookTransactionHistoryRepository.findByUserIdAndBookId
            (bookId, user.getEmail()).orElseThrow(() -> new EntityNotFoundException("No history found"));
    bookTransactionHistory.setReturned(true);
    bookTransactionHistoryRepository.save(bookTransactionHistory);
    return bookId;
  }

  public Integer approveReturnedBook(int bookId, Authentication currUser) {
    var user = ((UserPrincipal) currUser.getPrincipal()).getUser();

    Book book = bookRepository.findById(bookId)
        .orElseThrow(() -> new EntityNotFoundException("Book with Id:" + bookId + "not found"));

    if (book.isArchived() || !book.isShareable()) {
      throw new OperationNotPermittedException("Book is archived or not shareable");
    }

    if(!Objects.equals(user.getId(), book.getOwner().getId())) {
      throw new OperationNotPermittedException("Only owners can approve return");
    }

    BookTransactionHistory bookTransactionHistory = bookTransactionHistoryRepository.findByOwnerIdAndBookId
        (bookId, user.getEmail()).orElseThrow(() -> new EntityNotFoundException("No history found"));
    bookTransactionHistory.setReturnApproved(true);
    bookTransactionHistoryRepository.save(bookTransactionHistory);
    return bookId;
  }

  public void uploadCoverPicture(MultipartFile file, Authentication currUser, int bookId) {
    var user = ((UserPrincipal) currUser.getPrincipal()).getUser();
    Book book = bookRepository.findById(bookId)
        .orElseThrow(() -> new EntityNotFoundException("Book with Id:" + bookId + "not found"));
    var profilePicture = fileStorageService.saveFile(file, user.getEmail());
    book.setBookCover(profilePicture);
    bookRepository.save(book);
  }
}
