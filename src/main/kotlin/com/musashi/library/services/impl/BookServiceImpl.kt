package com.musashi.library.services.impl

import com.fasterxml.jackson.core.type.TypeReference
import com.musashi.jooq.generated.tables.Book
import com.musashi.jooq.generated.tables.Author
import com.musashi.jooq.generated.tables.BookAuthors
import com.musashi.library.common.PageBean
import com.musashi.library.dto.AuthorDetailsDto
import com.musashi.library.dto.BookDto
import com.musashi.library.dto.BookTableDto
import com.musashi.library.services.BookService
import org.jooq.DSLContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import org.slf4j.LoggerFactory
import com.fasterxml.jackson.databind.ObjectMapper
import org.jooq.impl.DSL
import org.postgresql.util.PGobject
import org.springframework.transaction.annotation.EnableTransactionManagement

@Service
@EnableTransactionManagement
class BookServiceImpl(
    @Autowired private val dslContext: DSLContext

) : BookService {
    companion object{private val logger=LoggerFactory.getLogger(BookServiceImpl::class.java)}

    override fun findByIsbn(isbn: String): BookTableDto? {

        val book = Book.BOOK
        return try {
            dslContext
                .selectFrom(book)
                .where(book.ISBN.eq(isbn))
                .fetchOneInto(BookTableDto::class.java)

        } catch (e: Exception) {
            logger.error("An error occurred while finding book by ISBN: ${e.message}", e)
            null
        }
    }

    override fun findAll(pageNum: Int, pageSize: Int): PageBean<BookDto>? {

        val book = Book.BOOK
        val author = Author.AUTHOR
        val bookAuthors = BookAuthors.BOOK_AUTHORS
        val objectMapper = ObjectMapper()

        // Validate and adjust page parameters
        val safePageNum = maxOf(0, pageNum)
        val safePageSize = pageSize.coerceIn(1, 100)

        return try {
            val totalCount = dslContext
                .selectCount()
                .from(book)
                .fetchOne(0, Long::class.java) ?: 0L

            val booksWithAuthors = dslContext
                .select(
                    book.ID,
                    book.ISBN,
                    book.TITLE,
                    book.PRICE,
                    book.PUBLICATION_STATUS,
                    book.BOOK_COVER,
                    book.DESCRIPTION,
                    book.PUBLICATION_DATE,
                    book.CREATED_TIME,
                    book.UPDATED_TIME,
                    // Use COALESCE with JSON aggregate
                    DSL.field(
                        "COALESCE((SELECT json_agg(json_build_object(" +
                                "'AUTHOR_ID', ba.AUTHOR_ID, " +
                                "'FIRST_NAME', a.FIRST_NAME, " +
                                "'LAST_NAME', a.LAST_NAME, " +
                                "'ROLE', ba.ROLE, " +
                                "'CONTRIBUTION', ba.CONTRIBUTION)) " +
                                "FROM book_authors ba " +
                                "LEFT JOIN author a ON a.ID = ba.AUTHOR_ID " +
                                "WHERE ba.BOOK_ID = {0}), '[]')",
                        book.ID
                    ).`as`("authors")
                )
                .from(book)
                .leftJoin(bookAuthors).on(book.ID.eq(bookAuthors.BOOK_ID))
                .leftJoin(author).on(author.ID.eq(bookAuthors.AUTHOR_ID))
                .groupBy(
                    book.ID,
                    book.ISBN,
                    book.TITLE,
                    book.PRICE,
                    book.PUBLICATION_STATUS,
                    book.BOOK_COVER,
                    book.DESCRIPTION,
                    book.PUBLICATION_DATE,
                    book.CREATED_TIME,
                    book.UPDATED_TIME
                )
                .orderBy(book.ID)
                .limit(safePageSize)
                .offset(safePageNum * safePageSize)
                .fetch { record ->
                    BookDto(
                        id = record.get(book.ID),
                        isbn = record.get(book.ISBN),
                        title = record.get(book.TITLE),
                        price = record.get(book.PRICE),
                        publicationStatus = record.get(book.PUBLICATION_STATUS),
                        bookCover = record.get(book.BOOK_COVER),
                        description = record.get(book.DESCRIPTION),
                        publicationDate = record.get(book.PUBLICATION_DATE),
                        createdTime = record.get(book.CREATED_TIME),
                        updatedTime = record.get(book.UPDATED_TIME),
                        authors = (record.getValue("authors") as? PGobject)?.value?.let { authorsJson ->
                            objectMapper.readValue(
                                authorsJson,
                                object : TypeReference<List<Map<String, Any>>>() {}
                            ).map { authorMap: Map<String, Any> ->
                                AuthorDetailsDto(
                                    authorId = (authorMap["AUTHOR_ID"] as Number).toLong(),
                                    firstName = authorMap["FIRST_NAME"] as String,
                                    lastName = authorMap["LAST_NAME"] as String,
                                    role = authorMap["ROLE"] as String?,
                                    contribution = authorMap["CONTRIBUTION"] as String?
                                )
                            }
                        } ?: emptyList() // Default to an empty list if authorsJson is null
                    )
                }

            PageBean(totalCount, booksWithAuthors)
        } catch (e: Exception) {
            logger.error("An error occurred while finding all books: ${e.message}", e)
            null // Return null in case of exception
        }
    }

    @Transactional
    override fun add(bookDto: BookDto) {
        val book = Book.BOOK
        val author = Author.AUTHOR
        val bookAuthors = BookAuthors.BOOK_AUTHORS
        val now = LocalDateTime.now()
        // Insert Book
        try {
            val bookId = dslContext
                .insertInto(book)
                .set(book.ISBN, bookDto.isbn)
                .set(book.TITLE, bookDto.title)
                .set(book.PRICE, bookDto.price)
                .set(book.PUBLICATION_STATUS, bookDto.publicationStatus)
                .set(book.BOOK_COVER, bookDto.bookCover)
                .set(book.DESCRIPTION, bookDto.description)
                .set(book.PUBLICATION_DATE, bookDto.publicationDate)
                .set(book.CREATED_TIME, now)
                .set(book.UPDATED_TIME, now)
                .returningResult(book.ID)
                .fetchOne()?.getValue(book.ID)
                ?: throw RuntimeException("Failed to insert book")

            bookDto.authors.map { authorDetails ->
                // Check if author existed
              try{
                val existingAuthorId = dslContext
                    .select(author.ID)
                    .from(author)
                    .where(
                        author.FIRST_NAME.eq(authorDetails.firstName)
                            .and(author.LAST_NAME.eq(authorDetails.lastName))
                    )
                    .fetchOne()?.getValue(author.ID)

                // If author doesn't exist, insert new author
                val authorId = existingAuthorId ?: dslContext
                    .insertInto(author)
                    .set(author.FIRST_NAME, authorDetails.firstName)
                    .set(author.LAST_NAME, authorDetails.lastName)
                    .set(author.CREATED_TIME, now)
                    .set(author.UPDATED_TIME, now)
                    .returningResult(author.ID)
                    .fetchOne()?.getValue(author.ID)
                ?: throw RuntimeException("Failed to insert author")

                // Insert into book_authors
                dslContext
                    .insertInto(bookAuthors)
                    .set(bookAuthors.BOOK_ID, bookId)
                    .set(bookAuthors.AUTHOR_ID, authorId)
                    .set(bookAuthors.ROLE, authorDetails.role)
                    .set(bookAuthors.CONTRIBUTION, authorDetails.contribution)
                    .set(bookAuthors.CREATED_TIME, now)
                    .set(bookAuthors.UPDATED_TIME, now)
                    .execute()

              }catch (e: Exception) {
                  logger.error("An error occurred while processing authors: ${e.message}", e)
                  throw e
              }
            }

        } catch (e: Exception) {
            // logger.error("An error occurred while adding book: ${e.message}", e)
            throw RuntimeException("Transaction failed", e)
        }
    }

    @Transactional
    override fun update(bookDto: BookDto) {
        val book = Book.BOOK
        val bookAuthors = BookAuthors.BOOK_AUTHORS
        val author = Author.AUTHOR
        val now = LocalDateTime.now()

        // Update book table
        try {
            dslContext.update(book)
                .set(book.ISBN, bookDto.isbn)
                .set(book.TITLE, bookDto.title)
                .set(book.PRICE, bookDto.price)
                .set(book.PUBLICATION_STATUS, bookDto.publicationStatus)
                .set(book.BOOK_COVER, bookDto.bookCover)
                .set(book.DESCRIPTION, bookDto.description)
                .set(book.PUBLICATION_DATE, bookDto.publicationDate)
                .set(book.UPDATED_TIME, now)
                .where(book.ID.eq(bookDto.id))
                .execute()

            // Update authors table
            bookDto.authors.forEach { authorDetails ->
                dslContext.update(author)
                    .set(author.FIRST_NAME, authorDetails.firstName)
                    .set(author.LAST_NAME, authorDetails.lastName)
                    .set(author.UPDATED_TIME, now)
                    .where(author.ID.eq(authorDetails.authorId))
                    .execute()

                // Update book_authors table
                dslContext.update(bookAuthors)
                    .set(bookAuthors.ROLE, authorDetails.role)
                    .set(bookAuthors.CONTRIBUTION, authorDetails.contribution)
                    .set(bookAuthors.UPDATED_TIME, now)
                    .where(
                        bookAuthors.BOOK_ID.eq(bookDto.id)
                            .and(bookAuthors.AUTHOR_ID.eq(authorDetails.authorId))
                    )
                    .execute()
            }
        }catch (e: Exception) {
            logger.error("An error occurred while updating book: ${e.message}", e)
            throw RuntimeException("Transaction failed", e)
        }
    }

}
