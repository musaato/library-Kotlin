package com.musashi.library.dto

// For adding a new book with an author's role and contribution to it.
data class AuthorDetailsDto(
    val authorId: Long, // for findByAll result used, not for newly insert use.
    val firstName: String,
    val lastName: String,
    val role: String?,
    val contribution: String?
)
