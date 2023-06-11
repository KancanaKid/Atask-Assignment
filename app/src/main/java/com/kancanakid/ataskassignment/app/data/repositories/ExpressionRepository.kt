package com.kancanakid.ataskassignment.app.data.repositories

import android.content.Context
import android.util.Log
import com.kancanakid.ataskassignment.app.data.common.base64Decode
import com.kancanakid.ataskassignment.app.data.common.readData
import com.kancanakid.ataskassignment.app.data.common.saveData
import com.kancanakid.ataskassignment.app.data.model.Expression
import com.kancanakid.ataskassignment.app.data.persistance.dao.ExpressionDao
import com.kancanakid.ataskassignment.app.data.persistance.database.FileStorageDatabase
import com.kancanakid.ataskassignment.app.ui.common.Screen
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

/**
 * @author basyi
 * Created 6/6/2023 at 1:22 PM
 * purpose: Part as data layer to handle the data manipulation
 */
class ExpressionRepository @Inject constructor(
    private val dao:ExpressionDao,
    @ApplicationContext val context: Context
) : IExpressionRepository {
    override fun loadExpressionFromDb(): Flow<List<Expression>> = dao.getAll()

    /*
    * load data from file storage, since the text are encode in base64 format it will be decoded before returned as flow
    * */
    override fun loadExpressionFromFile(): Flow<List<Expression>> = flow<List<Expression>> {
        try {
            val result = readData(FileStorageDatabase.FILENAME,context)
            val items = arrayListOf<Expression>()
            result.forEach {
                if(it.isNotEmpty() || it.isNotBlank()){
                    val expressionValue = base64Decode(it).substringBefore("=")
                    val resultValue = base64Decode(it).substringAfter("=")
                    items.add(Expression(expressionValue, resultValue))
                }
            }
            emit(items)
        }catch (e:Exception){
            Log.e(TAG,e.message!!)
        }
    }.flowOn(Dispatchers.IO)


    override suspend fun insert(
        expression: Expression,
        storage:String,
        onSuccess: (Boolean) -> Unit,
        onError: (String) -> Unit
    ) {
        try {
            if(storage == Screen.DATABASE_STORAGE){
                dao.insert(expression)
                onSuccess(true)
            }else{
                //save to file storage
                val data = "${expression.expression} = ${expression.result}"
                saveData(data,FileStorageDatabase.FILENAME,context,{
                    onSuccess(true)
                },{
                    onError(it)
                })
            }
        }catch (e:Exception){
            onError(e.message!!)
        }
    }

    companion object {
        val TAG: String = ExpressionRepository::class.java.name
    }
}