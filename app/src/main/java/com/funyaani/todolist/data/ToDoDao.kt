package com.funyaani.todolist.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.funyaani.todolist.domain.Category
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface ToDoDao {

    @Query("SELECT * FROM todos ORDER BY date ASC, time ASC")
    fun getAllTodos(): Flow<List<ToDoItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(todo: ToDoItemEntity)

    @Query("SELECT * FROM todos WHERE category = :category ORDER BY date ASC, time ASC")
    fun getTodosByCategory(category: Category): Flow<List<ToDoItemEntity>>

//    @Query("SELECT * FROM todos WHERE date < :currentDate AND isCompleted = 0 ORDER BY date ASC")
//    fun getOverdueTodos(currentDate: LocalDate): Flow<List<ToDoItemEntity>>

    @Query("DELETE FROM todos WHERE id = :id")
    suspend fun deleteTodo(id: Long)

//    @Query("SELECT * FROM todos WHERE title LIKE '%' || :query || '%' ORDER BY date ASC, time ASC")
//    fun searchTodos(query: String): List<ToDoItemEntity>

    @Query("SELECT * FROM todos WHERE id = :id LIMIT 1")
    suspend fun getTodoById(id: Long): ToDoItemEntity?

    @Query("UPDATE todos SET isCompleted = 1, previousCategory = category, category = 'FINISHED' WHERE id = :id")
    suspend fun markAsCompleted(id: Long)

    @Query("UPDATE todos SET isCompleted = 0, category = previousCategory, previousCategory = NULL WHERE id = :id")
    suspend fun markAsIncomplete(id: Long)

    @Query("SELECT * FROM todos WHERE title LIKE '%' || :query || '%'")
    suspend fun searchTodos(query: String): List<ToDoItemEntity>
}

