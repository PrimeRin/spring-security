package com.example.spring_security.library.api.v1.service;

import com.example.spring_security.library.api.v1.dto.request.BookRequest;
import com.example.spring_security.library.api.v1.dto.response.BookResponse;
import com.example.spring_security.library.api.v1.entity.Book;
import com.example.spring_security.common.exception.ResourceNotFoundException;
import com.example.spring_security.library.api.v1.mapper.BookMapper;
import com.example.spring_security.library.api.v1.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookService {

    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

    @Transactional
    public BookResponse createBook(BookRequest request) {
        validateIsbnUniqueness(request.getIsbn(), null);

        String currentUsername = getCurrentUsername();

        Book book = bookMapper.toEntity(request);
        book.setCreatedBy(currentUsername);
        book.setCreatedAt(LocalDateTime.now());
        book.setUpdatedAt(LocalDateTime.now());

        Book savedBook = bookRepository.save(book);
        return bookMapper.toResponse(savedBook);
    }

    @Transactional(readOnly = true)
    public List<BookResponse> getAllBooks() {
        return bookMapper.toResponseList(bookRepository.findAll());
    }

    @Transactional(readOnly = true)
    public BookResponse getBookById(Long id) {
        Book book = findBookById(id);
        return bookMapper.toResponse(book);
    }

    @Transactional(readOnly = true)
    public List<BookResponse> searchBooks(String title, String author, String genre) {
        List<Book> books;

        if (title != null && !title.isEmpty()) {
            books = bookRepository.findByTitleContainingIgnoreCase(title);
        } else if (author != null && !author.isEmpty()) {
            books = bookRepository.findByAuthor(author);
        } else if (genre != null && !genre.isEmpty()) {
            books = bookRepository.findByGenre(genre);
        } else {
            books = bookRepository.findAll();
        }

        return bookMapper.toResponseList(books);
    }

    @Transactional
    public BookResponse updateBook(Long id, BookRequest request) {
        Book book = findBookById(id);

        if (request.getIsbn() != null && !book.getIsbn().equals(request.getIsbn())) {
            validateIsbnUniqueness(request.getIsbn(), id);
            book.setIsbn(request.getIsbn());
        }

        bookMapper.updateEntity(book, request);
        book.setUpdatedAt(LocalDateTime.now());

        Book updatedBook = bookRepository.save(book);
        return bookMapper.toResponse(updatedBook);
    }

    @Transactional
    public void deleteBook(Long id) {
        Book book = findBookById(id);
        bookRepository.delete(book);
    }

    private void validateIsbnUniqueness(String isbn, Long excludeBookId) {
        if (bookRepository.existsByIsbn(isbn)) {
            if (excludeBookId != null) {
                Optional<Book> existingBook = bookRepository.findByIsbn(isbn);
                if (existingBook.isPresent() && !existingBook.get().getId().equals(excludeBookId)) {
                    throw new RuntimeException("Book with ISBN " + isbn + " already exists");
                }
            } else {
                throw new RuntimeException("Book with ISBN " + isbn + " already exists");
            }
        }
    }

    private Book findBookById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + id));
    }

    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return "anonymous";
        }
        return authentication.getName();
    }
}
