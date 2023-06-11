package com.kancanakid.ataskassignment.app.di

import android.content.Context
import com.kancanakid.ataskassignment.app.data.persistance.dao.ExpressionDao
import com.kancanakid.ataskassignment.app.data.repositories.ExpressionRepository
import com.kancanakid.ataskassignment.app.data.repositories.IExpressionRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext

/**
 * @author basyi
 * Created 6/6/2023 at 1:28 PM
 */
@Module
@InstallIn(ViewModelComponent::class, ServiceComponent::class)
object RepositoryModule {

    @Provides
    fun provideRepository(expressionDao: ExpressionDao, @ApplicationContext context: Context) : IExpressionRepository = ExpressionRepository(expressionDao, context)

}