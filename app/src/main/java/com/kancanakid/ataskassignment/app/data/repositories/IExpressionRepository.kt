package com.kancanakid.ataskassignment.app.data.repositories

import com.kancanakid.ataskassignment.app.data.model.Expression
import kotlinx.coroutines.flow.Flow

/**
 * @author basyi
 * Created 6/6/2023 at 2:31 PM
 */
interface IExpressionRepository {
    fun loadExpressionFromDb():Flow<List<Expression>>
    fun loadExpressionFromFile():Flow<List<Expression>>
    suspend fun insert(expression: Expression, storage:String, onSuccess: (Boolean) -> Unit, onError:(String) -> Unit)
}