package com.musashi.library.services.impl

import com.musashi.jooq.generated.tables.Author
import com.musashi.jooq.generated.tables.Book
import com.musashi.jooq.generated.tables.BookAuthors
import com.musashi.library.common.PageBean
import com.musashi.library.dto.AuthorBooksDto
import com.musashi.library.dto.AuthorTableDto
import com.musashi.library.services.AuthorService
import org.jooq.DSLContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import org.slf4j.LoggerFactory
import org.springframework.transaction.annotation.EnableTransactionManagement

@Service
@EnableTransactionManagement
class AuthorServiceImpl( // Autowired constructor
    @Autowired private val dslContext: DSLContext
) : AuthorService {

    companion object{private val logger=LoggerFactory.getLogger(AuthorServiceImpl::class.java)}

    override fun findByName(firstName: String, lastName: String): AuthorTableDto? {

        val author = Author.AUTHOR
        return try {
            dslContext
                .selectFrom(author)
                .where(
                    author.FIRST_NAME.eq(firstName)
                        .and(author.LAST_NAME.eq(lastName))
                )
                .fetchOneInto(AuthorTableDto::class.java)
        }catch (e: Exception) {
            logger.error("An error occurred while finding the author: ${e.message}", e)
            null
        }
    }

    override fun findAll(pageNum: Int, pageSize: Int): PageBean<AuthorTableDto>? {
        val author = Author.AUTHOR
        val safePageNum = maxOf(0, pageNum)
        val safePageSize = pageSize.coerceIn(1, 100)
      return try{
        val totalCount = dslContext
            .selectCount()
            .from(author)
            .fetchOne(0, Long::class.java)?:0L

        val authors = dslContext
                .selectFrom(author)
            .orderBy(author.UPDATED_TIME.desc())
            .limit(safePageSize)
            .offset(safePageNum * safePageSize)
            .fetchInto(AuthorTableDto::class.java)

            PageBean(
            total = totalCount,
            items = authors
        )
      }catch (e: Exception) {
          logger.error("An error occurred while finding all authors: ${e.message}", e)
          null
      }
    }

    // find all books by authorId
    override fun findBooksByAuthorId(pageNum: Int, pageSize: Int, authorId: Long): PageBean<AuthorBooksDto>? {
        val book = Book.BOOK
        val bookAuthors = BookAuthors.BOOK_AUTHORS

        // Validate and adjust page parameters
        val safePageNum = maxOf(0, pageNum)
        val safePageSize = pageSize.coerceIn(1, 100)

        // Count total books
        return try{
        val totalCount = dslContext
            .selectCount()
            .from(bookAuthors)
            .where(bookAuthors.AUTHOR_ID.eq(authorId))
            .fetchOne(0, Long::class.java) ?: 0L

       // println("totalCount: $totalCount")

        val books = dslContext
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
                bookAuthors.ROLE,
                bookAuthors.CONTRIBUTION

            )
            .from(book)
            .join(bookAuthors).on(book.ID.eq(bookAuthors.BOOK_ID))
            .where(bookAuthors.AUTHOR_ID.eq(authorId))
            .orderBy(book.ID)
            .limit(safePageSize)
            .offset(safePageNum * safePageSize)
            .fetchInto(AuthorBooksDto::class.java)

            PageBean(
            total = totalCount,
            items = books
        )
      }catch (e: Exception) {
            logger.error("An error occurred while fetching all works of the author: ${e.message}", e)
            null
      }

    }

    // add author record
    @Transactional
    override fun add(authorTable: AuthorTableDto) {
        val author = Author.AUTHOR
        val now = LocalDateTime.now()
        try {
            dslContext
                .insertInto(author)
                .set(author.FIRST_NAME, authorTable.firstName)
                .set(author.LAST_NAME, authorTable.lastName)
                // set statement will ignore if authorTable attributes are null
                .set(author.PROFILE_PICTURE, authorTable.profilePicture)
                .set(author.BIOGRAPHY, authorTable.biography)
                .set(author.NATIONALITY_CODE, authorTable.nationalityCode)
                .set(author.BIRTHDATE, authorTable.birthdate)
                .set(author.CREATED_TIME, now)
                .set(author.UPDATED_TIME, now)
                .execute()
        } catch (e: Exception) {
            logger.error("An error occurred while adding author: ${e.message}", e)
            throw RuntimeException("Transaction failed", e)
        }
    }

    // update author by author id
    @Transactional
    override fun update(authorTable: AuthorTableDto) {
        val author = Author.AUTHOR
        val now = LocalDateTime.now()
      try{
        dslContext
            .update(author)
            .set(author.FIRST_NAME, authorTable.firstName)
            .set(author.LAST_NAME, authorTable.lastName)
            .set(author.PROFILE_PICTURE, authorTable.profilePicture)
            .set(author.BIOGRAPHY, authorTable.biography)
            .set(author.NATIONALITY_CODE, authorTable.nationalityCode)
            .set(author.BIRTHDATE, authorTable.birthdate)
            .set(author.UPDATED_TIME, now)
            .where(author.ID.eq(authorTable.id))
            .execute()
      }catch (e: Exception) {
          logger.error("An error occurred while updating the author: ${e.message}", e)
          throw RuntimeException("Transaction failed", e)
      }
    }

}
