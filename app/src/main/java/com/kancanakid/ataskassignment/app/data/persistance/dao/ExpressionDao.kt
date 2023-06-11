package com.kancanakid.ataskassignment.app.data.persistance.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kancanakid.ataskassignment.app.data.model.Expression
import kotlinx.coroutines.flow.Flow

/**
 * @author basyi
 * Created 6/6/2023 at 1:14 PM
 */
@Dao
interface ExpressionDao {
    @Query("SELECT * FROM expression")
    fun getAll():Flow<List<Expression>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(expression: Expression)

    @Delete
    suspend fun delete(expression: Expression)
}