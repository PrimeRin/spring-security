package com.example.spring_security.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookResponse {
    private Long id;
    private String title;
    private String author;
    private String isbn;
    private Integer publishedYear;
    private String genre;
    private String description;
    private BigDecimal price;
    private Integer stockQuantity;
    private String createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
