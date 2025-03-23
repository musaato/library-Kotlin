package com.musashi.library.controllers

import com.musashi.library.common.PageBean
import com.musashi.library.dto.AuthorDetailsDto
import com.musashi.library.dto.BookDto
import com.musashi.library.dto.BookTableDto
import com.musashi.library.services.BookService
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.anyInt
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.mockito.kotlin.whenever
import org.springframework.test.context.bean.override.mockito.MockitoBean
import java.math.BigDecimal
import java.time.LocalDateTime

@WebMvcTest(BookController::class)
class BookControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockitoBean
    private lateinit var bookService: BookService

    // Test data helper functions
    private fun createSampleBookTableDto(isbn: String) = BookTableDto(
        id = 1,
        isbn = isbn,
        title = "Sample Book",
        price = BigDecimal(29.99),
        publicationStatus = "published",
        bookCover = "sample-cover.jpg",
        description = "A sample book description",
        publicationDate = LocalDateTime.now(),
        createdTime = LocalDateTime.now(),
        updatedTime = LocalDateTime.now()
    )

    private fun createSampleBookDto() = BookDto(
        id = 1,
        isbn = "1234567890",
        title = "Sample Book",
        price = BigDecimal(29.99),
        publicationStatus = "published",
        bookCover = "sample-cover.jpg",
        description = "A sample book description",
        publicationDate = LocalDateTime.now(),
        createdTime = LocalDateTime.now(),
        updatedTime = LocalDateTime.now(),
        authors = listOf(
            AuthorDetailsDto(
                authorId = 1,
                firstName = "John",
                lastName = "Doe",
                role = "Primary",
                contribution = "Author"
            )
        )
    )

    // function findByIsbn()
    @Test
    fun `findByIsbn should return success result when book exists`() {
        // Given
        val isbn = "1234567890"
        val bookTableDto = createSampleBookTableDto(isbn)
        whenever(bookService.findByIsbn(isbn)).thenReturn(bookTableDto)

        // When/Then
        mockMvc.perform(get("/book/find")
            .param("isbn", isbn)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.code").value(0))
            .andExpect(jsonPath("$.msg").value("book found."))
            .andExpect(jsonPath("$.data.isbn").value(isbn))
            .andExpect(jsonPath("$.data.title").value("Sample Book"))
            .andExpect(jsonPath("$.data.price").value(29.99))
    }

    @Test
    fun `findByIsbn should return error result when book does not exist`() {
        // Given
        val isbn = "nonexistent"
        whenever(bookService.findByIsbn(isbn)).thenReturn(null)

        // When/Then
        mockMvc.perform(get("/book/find")
            .param("isbn", isbn)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.code").value(1))
            .andExpect(jsonPath("$.msg").value("book is not exist"))
    }

    @Test
    fun `findByIsbn should handle service exception`() {
        // Given
        val isbn = "1234567890"
        whenever(bookService.findByIsbn(isbn)).thenThrow(RuntimeException("Database error"))

        // When/Then
        mockMvc.perform(get("/book/find")
            .param("isbn", isbn)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.code").value(1))
            .andExpect(jsonPath("$.msg").value("Failed to fetch book: Database error"))
    }

    // function findAll()
    @Test
    fun `findAll should return success result with paginated books`() {
        // Given
        val pageNum = 1
        val pageSize = 10
        val bookDto = createSampleBookDto()
        val pageBean = PageBean(
            total = 1L,
            items = listOf(bookDto)
        )

        whenever(bookService.findAll(pageNum - 1, pageSize)).thenReturn(pageBean)

        // When/Then
        mockMvc.perform(get("/book")
            .param("pageNum", pageNum.toString())
            .param("pageSize", pageSize.toString())
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.code").value(0))
            .andExpect(jsonPath("$.msg").value("Fetched books successfully."))
            .andExpect(jsonPath("$.data.total").value(1))
            .andExpect(jsonPath("$.data.items[0].isbn").value(bookDto.isbn))
            .andExpect(jsonPath("$.data.items[0].title").value(bookDto.title))
            .andExpect(jsonPath("$.data.items[0].price").value(bookDto.price))
    }

    @Test
    fun `findAll should use default pagination values when not provided`() {
        // Given
        val defaultPageNum = 1
        val defaultPageSize = 10
        val pageBean = PageBean<BookDto>(
            total = 0L,
            items = emptyList()
        )

        whenever(bookService.findAll(defaultPageNum - 1, defaultPageSize)).thenReturn(pageBean)

        // When/Then
        mockMvc.perform(get("/book")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.code").value(0))
            .andExpect(jsonPath("$.data.total").value(0))
    }

    @Test
    fun `findAll should handle service exception`() {
        // Given
        val pageNum = 1
        val pageSize = 10
        whenever(bookService.findAll(pageNum - 1, pageSize))
            .thenThrow(RuntimeException("Database error"))

        // When/Then
        mockMvc.perform(get("/book")
            .param("pageNum", pageNum.toString())
            .param("pageSize", pageSize.toString())
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.code").value(1))
            .andExpect(jsonPath("$.msg").value("Failed to fetch books: Database error"))
    }

    @Test
    fun `findAll should handle invalid pagination parameters`() {
        // Given
        val pageNum = 0  // Invalid page number
        val pageSize = 1000  // Very large page size
        val pageBean = PageBean<BookDto>(
            total = 0L,
            items = emptyList()
        )

//         whenever(bookService.findAll(any(Int::class.java), any(Int::class.java))).thenReturn(pageBean) // works
         whenever(bookService.findAll(anyInt(), anyInt())).thenReturn(pageBean) // works

        // When/Then
        mockMvc.perform(get("/book")
            .param("pageNum", pageNum.toString())
            .param("pageSize", pageSize.toString())
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.code").value(0))
    }
}