package com.musashi.library.controllers

import com.musashi.library.common.PageBean
import com.musashi.library.common.Result
import com.musashi.library.dto.AuthorBooksDto
import com.musashi.library.dto.AuthorTableDto
import com.musashi.library.services.AuthorService
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime

@RestController
@RequestMapping("/author")
class AuthorController(private val authorService: AuthorService) {

       @GetMapping("/find")
    fun findByName(@RequestParam @Validated firstName: String, lastName: String) :Result<AuthorTableDto> {

       return try{
          val author = authorService.findByName(firstName, lastName)
           if (author != null) {
               val result = Result.success(author)
               result.msg = "author found."
               result
           } else {
               Result.error("author is not exist")
           }
       } catch (e: Exception){
           Result.error("Failed to fetch author: ${e.message}")
           // println("exception: $e")
       }
    }

    @GetMapping
    fun findAll(@RequestParam(value = "pageNum", defaultValue = "1") pageNum: Int,
                @RequestParam(value = "pageSize", defaultValue = "10") pageSize: Int)
            : Result<PageBean<AuthorTableDto>?> {
        println("pageNum: $pageNum, pageSize: $pageSize")
        return try {
            val pageBean = authorService.findAll(pageNum-1, pageSize)
            val result = Result.success(pageBean)
            result.msg = "Fetched authors successfully."
            result
        } catch (e: Exception) {
            Result.error("Failed to fetch authors: ${e.message}")
        }
    }

    // findBooksByAuthorId return AuthorBooksDto
    @GetMapping("/findBooks")
    fun findBooksByAuthorId(@RequestParam(value = "id") authorId: Long,
                            @RequestParam(value = "pageNum", defaultValue = "1") pageNum: Int,
                            @RequestParam(value = "pageSize", defaultValue = "10") pageSize: Int)
                            : Result<PageBean<AuthorBooksDto>> {
        return try {
            val pageBean = authorService.findBooksByAuthorId(pageNum-1,pageSize, authorId)
            if (pageBean != null) {
                val result = Result.success(pageBean)
                result.msg = "Author's all works found."
                result
            } else {
                Result.error("Author's work not exist.")
            }
        }catch (e: Exception) {
            Result.error("Failed to fetch author's works: ${e.message}")
        }
    }

    // Input author existed or not, front end can invoke findByName() by Ajax to check when user inputting author's name in author form
    @PostMapping
    fun add(@RequestBody @Validated authorTable: AuthorTableDto): Result<Any?>{
        val birthdate = authorTable.birthdate
        val now = LocalDateTime.now()
        // check whether author's birthdate is before now or not
        if(birthdate == null || birthdate.isBefore(now)) {
            return try {
                val firstName = authorTable.firstName
                val lastName =authorTable.lastName
                val author =authorService.findByName(firstName,lastName)
                // double check whether author existed or not.
                if (author==null) {
                    authorService.add(authorTable)
                    Result.success()
                } else{
                    Result.error("Author already existed.")
                }
            } catch (e: Exception) {
                Result.error("Failed to insert author record: ${e.message}")
            }
        }else{
            return Result.error("Wrong birthdate.")
        }

    }

    @PutMapping
    fun update(@RequestBody @Validated authorTable: AuthorTableDto): Result<Any?>{
        val birthdate = authorTable.birthdate
        val now = LocalDateTime.now()
        if(birthdate == null || birthdate.isBefore(now)) {
            return try {
                authorService.update(authorTable)
                Result.success()
            } catch (e: Exception) {
                Result.error("Failed to update author record: ${e.message}")
            }
        }else{
            return Result.error("Wrong birthdate.")
        }
    }

}