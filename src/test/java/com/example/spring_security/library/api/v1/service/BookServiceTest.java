package com.example.spring_security.library.api.v1.service;

import com.example.spring_security.library.api.v1.dto.request.BookRequest;
import com.example.spring_security.library.api.v1.dto.response.BookResponse;
import com.example.spring_security.library.api.v1.entity.Book;
import com.example.spring_security.common.exception.ResourceNotFoundException;
import com.example.spring_security.library.api.v1.mapper.BookMapper;
import com.example.spring_security.library.api.v1.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Book Service Tests")
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookMapper bookMapper;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private BookService bookService;

    private BookRequest bookRequest;
    private BookRequest updateRequest;
    private Book book;
    private Book updatedBook;
    private BookResponse updatedResponse;
    private BookResponse bookResponse;
    private Book book2;
    private BookResponse bookResponse2;
    private final String TEST_USERNAME = "testuser";
    private final Long BOOK_ID = 1L;
    private final Long NON_EXISTENT_ID = 999L;

    @BeforeEach
    void setUp() {
        bookRequest = BookRequest.builder()
                .title("Test Book")
                .author("Sonam")
                .isbn("1234567890123")
                .publishedYear(2026)
                .genre("Sci Fiction")
                .description("Just for test")
                .price(new BigDecimal("230.00"))
                .stockQuantity(5)
                .build();

        book = Book.builder()
                .id(BOOK_ID)
                .title("Test Book")
                .author("Sonam")
                .isbn("1234567890123")
                .publishedYear(2026)
                .genre("Sci Fiction")
                .description("Just for test")
                .price(new BigDecimal("230.00"))
                .stockQuantity(5)
                .createdBy(TEST_USERNAME)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        bookResponse = BookResponse.builder()
                .id(BOOK_ID)
                .title("Test Book")
                .author("Sonam")
                .isbn("1234567890123")
                .publishedYear(2026)
                .genre("Sci Fiction")
                .description("Just for test")
                .price(new BigDecimal("230.00"))
                .stockQuantity(5)
                .createdBy(TEST_USERNAME)
                .createdAt(book.getCreatedAt())
                .updatedAt(book.getUpdatedAt())
                .build();

        book2 = Book.builder()
                .id(2L)
                .title("Test Book 2")
                .author("Tashi")
                .isbn("9876543210123")
                .publishedYear(2025)
                .genre("Fantasy")
                .description("Another test book")
                .price(new BigDecimal("150.00"))
                .stockQuantity(3)
                .createdBy(TEST_USERNAME)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        bookResponse2 = BookResponse.builder()
                .id(2L)
                .title("Test Book 2")
                .author("Tashi")
                .isbn("9876543210123")
                .publishedYear(2025)
                .genre("Fantasy")
                .description("Another test book")
                .price(new BigDecimal("150.00"))
                .stockQuantity(3)
                .createdBy(TEST_USERNAME)
                .createdAt(book2.getCreatedAt())
                .updatedAt(book2.getUpdatedAt())
                .build();

        updateRequest = BookRequest.builder()
                .title("Updated Title")
                .author("Updated Author")
                .isbn("9999999999999")
                .publishedYear(2026)
                .genre("Updated Genre")
                .description("Updated Description")
                .price(new BigDecimal("300.00"))
                .stockQuantity(10)
                .build();

        updatedBook = Book.builder()
                .id(BOOK_ID)
                .title("Updated Title")
                .author("Updated Author")
                .isbn("9999999999999")
                .publishedYear(2026)
                .genre("Updated Genre")
                .description("Updated Description")
                .price(new BigDecimal("300.00"))
                .stockQuantity(10)
                .createdBy(TEST_USERNAME)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        updatedResponse = BookResponse.builder()
                .id(BOOK_ID)
                .title("Updated Title")
                .author("Updated Author")
                .isbn("9999999999999")
                .publishedYear(2026)
                .genre("Updated Genre")
                .description("Updated Description")
                .price(new BigDecimal("300.00"))
                .stockQuantity(10)
                .createdBy(TEST_USERNAME)
                .createdAt(updatedBook.getCreatedAt())
                .updatedAt(updatedBook.getUpdatedAt())
                .build();
    }

    private void setupSecurityContext(String username) {
        lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
        lenient().when(authentication.getName()).thenReturn(username);
        lenient().when(authentication.isAuthenticated()).thenReturn(true);
        SecurityContextHolder.setContext(securityContext);
    }


    @Nested
    @DisplayName("Create Book Method Tests")
    class CreateBookTests {

        @BeforeEach
        void setUp() {
            setupSecurityContext(TEST_USERNAME);
        }

        @Test
        @DisplayName("Should create book successfully with valid request")
        void shouldCreateBookSuccessfully() {
            // Arrange
            when(bookRepository.existsByIsbn(bookRequest.getIsbn())).thenReturn(false);
            when(bookMapper.toEntity(bookRequest)).thenReturn(book);
            when(bookRepository.save(any(Book.class))).thenReturn(book);
            when(bookMapper.toResponse(book)).thenReturn(bookResponse);

            // Act
            BookResponse result = bookService.createBook(bookRequest);

            // Assert
            assertNotNull(result);
            assertEquals(bookResponse.getId(), result.getId());
            assertEquals(bookResponse.getTitle(), result.getTitle());
            assertEquals(bookResponse.getAuthor(), result.getAuthor());
            assertEquals(bookResponse.getIsbn(), result.getIsbn());
            assertEquals(bookResponse.getPublishedYear(), result.getPublishedYear());
            assertEquals(bookResponse.getGenre(), result.getGenre());
            assertEquals(bookResponse.getDescription(), result.getDescription());
            assertEquals(bookResponse.getPrice(), result.getPrice());
            assertEquals(bookResponse.getStockQuantity(), result.getStockQuantity());
            assertEquals(bookResponse.getCreatedBy(), result.getCreatedBy());

            verify(bookRepository).existsByIsbn(bookRequest.getIsbn());
            verify(bookMapper).toEntity(bookRequest);
            verify(bookRepository).save(any(Book.class));
            verify(bookMapper).toResponse(book);
            verifyNoMoreInteractions(bookRepository, bookMapper);
        }

        @Test
        @DisplayName("Should throw exception when ISBN already exists")
        void shouldThrowExceptionWhenIsbnAlreadyExists() {
            // Arrange
            when(bookRepository.existsByIsbn(bookRequest.getIsbn())).thenReturn(true);

            // Act & Assert
            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> bookService.createBook(bookRequest));

            assertEquals("Book with ISBN " + bookRequest.getIsbn() + " already exists",
                    exception.getMessage());

            verify(bookRepository).existsByIsbn(bookRequest.getIsbn());
            verify(bookMapper, never()).toEntity(any());
            verify(bookRepository, never()).save(any());
            verify(bookMapper, never()).toResponse(any());
        }

        @Test
        @DisplayName("Should set audit fields correctly when creating book")
        void shouldSetAuditFieldsCorrectly() {
            // Arrange
            when(bookRepository.existsByIsbn(bookRequest.getIsbn())).thenReturn(false);
            when(bookMapper.toEntity(bookRequest)).thenReturn(new Book());
            when(bookRepository.save(any(Book.class))).thenAnswer(invocation -> {
                Book savedBook = invocation.getArgument(0);
                savedBook.setId(BOOK_ID);
                return savedBook;
            });
            when(bookMapper.toResponse(any(Book.class))).thenReturn(bookResponse);

            // Act
            bookService.createBook(bookRequest);

            // Assert
            verify(bookRepository).save(argThat(savedBook ->
                    savedBook.getCreatedBy().equals(TEST_USERNAME) &&
                            savedBook.getCreatedAt() != null &&
                            savedBook.getUpdatedAt() != null
            ));
        }
    }

    @Nested
    @DisplayName("Get All Books Method Tests")
    class GetAllBooksTests {

        @Test
        @DisplayName("Should return all books when repository has books")
        void shouldReturnAllBooks() {
            // Arrange
            List<Book> books = Arrays.asList(book, book2);
            List<BookResponse> expectedResponses = Arrays.asList(bookResponse, bookResponse2);

            when(bookRepository.findAll()).thenReturn(books);
            when(bookMapper.toResponseList(books)).thenReturn(expectedResponses);

            // Act
            List<BookResponse> result = bookService.getAllBooks();

            // Assert
            assertNotNull(result);
            assertEquals(2, result.size());
            assertEquals(expectedResponses, result);

            verify(bookRepository).findAll();
            verify(bookMapper).toResponseList(books);
        }

        @Test
        @DisplayName("Should return empty list when no books exist")
        void shouldReturnEmptyListWhenNoBooks() {
            // Arrange
            List<Book> emptyList = Arrays.asList();
            when(bookRepository.findAll()).thenReturn(emptyList);
            when(bookMapper.toResponseList(emptyList)).thenReturn(Arrays.asList());

            // Act
            List<BookResponse> result = bookService.getAllBooks();

            // Assert
            assertNotNull(result);
            assertTrue(result.isEmpty());

            verify(bookRepository).findAll();
            verify(bookMapper).toResponseList(emptyList);
        }
    }

    @Nested
    @DisplayName("Get Book By ID Method Tests")
    class GetBookByIdTests {

        @Test
        @DisplayName("Should return book when ID exists")
        void shouldReturnBookWhenIdExists() {
            // Arrange
            when(bookRepository.findById(BOOK_ID)).thenReturn(Optional.of(book));
            when(bookMapper.toResponse(book)).thenReturn(bookResponse);

            // Act
            BookResponse result = bookService.getBookById(BOOK_ID);

            // Assert
            assertNotNull(result);
            assertEquals(bookResponse.getId(), result.getId());
            assertEquals(bookResponse.getTitle(), result.getTitle());
            assertEquals(bookResponse.getAuthor(), result.getAuthor());

            verify(bookRepository).findById(BOOK_ID);
            verify(bookMapper).toResponse(book);
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when ID doesn't exist")
        void shouldThrowExceptionWhenIdDoesNotExist() {
            // Arrange
            when(bookRepository.findById(NON_EXISTENT_ID)).thenReturn(Optional.empty());

            // Act & Assert
            ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                    () -> bookService.getBookById(NON_EXISTENT_ID));

            assertEquals("Book not found with id: " + NON_EXISTENT_ID, exception.getMessage());

            verify(bookRepository).findById(NON_EXISTENT_ID);
            verify(bookMapper, never()).toResponse(any());
        }
    }

    @Nested
    @DisplayName("Search Books Method Tests")
    class SearchBooksTests {

        @Test
        @DisplayName("Should search books by title")
        void shouldSearchBooksByTitle() {
            // Arrange
            String title = "Test";
            List<Book> books = Arrays.asList(book, book2);
            List<BookResponse> expectedResponses = Arrays.asList(bookResponse, bookResponse2);

            when(bookRepository.findByTitleContainingIgnoreCase(title)).thenReturn(books);
            when(bookMapper.toResponseList(books)).thenReturn(expectedResponses);

            // Act
            List<BookResponse> result = bookService.searchBooks(title, null, null);

            // Assert
            assertNotNull(result);
            assertEquals(2, result.size());

            verify(bookRepository).findByTitleContainingIgnoreCase(title);
            verify(bookMapper).toResponseList(books);
            verify(bookRepository, never()).findByAuthor(anyString());
            verify(bookRepository, never()).findByGenre(anyString());
            verify(bookRepository, never()).findAll();
        }

        @Test
        @DisplayName("Should search books by author")
        void shouldSearchBooksByAuthor() {
            // Arrange
            String author = "Sonam";
            List<Book> books = Arrays.asList(book);
            List<BookResponse> expectedResponses = Arrays.asList(bookResponse);

            when(bookRepository.findByAuthor(author)).thenReturn(books);
            when(bookMapper.toResponseList(books)).thenReturn(expectedResponses);

            // Act
            List<BookResponse> result = bookService.searchBooks(null, author, null);

            // Assert
            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals(author, result.get(0).getAuthor());

            verify(bookRepository).findByAuthor(author);
            verify(bookMapper).toResponseList(books);
            verify(bookRepository, never()).findByTitleContainingIgnoreCase(anyString());
            verify(bookRepository, never()).findByGenre(anyString());
            verify(bookRepository, never()).findAll();
        }

        @Test
        @DisplayName("Should search books by genre")
        void shouldSearchBooksByGenre() {
            // Arrange
            String genre = "Sci Fiction";
            List<Book> books = Arrays.asList(book);
            List<BookResponse> expectedResponses = Arrays.asList(bookResponse);

            when(bookRepository.findByGenre(genre)).thenReturn(books);
            when(bookMapper.toResponseList(books)).thenReturn(expectedResponses);

            // Act
            List<BookResponse> result = bookService.searchBooks(null, null, genre);

            // Assert
            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals(genre, result.get(0).getGenre());

            verify(bookRepository).findByGenre(genre);
            verify(bookMapper).toResponseList(books);
            verify(bookRepository, never()).findByTitleContainingIgnoreCase(anyString());
            verify(bookRepository, never()).findByAuthor(anyString());
            verify(bookRepository, never()).findAll();
        }

        @Test
        @DisplayName("Should return all books when no search criteria provided")
        void shouldReturnAllBooksWhenNoSearchCriteria() {
            // Arrange
            List<Book> books = Arrays.asList(book, book2);
            List<BookResponse> expectedResponses = Arrays.asList(bookResponse, bookResponse2);

            when(bookRepository.findAll()).thenReturn(books);
            when(bookMapper.toResponseList(books)).thenReturn(expectedResponses);

            // Act
            List<BookResponse> result = bookService.searchBooks(null, null, null);

            // Assert
            assertNotNull(result);
            assertEquals(2, result.size());

            verify(bookRepository).findAll();
            verify(bookMapper).toResponseList(books);
            verify(bookRepository, never()).findByTitleContainingIgnoreCase(anyString());
            verify(bookRepository, never()).findByAuthor(anyString());
            verify(bookRepository, never()).findByGenre(anyString());
        }

        @Test
        @DisplayName("Should return empty list when search returns no results")
        void shouldReturnEmptyListWhenNoResults() {
            // Arrange
            String title = "NonExistent";
            List<Book> emptyList = Arrays.asList();

            when(bookRepository.findByTitleContainingIgnoreCase(title)).thenReturn(emptyList);
            when(bookMapper.toResponseList(emptyList)).thenReturn(Arrays.asList());

            // Act
            List<BookResponse> result = bookService.searchBooks(title, null, null);

            // Assert
            assertNotNull(result);
            assertTrue(result.isEmpty());

            verify(bookRepository).findByTitleContainingIgnoreCase(title);
            verify(bookMapper).toResponseList(emptyList);
        }
    }

    @Nested
    @DisplayName("Update Book Method Tests")
    class UpdateBookTests {

        @BeforeEach
        void setUp() {
            setupSecurityContext(TEST_USERNAME);
        }

        @Test
        @DisplayName("Should update book successfully")
        void shouldUpdateBookSuccessfully() {
            when(bookRepository.findById(BOOK_ID)).thenReturn(Optional.of(book));
            when(bookRepository.existsByIsbn(updateRequest.getIsbn())).thenReturn(false);
            doNothing().when(bookMapper).updateEntity(book, updateRequest);
            when(bookRepository.save(any(Book.class))).thenReturn(updatedBook);
            when(bookMapper.toResponse(updatedBook)).thenReturn(updatedResponse);

            // Act
            BookResponse result = bookService.updateBook(BOOK_ID, updateRequest);

            // Assert
            assertNotNull(result);
            assertEquals(updatedResponse.getTitle(), result.getTitle());
            assertEquals(updatedResponse.getAuthor(), result.getAuthor());
            assertEquals(updatedResponse.getPrice(), result.getPrice());
            assertEquals(updatedResponse.getStockQuantity(), result.getStockQuantity());

            verify(bookRepository).findById(BOOK_ID);
            verify(bookRepository).existsByIsbn(updateRequest.getIsbn());
            verify(bookMapper).updateEntity(book, updateRequest);
            verify(bookRepository).save(book);
            verify(bookMapper).toResponse(updatedBook);
        }

        @Test
        @DisplayName("Should update book when ISBN is not changed")
        void shouldUpdateBookWhenIsbnNotChanged() {
            // Arrange
            BookRequest updateRequest = BookRequest.builder()
                    .title("Updated Title")
                    .author("Updated Author")
                    .isbn("1234567890123") // Same ISBN
                    .publishedYear(2026)
                    .genre("Updated Genre")
                    .description("Updated Description")
                    .price(new BigDecimal("300.00"))
                    .stockQuantity(10)
                    .build();

            Book updatedBook = new Book();
            updatedBook.setId(BOOK_ID);
            updatedBook.setTitle("Updated Title");

            when(bookRepository.findById(BOOK_ID)).thenReturn(Optional.of(book));
            doNothing().when(bookMapper).updateEntity(book, updateRequest);
            when(bookRepository.save(any(Book.class))).thenReturn(updatedBook);
            when(bookMapper.toResponse(updatedBook)).thenReturn(BookResponse.builder()
                    .id(BOOK_ID)
                    .title("Updated Title")
                    .build());

            // Act
            bookService.updateBook(BOOK_ID, updateRequest);

            // Assert
            verify(bookRepository).findById(BOOK_ID);
            verify(bookRepository, never()).existsByIsbn(anyString()); // Should not check ISBN
            verify(bookMapper).updateEntity(book, updateRequest);
            verify(bookRepository).save(book);
        }

        @Test
        @DisplayName("Should throw exception when ISBN already exists for another book")
        void shouldThrowExceptionWhenIsbnAlreadyExists() {
            // Arrange
            BookRequest updateRequest = BookRequest.builder()
                    .title("Updated Title")
                    .author("Updated Author")
                    .isbn("9876543210123") // Different ISBN
                    .publishedYear(2026)
                    .genre("Updated Genre")
                    .description("Updated Description")
                    .price(new BigDecimal("300.00"))
                    .stockQuantity(10)
                    .build();

            Book existingBookWithIsbn = Book.builder()
                    .id(2L)
                    .isbn("9876543210123")
                    .build();

            when(bookRepository.findById(BOOK_ID)).thenReturn(Optional.of(book));
            when(bookRepository.existsByIsbn(updateRequest.getIsbn())).thenReturn(true);
            when(bookRepository.findByIsbn(updateRequest.getIsbn())).thenReturn(Optional.of(existingBookWithIsbn));

            // Act & Assert
            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> bookService.updateBook(BOOK_ID, updateRequest));

            assertEquals("Book with ISBN " + updateRequest.getIsbn() + " already exists",
                    exception.getMessage());

            verify(bookRepository).findById(BOOK_ID);
            verify(bookRepository).existsByIsbn(updateRequest.getIsbn());
            verify(bookRepository).findByIsbn(updateRequest.getIsbn());
            verify(bookMapper, never()).updateEntity(any(), any());
            verify(bookRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when updating non-existent book")
        void shouldThrowExceptionWhenBookNotFound() {
            // Arrange
            when(bookRepository.findById(NON_EXISTENT_ID)).thenReturn(Optional.empty());

            // Act & Assert
            ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                    () -> bookService.updateBook(NON_EXISTENT_ID, bookRequest));

            assertEquals("Book not found with id: " + NON_EXISTENT_ID, exception.getMessage());

            verify(bookRepository).findById(NON_EXISTENT_ID);
            verify(bookRepository, never()).existsByIsbn(anyString());
            verify(bookMapper, never()).updateEntity(any(), any());
            verify(bookRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should update only provided fields (partial update)")
        void shouldUpdateOnlyProvidedFields() {
            // Arrange
            BookRequest partialUpdate = BookRequest.builder()
                    .title("Partial Update Title")
                    .stockQuantity(20)
                    .build();

            when(bookRepository.findById(BOOK_ID)).thenReturn(Optional.of(book));
            doNothing().when(bookMapper).updateEntity(book, partialUpdate);
            when(bookRepository.save(any(Book.class))).thenReturn(book);
            when(bookMapper.toResponse(any(Book.class))).thenReturn(bookResponse);

            // Act
            bookService.updateBook(BOOK_ID, partialUpdate);

            // Assert
            verify(bookRepository).findById(BOOK_ID);
            verify(bookMapper).updateEntity(book, partialUpdate);
            verify(bookRepository).save(book);

            // Verify ISBN check was not called since ISBN was not provided
            verify(bookRepository, never()).existsByIsbn(anyString());
        }

        @Test
        @DisplayName("Should update updatedAt timestamp when updating book")
        void shouldUpdateUpdatedAtTimestamp() {
            // Arrange
            BookRequest updateRequest = BookRequest.builder()
                    .title("Updated Title")
                    .build();

            when(bookRepository.findById(BOOK_ID)).thenReturn(Optional.of(book));
            doNothing().when(bookMapper).updateEntity(book, updateRequest);
            when(bookRepository.save(any(Book.class))).thenReturn(book);
            when(bookMapper.toResponse(any(Book.class))).thenReturn(bookResponse);

            // Act
            bookService.updateBook(BOOK_ID, updateRequest);

            // Assert
            verify(bookRepository).save(argThat(savedBook ->
                    savedBook.getUpdatedAt() != null
            ));
        }
    }

    @Nested
    @DisplayName("Delete Book Method Tests")
    class DeleteBookTests {

        @Test
        @DisplayName("Should delete book successfully")
        void shouldDeleteBookSuccessfully() {
            // Arrange
            when(bookRepository.findById(BOOK_ID)).thenReturn(Optional.of(book));
            doNothing().when(bookRepository).delete(book);

            // Act
            bookService.deleteBook(BOOK_ID);

            // Assert
            verify(bookRepository).findById(BOOK_ID);
            verify(bookRepository).delete(book);
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when deleting non-existent book")
        void shouldThrowExceptionWhenBookNotFound() {
            // Arrange
            when(bookRepository.findById(NON_EXISTENT_ID)).thenReturn(Optional.empty());

            // Act & Assert
            ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                    () -> bookService.deleteBook(NON_EXISTENT_ID));

            assertEquals("Book not found with id: " + NON_EXISTENT_ID, exception.getMessage());

            verify(bookRepository).findById(NON_EXISTENT_ID);
            verify(bookRepository, never()).delete(any());
        }
    }

    @Nested
    @DisplayName("ISBN Uniqueness Validation Tests")
    class IsbnUniquenessTests {

        @BeforeEach
        void setUp() {
            setupSecurityContext(TEST_USERNAME);
        }

        @Test
        @DisplayName("Should allow unique ISBN when creating book")
        void shouldAllowUniqueIsbnWhenCreating() {
            // Arrange
            when(bookRepository.existsByIsbn(bookRequest.getIsbn())).thenReturn(false);
            when(bookMapper.toEntity(bookRequest)).thenReturn(book);
            when(bookRepository.save(any(Book.class))).thenReturn(book);
            when(bookMapper.toResponse(book)).thenReturn(bookResponse);

            // Act
            bookService.createBook(bookRequest);

            // Assert - No exception thrown
            verify(bookRepository).existsByIsbn(bookRequest.getIsbn());
            verify(bookRepository).save(any(Book.class));
        }

        @Test
        @DisplayName("Should allow ISBN when updating the same book")
        void shouldAllowIsbnWhenUpdatingSameBook() {
            // Arrange
            BookRequest updateRequest = BookRequest.builder()
                    .title("Updated Title")
                    .isbn("1234567890123") // Same ISBN
                    .build();

            when(bookRepository.findById(BOOK_ID)).thenReturn(Optional.of(book));
            doNothing().when(bookMapper).updateEntity(book, updateRequest);
            when(bookRepository.save(any(Book.class))).thenReturn(book);
            when(bookMapper.toResponse(any(Book.class))).thenReturn(bookResponse);

            // Act
            bookService.updateBook(BOOK_ID, updateRequest);

            // Assert - No ISBN check performed
            verify(bookRepository).findById(BOOK_ID);
            verify(bookRepository, never()).existsByIsbn(anyString());
        }
    }

    @Nested
    @DisplayName("Edge Cases and Exception Tests")
    class EdgeCaseTests {

        @Test
        @DisplayName("Should handle null search parameters gracefully")
        void shouldHandleNullSearchParameters() {
            // Arrange
            List<Book> books = Arrays.asList(book);
            when(bookRepository.findAll()).thenReturn(books);
            when(bookMapper.toResponseList(books)).thenReturn(Arrays.asList(bookResponse));

            // Act
            List<BookResponse> result = bookService.searchBooks(null, null, null);

            // Assert
            assertNotNull(result);
            verify(bookRepository).findAll();
        }

        @Test
        @DisplayName("Should handle empty search parameters gracefully")
        void shouldHandleEmptySearchParameters() {
            // Arrange
            List<Book> books = Arrays.asList(book);
            when(bookRepository.findAll()).thenReturn(books);
            when(bookMapper.toResponseList(books)).thenReturn(Arrays.asList(bookResponse));

            // Act
            List<BookResponse> result = bookService.searchBooks("", "", "");

            // Assert
            assertNotNull(result);
            verify(bookRepository).findAll();
        }

        @Test
        @DisplayName("Should handle null request for create")
        void shouldHandleNullRequestForCreate() {
            // Act & Assert
            assertThrows(NullPointerException.class,
                    () -> bookService.createBook(null));
        }

        @Test
        @DisplayName("Should handle null ID for getById")
        void shouldHandleNullIdForGetById() {
            // Act & Assert
            assertThrows(ResourceNotFoundException.class,
                    () -> bookService.getBookById(null));
        }

        @Test
        @DisplayName("Should handle null ID for update")
        void shouldHandleNullIdForUpdate() {
            // Act & Assert
            assertThrows(ResourceNotFoundException.class,
                    () -> bookService.updateBook(null, bookRequest));
        }

        @Test
        @DisplayName("Should handle null ID for delete")
        void shouldHandleNullIdForDelete() {
            // Act & Assert
            assertThrows(ResourceNotFoundException.class,
                    () -> bookService.deleteBook(null));
        }

        @Test
        @DisplayName("Should handle null request for update")
        void shouldHandleNullRequestForUpdate() {
            // Arrange
            when(bookRepository.findById(BOOK_ID)).thenReturn(Optional.of(book));

            // Act & Assert
            assertThrows(NullPointerException.class,
                    () -> bookService.updateBook(BOOK_ID, null));
        }

        @Test
        @DisplayName("Should handle null ISBN in book request")
        void shouldHandleNullIsbnInRequest() {
            // Arrange
            BookRequest requestWithNullIsbn = BookRequest.builder()
                    .title("Test Book")
                    .author("Sonam")
                    .isbn(null)
                    .publishedYear(2026)
                    .genre("Sci Fiction")
                    .price(new BigDecimal("230.00"))
                    .stockQuantity(5)
                    .build();

            when(bookRepository.existsByIsbn(null)).thenReturn(false);
            when(bookMapper.toEntity(requestWithNullIsbn)).thenReturn(book);
            when(bookRepository.save(any(Book.class))).thenReturn(book);
            when(bookMapper.toResponse(book)).thenReturn(bookResponse);

            // Act
            BookResponse result = bookService.createBook(requestWithNullIsbn);

            // Assert
            assertNotNull(result);
            verify(bookRepository).existsByIsbn(null);
        }
    }

    @Nested
    @DisplayName("Concurrency Tests")
    class ConcurrencyTests {

        @Test
        @DisplayName("Should handle concurrent book creation with same ISBN")
        void shouldHandleConcurrentBookCreation() {
            // Arrange
            when(bookRepository.existsByIsbn(bookRequest.getIsbn()))
                    .thenReturn(true) // First call returns true (already exists)
                    .thenReturn(false); // Second call returns false

            // Act & Assert
            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> bookService.createBook(bookRequest));

            assertEquals("Book with ISBN " + bookRequest.getIsbn() + " already exists",
                    exception.getMessage());

            verify(bookRepository, atLeastOnce()).existsByIsbn(bookRequest.getIsbn());
        }
    }
}
