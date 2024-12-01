package com.delani.shelfShare.history;

import com.delani.shelfShare.book.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookTransactionHistoryRepository extends JpaRepository<BookTransactionHistory, Integer> {


  @Query("""
      SELECT history
      FROM BookTransactionHistory history
      WHERE history.userId = :userId
      """)
  Page<BookTransactionHistory> findAllBorrowedBooks(Pageable pageable, String userId);

  @Query("""
      SELECT history
      FROM BookTransactionHistory history
      WHERE history.book.createdBy = :userId
      """)
  Page<BookTransactionHistory> findAllReturnedBooks(Pageable pageable, String userId);

  @Query("""
      SELECT
      (COUNT (*) > 0) As IsBorrowed
      FROM BookTransactionHistory history
      WHERE history.userId = :userId
      AND history.book.id = :bookId
      AND history.returnApproved = false
      """)
  boolean isAlreadyBorrowedByYou(@Param("bookId") int bookId, @Param("userId") String userId);


  @Query("""
      SELECT
      (COUNT (*) > 0) As IsBorrowed
      FROM BookTransactionHistory history
      WHERE history.book.id = :bookId
      AND history.returnApproved = false
      """)
  boolean isAlreadyBorrowed(int bookId, String email);

  @Query("""
      SELECT history
      FROM BookTransactionHistory history
      WHERE history.userId = :userId
      AND history.book.id = :bookId
      AND history.returned = false
      AND history.returnApproved = false
      """)
  Optional<BookTransactionHistory> findByUserIdAndBookId(@Param("bookId") int bookId, @Param("userId") String userId);

  @Query("""
      SELECT history
      FROM BookTransactionHistory history
      WHERE history.book.createdBy = :userId
      AND history.book.id = :bookId
      AND history.returned = true
      AND history.returnApproved = false
      """)
  Optional<BookTransactionHistory> findByOwnerIdAndBookId(@Param("bookId") int bookId, @Param("userId") String userId);
}
