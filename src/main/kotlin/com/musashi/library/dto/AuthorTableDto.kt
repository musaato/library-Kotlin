package com.musashi.library.dto

import java.time.LocalDateTime
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Past

// For add a new author info, not related to book, can map to a database table.
data class AuthorTableDto(
    val id: Long?,
    @field:NotBlank(message = "First name cannot be blank")
    val firstName: String,
    @field:NotBlank(message = "Last name cannot be blank")
    val lastName: String,
    val profilePicture: String?,
    val biography: String?,
    val nationalityCode: String?,
    @field:Past(message = "Birthdate must be in the past")
    val birthdate: LocalDateTime?,
    val createdTime: LocalDateTime?,
    val updatedTime: LocalDateTime?,
)
