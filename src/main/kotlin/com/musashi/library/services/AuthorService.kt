package com.musashi.library.services

import com.musashi.library.common.PageBean
import com.musashi.library.dto.AuthorBooksDto
import com.musashi.library.dto.AuthorTableDto

interface AuthorService {

    fun findByName(firstName: String, lastName: String): AuthorTableDto?

    fun findAll(pageNum: Int, pageSize: Int): PageBean<AuthorTableDto>?

    fun findBooksByAuthorId(pageNum: Int, pageSize: Int, authorId: Long): PageBean<AuthorBooksDto>?

    fun add(authorTable: AuthorTableDto)

    fun update(authorTable: AuthorTableDto)
}