package com.musashi.library.controllers

import com.musashi.library.common.PageBean
import com.musashi.library.dto.BookDto
import com.musashi.library.services.BookService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import com.musashi.library.common.Result
import com.musashi.library.dto.BookTableDto
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam

@RestController
@RequestMapping("/book")
class BookController(private val bookService: BookService) {

    @GetMapping("/find") // for ajax request use, which can be invoked to validate existence before submitting new book post form request.
    fun findByIsbn(@RequestParam @Validated isbn: String): Result<BookTableDto> {
        return try {
            val book = bookService.findByIsbn(isbn)
            // println("book: $book")
            if (book != null) {
                val result = Result.success(book)
                result.msg = "book found."
                result
            } else {
                Result.error("book is not exist")
            }

        } catch (e: Exception) {
            Result.error("Failed to fetch book: ${e.message}")
        }
    }


    @GetMapping
    fun findAll(
        @RequestParam(value = "pageNum", defaultValue = "1") pageNum: Int,
        @RequestParam(value = "pageSize", defaultValue = "10") pageSize: Int
    )
            : Result<PageBean<BookDto>?> {
        println("pageNum: $pageNum, pageSize: $pageSize")
        return try {
            // adjust user-sent first page number from 1 to 0 to meet DB offset
            // url be like: http://localhost:7777/book?pageNum=1&pageSize=5 fetch page 1 and size 5
            val pageBean = bookService.findAll(pageNum - 1, pageSize)
            val result = Result.success(pageBean)
            result.msg = "Fetched books successfully."
            result
        } catch (e: Exception) {
            Result.error("Failed to fetch books: ${e.message}")
        }
    }

    // whether book existed, already checked by Ajax findByIsbn() when user inputting ISBN in book form
    @PostMapping
    fun add(@RequestBody bookDto: BookDto): Result<Any?> {
        return try {
            val isbn = bookDto.isbn
            val book = bookService.findByIsbn(isbn)
            // double check whether book existed or not.
            if (book == null) {
                bookService.add(bookDto)
                Result.success()
            } else{
                Result.error("Book already existed.")
            }
        } catch (e: Exception) {
            Result.error("Failed to insert book record: ${e.message}")
        }
    }

    // update book record by bookId
    // http://localhost:7777/book
    @PutMapping
    fun update(@RequestBody @Validated bookDto: BookDto): Result<Any?> {
        val isbn = bookDto.isbn
        val newPublicationStatus = bookDto.publicationStatus
        // println("newPublicationStatus: $newPublicationStatus")

        val book = bookService.findByIsbn(isbn)
        val oldPublicationStatus = book?.publicationStatus
        println("oldPublicationStatus: $oldPublicationStatus")
        // book record updatable as long as newPublicationStatus is not unpublished and oldPublicationStatus is not published
        if (!(newPublicationStatus == "unpublished" && oldPublicationStatus == "published")) {
            return try {
                println("we can go to update book record now.")
                bookService.update(bookDto)
                Result.success()
            } catch (e: Exception) {
                Result.error("Failed to update book record: ${e.message}")
            }
        } else {
            return Result.error("You can't update published book to unpublished. ")
        }

    }


}