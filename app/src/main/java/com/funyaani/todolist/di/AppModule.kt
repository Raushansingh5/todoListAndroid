package com.funyaani.todolist.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.funyaani.todolist.data.ToDoDao
import com.funyaani.todolist.data.TodoDatabase
import com.funyaani.todolist.data.TodoRepositoryImpl
import com.funyaani.todolist.domain.TodoRepository
import com.funyaani.todolist.domain.UseCase.AddTodoUseCase
import com.funyaani.todolist.domain.UseCase.DeleteTodoUseCase
import com.funyaani.todolist.domain.UseCase.GetTodoById
import com.funyaani.todolist.domain.UseCase.GetTodosUseCase
import com.funyaani.todolist.domain.UseCase.NoteUseCase
import com.funyaani.todolist.domain.UseCase.SearchTodosUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideTodoDatabase(app:Application): TodoDatabase {
        return Room.databaseBuilder(
            app,
            TodoDatabase::class.java,
            "todo_db"
        ).build()
    }

    @Provides
    fun provideTodoDao(database: TodoDatabase): ToDoDao {
        return database.todoDao()
    }

    @Provides
    @Singleton
    fun provideTodoRepository(dao: ToDoDao): TodoRepository {
        return TodoRepositoryImpl(dao)
    }

    @Provides
    @Singleton
    fun provideTodoUseCase(repository: TodoRepository): NoteUseCase {
        return NoteUseCase(
            addTodoUseCase = AddTodoUseCase(repository),
            deleteTodoUseCase = DeleteTodoUseCase(repository),
            getTodosUseCase = GetTodosUseCase(repository),
            getTodoById= GetTodoById(repository),
            searchTodo = SearchTodosUseCase(repository)
        )
    }

    @Provides
    @Singleton
    fun provideApplicationContext(@ApplicationContext context: Context): Context {
        return context
    }
}