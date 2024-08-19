package com.techTask.library.data.repositories;

import com.techTask.library.data.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    Optional<Book> findByTitleAndAuthor(String title, String author);

    @Modifying
    @Query(value = "DELETE FROM manage_library WHERE book_ID = :bookId AND member_ID = :memberId", nativeQuery = true)
    void deleteBookFromMember(@Param("bookId") Long bookId, @Param("memberId") Long memberId);

    @Modifying
    @Query(value = "INSERT INTO manage_library (book_ID, member_ID) VALUES (:bookId, :memberId)", nativeQuery = true)
    void addBookToMember(@Param("bookId") Long bookId, @Param("memberId") Long memberId);
}
