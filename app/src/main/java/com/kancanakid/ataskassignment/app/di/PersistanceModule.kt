package com.kancanakid.ataskassignment.app.di

import android.content.Context
import androidx.room.Room
import com.kancanakid.ataskassignment.app.data.persistance.dao.ExpressionDao
import com.kancanakid.ataskassignment.app.data.persistance.database.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * @author basyi
 * Created 6/6/2023 at 1:27 PM
 */

@Module
@InstallIn(SingletonComponent::class)
object PersistanceModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context):AppDatabase =
        Room.databaseBuilder(context,AppDatabase::class.java, AppDatabase.DB_NAME).fallbackToDestructiveMigration().build()

    @Provides
    @Singleton
    fun provideDao(appDatabase: AppDatabase):ExpressionDao = appDatabase.expressionDao()
}