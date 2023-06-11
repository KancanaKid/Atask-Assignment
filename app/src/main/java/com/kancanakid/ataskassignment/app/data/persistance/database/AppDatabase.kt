package com.kancanakid.ataskassignment.app.data.persistance.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.kancanakid.ataskassignment.app.data.model.Expression
import com.kancanakid.ataskassignment.app.data.persistance.dao.ExpressionDao

/**
 * @author basyi
 * Created 6/6/2023 at 1:10 PM
 */
@Database(entities = [Expression::class], version = 1, exportSchema = false)
abstract class AppDatabase:RoomDatabase() {
    abstract fun expressionDao():ExpressionDao

    companion object {
        const val DB_NAME = "atask_assignment_db"
    }
}