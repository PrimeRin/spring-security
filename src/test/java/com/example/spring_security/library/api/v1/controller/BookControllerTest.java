package com.example.spring_security.library.api.v1.controller;

import com.example.spring_security.library.api.v1.dto.request.BookRequest;
import com.example.spring_security.library.api.v1.dto.response.BookResponse;
import com.example.spring_security.library.api.v1.entity.Book;
import com.example.spring_security.library.api.v1.repository.BookRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class BookControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;  // Changed to WebApplicationContext

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private RestTestClient client;
    private BookRequest bookRequest;
    private Book existingBook;

    @BeforeEach
    void setUp() {
        client = RestTestClient.bindToApplicationContext(webApplicationContext).build();  // Using WebApplicationContext
        bookRepository.deleteAll();

        bookRequest = BookRequest.builder()
                .title("Integration Test Book")
                .author("John Doe")
                .isbn("9781234567890")
                .publishedYear(2024)
                .genre("Technology")
                .description("An excellent book about integration testing")
                .price(new BigDecimal("29.99"))
                .stockQuantity(50)
                .build();

        existingBook = Book.builder()
                .title("Existing Book")
                .author("Jane Smith")
                .isbn("9780987654321")
                .publishedYear(2023)
                .genre("Science")
                .description("A fascinating science book")
                .price(new BigDecimal("19.99"))
                .stockQuantity(30)
                .createdBy("admin")
                .build();
        existingBook = bookRepository.save(existingBook);
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"BOOK_CREATE"})
    void shouldCreateBookSuccessfully() {
        // Create a book
        BookResponse response = client.post()
                .uri("/api/v1/books")
                .contentType(MediaType.APPLICATION_JSON)
                .body(bookRequest)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(BookResponse.class)
                .returnResult()
                .getResponseBody();

        // Verify response
        assertThat(response).isNotNull();
        assertThat(response.getId()).isNotNull();
        assertThat(response.getTitle()).isEqualTo("Integration Test Book");
        assertThat(response.getAuthor()).isEqualTo("John Doe");
        assertThat(response.getIsbn()).isEqualTo("9781234567890");
        assertThat(response.getPublishedYear()).isEqualTo(2024);
        assertThat(response.getGenre()).isEqualTo("Technology");
        assertThat(response.getDescription()).isEqualTo("An excellent book about integration testing");
        assertThat(response.getPrice()).isEqualByComparingTo(new BigDecimal("29.99"));
        assertThat(response.getStockQuantity()).isEqualTo(50);
        assertThat(response.getCreatedBy()).isEqualTo("admin");
        assertThat(response.getCreatedAt()).isNotNull();
        assertThat(response.getUpdatedAt()).isNotNull();

        // Verify in database
        Book savedBook = bookRepository.findById(response.getId()).orElseThrow();
        assertThat(savedBook.getTitle()).isEqualTo("Integration Test Book");
        assertThat(savedBook.getAuthor()).isEqualTo("John Doe");
        assertThat(savedBook.getIsbn()).isEqualTo("9781234567890");
        assertThat(savedBook.getCreatedBy()).isEqualTo("admin");
    }

    @Test
    @WithMockUser(authorities = {"BOOK_READ"})
    void shouldRetrieveBookById() {
        // Retrieve the existing book by ID
        BookResponse response = client.get()
                .uri("/api/v1/books/{id}", existingBook.getId())
                .exchange()
                .expectStatus().isOk()
                .expectBody(BookResponse.class)
                .returnResult()
                .getResponseBody();

        // Verify response
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(existingBook.getId());
        assertThat(response.getTitle()).isEqualTo("Existing Book");
        assertThat(response.getAuthor()).isEqualTo("Jane Smith");
        assertThat(response.getIsbn()).isEqualTo("9780987654321");
        assertThat(response.getPublishedYear()).isEqualTo(2023);
        assertThat(response.getGenre()).isEqualTo("Science");
        assertThat(response.getDescription()).isEqualTo("A fascinating science book");
        assertThat(response.getPrice()).isEqualByComparingTo(new BigDecimal("19.99"));
        assertThat(response.getStockQuantity()).isEqualTo(30);
        assertThat(response.getCreatedBy()).isEqualTo("admin");
        assertThat(response.getCreatedAt()).isNotNull();
        assertThat(response.getUpdatedAt()).isNotNull();

        // Verify it matches the database
        Book dbBook = bookRepository.findById(existingBook.getId()).orElseThrow();
        assertThat(dbBook.getTitle()).isEqualTo(response.getTitle());
        assertThat(dbBook.getAuthor()).isEqualTo(response.getAuthor());
        assertThat(dbBook.getIsbn()).isEqualTo(response.getIsbn());
    }
}
