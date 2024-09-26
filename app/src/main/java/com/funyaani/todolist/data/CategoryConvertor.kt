package com.funyaani.todolist.data

import androidx.room.TypeConverter
import com.funyaani.todolist.domain.Category

class CategoryConverter {
    @TypeConverter
    fun fromCategory(category: Category): String {
        return category.name
    }

    @TypeConverter
    fun toCategory(category: String): Category {
        return Category.valueOf(category)
    }
}
