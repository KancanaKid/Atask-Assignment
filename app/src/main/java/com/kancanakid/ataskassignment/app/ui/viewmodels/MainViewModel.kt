package com.kancanakid.ataskassignment.app.ui.viewmodels

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.kancanakid.ataskassignment.app.R
import com.kancanakid.ataskassignment.app.data.model.Expression
import com.kancanakid.ataskassignment.app.data.repositories.ExpressionRepository
import com.kancanakid.ataskassignment.app.ui.common.UiState
import com.kancanakid.ataskassignment.app.ui.common.UiState.Success
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import okio.IOException
import javax.inject.Inject

/**
 * @author basyi
 * Created 6/6/2023 at 1:01 PM
 * purpose: Part as UI layer to handle the states of data or use cases
 */
@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: ExpressionRepository) : ViewModel() {

    val uiListState : StateFlow<UiState> = repository.loadExpressionFromDb()
        .map<List<Expression>, UiState>(::Success)
        .catch { emit(UiState.Error(it)) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UiState.Loading)

    val uiListFromFileState : StateFlow<UiState> = repository.loadExpressionFromFile()
        .map<List<Expression>, UiState>(::Success)
        .catch { emit(UiState.Error(it)) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UiState.Loading)

    val insertLiveData by lazy { MutableLiveData<Boolean>() }
    var dialogOpen = MutableLiveData<Boolean>()
    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    fun insert(expression:String, result:String, storage:String){
        viewModelScope.launch {
            val data = Expression(expression, result)
            repository.insert(data, storage,onSuccess = { e ->
                insertLiveData.postValue(e)
            }, onError = {
                Log.i(MainViewModel::class.java.name,it)
                insertLiveData.postValue(false)
            })
        }
    }

    /*
    * Process the text recognition using Uri as source. Will returning the result as String on callback function
    * */
    fun processImage(imageUri: Uri?, context:Context, onSuccess : (Map<String,String>) -> Unit, onError : (String) -> Unit) {
        var image:InputImage? = null
        try {
            imageUri?.let {
                image = InputImage.fromFilePath(context, it)
                image?.let { inputImage ->

                    recognizer.process(inputImage).addOnSuccessListener { visionText ->
                        val blockText = visionText.text.replace("\\s".toRegex(),"")
                        val firstArity:Int
                        val secondArity:Int
                        try {
                            if(blockText.length > 3){
                                firstArity = blockText.substring(0,1).toInt()
                                secondArity = blockText.substring(2,3).toInt()
                            }else{
                                firstArity = blockText.substring(0,1).toInt()
                                secondArity = blockText.substring(2).toInt()
                            }

                            val operand = blockText.substring(1,2)
                            operation(firstArity, secondArity, operand) {result ->
                                if(result != "Invalid"){
                                    val data = mapOf("result" to result, "mathExpression" to "$firstArity $operand $secondArity")
                                    onSuccess(data)
                                }else{
                                    onError(context.getString(R.string.invalid_oprand_msg))
                                }
                            }
                            dialogOpen.value = false
                        }catch (e:Exception){
                            onError(context.getString(R.string.invalid_inputs_msg))
                            dialogOpen.value = false
                        }
                    }.addOnFailureListener {e ->
                        onError(context.getString(R.string.operation_failure_msg,e.message))
                        dialogOpen.value = false
                    }
                }
            }
        }catch (e:IOException){
            onError(context.getString(R.string.operation_failure_msg,e.message))
            dialogOpen.value = false
        }
    }

    /*
    * Map the operand as per text recognition. Some arithmetic sign are sensitive to recognize, it require the clear image as source
    * */
    private fun operation(arity1:Int, arity2: Int, operand:String, onResult: (String) -> Unit){
        val result:Int
        when(operand){
            "+" -> {
                result = arity1 + arity2
                onResult.invoke(result.toString())
            }
            "-" -> {
                result =  arity1 - arity2
                onResult.invoke(result.toString())
            }
            "*" -> {
                result =  arity1 * arity2
                onResult.invoke(result.toString())
            }
            "X" -> {
                result =  arity1 * arity2
                onResult.invoke(result.toString())
            }
            "/" -> {
                result =  arity1 / arity2
                onResult.invoke(result.toString())
            }
            ":" -> {
                result =  arity1 / arity2
                onResult.invoke(result.toString())
            }
            else ->{
                onResult.invoke("Invalid")
            }
        }
    }
}