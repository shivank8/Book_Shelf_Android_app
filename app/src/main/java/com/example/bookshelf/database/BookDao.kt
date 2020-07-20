package com.example.bookshelf.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface BookDao {
    @Insert
    fun insertBook(bookEntity: BookEntity)
    @Delete
    fun deleteBook(bookEntity: BookEntity)

    @Query("SELECT * FROM books")
    fun getAll():List<BookEntity>

    @Query("SELECT * from books WHERE book_id=:bookId")
    fun getBookByid(bookId:String):BookEntity

}