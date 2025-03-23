package com.musashi.library.dto

import javax.validation.constraints.DecimalMin
import java.math.BigDecimal
import java.time.LocalDateTime

// For adding a new book with authors' role and contribute to it by using a List<AuthorDetailsDto>
data class BookDto(
    val id: Long,
    val isbn: String,
    val title: String,
    // book price have to be greater than 0(本の価格は0以上であること)
    @field:DecimalMin(value = "0.01", inclusive = false, message = "Price must be greater than 0")
    val price: BigDecimal,
    val publicationStatus: String,
    val bookCover: String?,
    val description: String?,
    val publicationDate: LocalDateTime?,
    val createdTime: LocalDateTime?,
    val updatedTime: LocalDateTime?,
    // a book has at least 1 author(一冊の本は最低1人の著者を持つこと)
    val authors: List<AuthorDetailsDto>
)