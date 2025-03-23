package com.musashi.library.services.impl

import com.musashi.jooq.generated.tables.Book
import com.musashi.jooq.generated.tables.records.BookRecord
import com.musashi.jooq.generated.tables.Author
import com.musashi.jooq.generated.tables.BookAuthors
import com.musashi.library.common.PageBean
import com.musashi.library.dto.AuthorDetailsDto
import com.musashi.library.dto.BookDto
import com.musashi.library.dto.BookTableDto
import org.jooq.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import kotlin.test.assertEquals
import java.math.BigDecimal
import java.time.LocalDateTime
import org.slf4j.LoggerFactory

class BookServiceImplTest {
    companion object {
        private val logger = LoggerFactory.getLogger(BookServiceImplTest::class.java)
    }

    private  val now = LocalDateTime.now()
    private val book = Book.BOOK
    private lateinit var dslContext: DSLContext
    private lateinit var bookService: BookServiceImpl
    private lateinit var sampleBookDto: BookTableDto

    @BeforeEach
    fun setUp() {
        dslContext = mock()
        bookService = BookServiceImpl(dslContext)
        sampleBookDto = BookTableDto(
            id = 1L,
            isbn = "9781234567891",
            title = "Test Book",
            price = BigDecimal("29.99"),
            publicationStatus = "published",
            bookCover = "cover.jpg",
            description = "Test description",
            publicationDate = now,
            createdTime = now,
            updatedTime = now
        )
    }

    // 1st method findByIsbn() test
    @Test
    fun `findByIsbn should return BookTableDto when book exists`() {
        val isbn = "9781234567891"

        // use "import org.mockito.Mockito" instead of "import org.mockito.kotlin.mock"
        val whereStep =
            Mockito.mock(SelectWhereStep::class.java) as SelectWhereStep<BookRecord> // building a SELECT SQL query
        val conditionStep = Mockito.mock(SelectConditionStep::class.java) as SelectConditionStep<BookRecord>

        // select all From
        whenever(dslContext.selectFrom(book)).thenReturn(whereStep)
        whenever(whereStep.where(book.ISBN.eq(isbn))).thenReturn(conditionStep)
        whenever(conditionStep.fetchOneInto(BookTableDto::class.java)).thenReturn(sampleBookDto)


        val result = bookService.findByIsbn(isbn)
        println("sampleBookDto is: $sampleBookDto")
        assertEquals(sampleBookDto, result)
        Mockito.verify(dslContext)
            .selectFrom(book) // This line mocks the behavior of dslContext.selectFrom(book) to return whereStep
        Mockito.verify(whereStep)
            .where(book.ISBN.eq(isbn)) // This line mocks the behavior of whereStep.where(book.ISBN.eq(isbn)) to return conditionStep.Verify if the where method on the whereStep mock was called with the argument book.ISBN.eq(isbn)
        Mockito.verify(conditionStep).fetchOneInto(BookTableDto::class.java)
    }

    @Test
    fun `findByIsbn should return null when book does not exist`() {
        // Given
        val isbn = "9780000000000"
        val whereStep = Mockito.mock(SelectWhereStep::class.java) as SelectWhereStep<BookRecord>
        val conditionStep = Mockito.mock(SelectConditionStep::class.java) as SelectConditionStep<BookRecord>

        whenever(dslContext.selectFrom(book)).thenReturn(whereStep)
        whenever(whereStep.where(book.ISBN.eq(isbn))).thenReturn(conditionStep)
        whenever(conditionStep.fetchOneInto(BookTableDto::class.java)).thenReturn(null)

        // When
        val result = bookService.findByIsbn(isbn)

        // Then
        kotlin.test.assertNull(result)
        Mockito.verify(dslContext).selectFrom(book)
        Mockito.verify(whereStep).where(book.ISBN.eq(isbn))
        Mockito.verify(conditionStep).fetchOneInto(BookTableDto::class.java)
    }

    @Test
    fun `findByIsbn should handle exception`() {
        // Given
        val isbn = "9781234567891"
        val whereStep = Mockito.mock(SelectWhereStep::class.java) as SelectWhereStep<BookRecord>

        whenever(dslContext.selectFrom(book)).thenReturn(whereStep)
        whenever(whereStep.where(book.ISBN.eq(isbn))).thenThrow(RuntimeException("Database error"))

        // When
        val result = bookService.findByIsbn(isbn)

        // Then
        kotlin.test.assertNull(result)
        Mockito.verify(dslContext).selectFrom(book)
        Mockito.verify(whereStep).where(book.ISBN.eq(isbn))
    }

    // 2nd method findAll() test
    @Test
    fun `findAll should return a PageBean of BookDto with authors`() {
        // Test parameters
        val pageNum = 0
        val pageSize = 10
        val totalCount = 2L

        // Mock data
        val author1 = AuthorDetailsDto(101L, "J.R.R.", "Tolkien", "Author", "Primary")
        val author2 = AuthorDetailsDto(102L, "Douglas", "Adams", "Author", "Primary")

        val bookTableDto1 = BookTableDto(
            id = 1L,
            isbn = "9780321765723",
            title = "The Lord of the Rings",
            price = BigDecimal("29.99"),
            publicationStatus = "Published",
            bookCover = "lotr.jpg",
            description = "A classic fantasy novel",
            publicationDate = now,
            createdTime = now,
            updatedTime = now
        )

        val bookTableDto2 = BookTableDto(
            id = 2L,
            isbn = "9780743273565",
            title = "The Hitchhiker's Guide to the Galaxy",
            price = BigDecimal("12.99"),
            publicationStatus = "Published",
            bookCover = "hitchhikers.jpg",
            description = "A science fiction comedy series",
            publicationDate = now,
            createdTime = now,
            updatedTime = now
        )

        // Mock count query
        val countSelect: SelectSelectStep<Record1<Int>> = mock()
        val countFrom: SelectJoinStep<Record1<Int>> = mock()
        whenever(dslContext.selectCount()).thenReturn(countSelect)
        whenever(countSelect.from(Book.BOOK)).thenReturn(countFrom)
        whenever(countFrom.fetchOne(0, Long::class.java)).thenReturn(totalCount)

        // Mock books query
        val booksSelect: SelectSelectStep<Record10<Long, String, String, BigDecimal, String, String, String, LocalDateTime, LocalDateTime, LocalDateTime>> = mock()
        val booksFrom: SelectJoinStep<Record10<Long, String, String, BigDecimal, String, String, String, LocalDateTime, LocalDateTime, LocalDateTime>> = mock()
        val booksOrder: SelectSeekStep1<Record10<Long, String, String, BigDecimal, String, String, String, LocalDateTime, LocalDateTime, LocalDateTime>, Long> = mock()
        val booksLimit: SelectLimitPercentStep<Record10<Long, String, String, BigDecimal, String, String, String, LocalDateTime, LocalDateTime, LocalDateTime>> = mock()
        val booksOffset: SelectLimitAfterOffsetStep<Record10<Long, String, String, BigDecimal, String, String, String, LocalDateTime, LocalDateTime, LocalDateTime>> = mock()

        whenever(dslContext.select(
            Book.BOOK.ID,
            Book.BOOK.ISBN,
            Book.BOOK.TITLE,
            Book.BOOK.PRICE,
            Book.BOOK.PUBLICATION_STATUS,
            Book.BOOK.BOOK_COVER,
            Book.BOOK.DESCRIPTION,
            Book.BOOK.PUBLICATION_DATE,
            Book.BOOK.CREATED_TIME,
            Book.BOOK.UPDATED_TIME
        )).thenReturn(booksSelect)
        whenever(booksSelect.from(Book.BOOK)).thenReturn(booksFrom)
        whenever(booksFrom.orderBy(Book.BOOK.ID)).thenReturn(booksOrder)
        whenever(booksOrder.limit(pageSize)).thenReturn(booksLimit)
        whenever(booksLimit.offset(pageNum * pageSize)).thenReturn(booksOffset)
        whenever(booksOffset.fetchInto(BookTableDto::class.java)).thenReturn(listOf(bookTableDto1, bookTableDto2))

        // Mock authors query for book 1
        val authorsSelect1: SelectSelectStep<Record5<Long, String, String, String, String>> = mock()
        val authorsFrom1: SelectJoinStep<Record5<Long, String, String, String, String>> = mock()
        val authorsJoin1: SelectOnStep<Record5<Long, String, String, String, String>> = mock()
        val authorsOn1: SelectOnConditionStep<Record5<Long, String, String, String, String>> = mock()
        val authorsWhere1: SelectConditionStep<Record5<Long, String, String, String, String>> = mock()

        // Mock authors query for book 2
        val authorsSelect2: SelectSelectStep<Record5<Long, String, String, String, String>> = mock()
        val authorsFrom2: SelectJoinStep<Record5<Long, String, String, String, String>> = mock()
        val authorsJoin2: SelectOnStep<Record5<Long, String, String, String, String>> = mock()
        val authorsOn2: SelectOnConditionStep<Record5<Long, String, String, String, String>> = mock()
        val authorsWhere2: SelectConditionStep<Record5<Long, String, String, String, String>> = mock()

        // Setup authors query for first book
        whenever(dslContext.select(
            BookAuthors.BOOK_AUTHORS.AUTHOR_ID,
            Author.AUTHOR.FIRST_NAME,
            Author.AUTHOR.LAST_NAME,
            BookAuthors.BOOK_AUTHORS.ROLE,
            BookAuthors.BOOK_AUTHORS.CONTRIBUTION
        )).thenReturn(authorsSelect1, authorsSelect2) // Return different mocks for each call

        whenever(authorsSelect1.from(Author.AUTHOR)).thenReturn(authorsFrom1)
        whenever(authorsFrom1.join(BookAuthors.BOOK_AUTHORS)).thenReturn(authorsJoin1)
        whenever(authorsJoin1.on(Author.AUTHOR.ID.eq(BookAuthors.BOOK_AUTHORS.AUTHOR_ID))).thenReturn(authorsOn1)
        whenever(authorsOn1.where(BookAuthors.BOOK_AUTHORS.BOOK_ID.eq(1L))).thenReturn(authorsWhere1)
        whenever(authorsWhere1.fetchInto(AuthorDetailsDto::class.java)).thenReturn(listOf(author1))

        // Setup authors query for second book
        whenever(authorsSelect2.from(Author.AUTHOR)).thenReturn(authorsFrom2)
        whenever(authorsFrom2.join(BookAuthors.BOOK_AUTHORS)).thenReturn(authorsJoin2)
        whenever(authorsJoin2.on(Author.AUTHOR.ID.eq(BookAuthors.BOOK_AUTHORS.AUTHOR_ID))).thenReturn(authorsOn2)
        whenever(authorsOn2.where(BookAuthors.BOOK_AUTHORS.BOOK_ID.eq(2L))).thenReturn(authorsWhere2)
        whenever(authorsWhere2.fetchInto(AuthorDetailsDto::class.java)).thenReturn(listOf(author2))

        // Execute the method
        val result = bookService.findAll(pageNum, pageSize)

        // Create expected result
        val expectedBook1 = BookDto(
            id = 1L,
            isbn = "9780321765723",
            title = "The Lord of the Rings",
            price = BigDecimal("29.99"),
            publicationStatus = "Published",
            bookCover = "lotr.jpg",
            description = "A classic fantasy novel",
            publicationDate = now,
            createdTime = now,
            updatedTime = now,
            authors = listOf(author1)
        )

        val expectedBook2 = BookDto(
            id = 2L,
            isbn = "9780743273565",
            title = "The Hitchhiker's Guide to the Galaxy",
            price = BigDecimal("12.99"),
            publicationStatus = "Published",
            bookCover = "hitchhikers.jpg",
            description = "A science fiction comedy series",
            publicationDate = now,
            createdTime = now,
            updatedTime = now,
            authors = listOf(author2)
        )

        // Assert
        val message = "assertEquals failed"
        assertEquals(PageBean(totalCount, listOf(expectedBook1, expectedBook2)), result, message)
        // logger.info("test execution finished.")
    }

    @Test
    fun `findAll should return null when an exception occurs`() {
        // Mock data
        val pageNum = 0
        val pageSize = 10

        // Mock DSLContext to throw an exception
        val selectSelectStep: SelectSelectStep<Record1<Int>> = mock()
        val selectJoinStep: SelectJoinStep<Record1<Int>> = mock()

        whenever(dslContext.selectCount()).thenReturn(selectSelectStep)
        whenever(selectSelectStep.from(Book.BOOK)).thenReturn(selectJoinStep)
        whenever(selectJoinStep.fetchOne(0, Long::class.java)).thenThrow(RuntimeException("Simulated exception"))

        // Call the method
        val result = bookService.findAll(pageNum, pageSize)

        // Assertion
        assertNull(result)
    }
}
