package com.example.spring_security.library.api.v1.repository;

import com.example.spring_security.library.api.v1.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    Optional<Book> findByIsbn(String isbn);

    List<Book> findByAuthor(String author);

    List<Book> findByTitleContainingIgnoreCase(String title);

    List<Book> findByGenre(String genre);

    List<Book> findByCreatedBy(String createdBy);

    @Query("SELECT b FROM Book b WHERE b.stockQuantity > 0")
    List<Book> findAvailableBooks();

    boolean existsByIsbn(String isbn);
}
