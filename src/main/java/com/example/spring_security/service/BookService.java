package com.example.spring_security.service;

import com.example.spring_security.dto.request.BookRequest;
import com.example.spring_security.dto.response.BookResponse;
import com.example.spring_security.entity.Book;
import com.example.spring_security.exception.ResourceNotFoundException;
import com.example.spring_security.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookService {

    private final BookRepository bookRepository;

    @Transactional
    public BookResponse createBook(BookRequest request) {
        // Check if ISBN already exists
        if (bookRepository.existsByIsbn(request.getIsbn())) {
            throw new RuntimeException("Book with ISBN " + request.getIsbn() + " already exists");
        }

        // Get current authenticated username
        String currentUsername = getCurrentUsername();

        Book book = new Book();
        book.setTitle(request.getTitle());
        book.setAuthor(request.getAuthor());
        book.setIsbn(request.getIsbn());
        book.setPublishedYear(request.getPublishedYear());
        book.setGenre(request.getGenre());
        book.setDescription(request.getDescription());
        book.setPrice(request.getPrice());
        book.setStockQuantity(request.getStockQuantity());
        book.setCreatedBy(currentUsername);

        Book savedBook = bookRepository.save(book);
        log.info("Book created: {} by {}", savedBook.getTitle(), currentUsername);

        return convertToResponse(savedBook);
    }

    @Transactional(readOnly = true)
    public List<BookResponse> getAllBooks() {
        return bookRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public BookResponse getBookById(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + id));
        return convertToResponse(book);
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

        return books.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public BookResponse updateBook(Long id, BookRequest request) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + id));

        // Update only fields that are provided (not null)
        if (request.getTitle() != null) book.setTitle(request.getTitle());
        if (request.getAuthor() != null) book.setAuthor(request.getAuthor());
        if (request.getIsbn() != null) {
            // Check if new ISBN conflicts with another book
            if (!book.getIsbn().equals(request.getIsbn()) &&
                    bookRepository.existsByIsbn(request.getIsbn())) {
                throw new RuntimeException("Book with ISBN " + request.getIsbn() + " already exists");
            }
            book.setIsbn(request.getIsbn());
        }
        if (request.getPublishedYear() != null) book.setPublishedYear(request.getPublishedYear());
        if (request.getGenre() != null) book.setGenre(request.getGenre());
        if (request.getDescription() != null) book.setDescription(request.getDescription());
        if (request.getPrice() != null) book.setPrice(request.getPrice());
        if (request.getStockQuantity() != null) book.setStockQuantity(request.getStockQuantity());

        Book updatedBook = bookRepository.save(book);
        log.info("Book updated: {} by {}", updatedBook.getTitle(), getCurrentUsername());

        return convertToResponse(updatedBook);
    }

    @Transactional
    public void deleteBook(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + id));

        bookRepository.delete(book);
        log.info("Book deleted: {} by {}", book.getTitle(), getCurrentUsername());
    }

    // Helper methods
    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return "anonymous";
        }
        return authentication.getName();
    }

    private BookResponse convertToResponse(Book book) {
        return BookResponse.builder()
                .id(book.getId())
                .title(book.getTitle())
                .author(book.getAuthor())
                .isbn(book.getIsbn())
                .publishedYear(book.getPublishedYear())
                .genre(book.getGenre())
                .description(book.getDescription())
                .price(book.getPrice())
                .stockQuantity(book.getStockQuantity())
                .createdBy(book.getCreatedBy())
                .createdAt(book.getCreatedAt())
                .updatedAt(book.getUpdatedAt())
                .build();
    }
}
