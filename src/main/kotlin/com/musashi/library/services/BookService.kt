package com.musashi.library.services

import com.musashi.library.common.PageBean
import com.musashi.library.dto.BookDto
import com.musashi.library.dto.BookTableDto

interface BookService {

    fun findByIsbn(isbn: String): BookTableDto?

    fun findAll(pageNum: Int, pageSize: Int): PageBean<BookDto>?

    fun add(bookDto: BookDto)

    fun update(bookDto: BookDto)

}