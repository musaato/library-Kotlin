package com.musashi.library.dto

import java.math.BigDecimal
import java.time.LocalDateTime
import javax.validation.constraints.DecimalMin

// For displaying an author and his/her related books. role and contribution are related to a book
data class AuthorBooksDto(
    val id: Long,
    val isbn: String,
    val title: String,
    @field:DecimalMin(value = "0.01", inclusive = false, message = "Price must be greater than 0")
    val price: BigDecimal,
    val publicationStatus: String,
    val bookCover: String?,
    val description: String?,
    val publicationDate: LocalDateTime?,
    val createdTime: LocalDateTime?,
    val updatedTime: LocalDateTime?,
    val role: String?,
    val contribution: String?
)
