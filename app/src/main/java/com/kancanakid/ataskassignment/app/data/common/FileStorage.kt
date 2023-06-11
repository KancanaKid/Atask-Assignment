package com.kancanakid.ataskassignment.app.data.common

import android.content.Context
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException

/**
 * @author basyi
 * Created 6/8/2023 at 12:14 PM
 * Purpose: to handle the file storage. The file will be find on /data/data/{app-package-name}/files/atask_assignment.text on device storage.
 * The text value decoded and encoded using base64 format as encryption - decryption purpose.
 */

fun saveData(data:String, fileName: String, applicationContext: Context, onSuccess : (Boolean) -> Unit, onError : (String) -> Unit){
    try {
        val input = base64Encode(data)
        getFile(applicationContext, fileName).appendText(input)
        getFile(applicationContext, fileName).appendText("\n")
        onSuccess(true)
    }catch(e:IOException) {
        onError(e.message!!)
    }
}

fun readData(fileName: String,  applicationContext: Context) : List<String>{
    return try {
        getFile(applicationContext, fileName).useLines {
            it.toList()
        }
    }catch (e:FileNotFoundException){
        e.message!!
        listOf()
    }catch (e:IOException){
        e.message!!
        listOf()
    }
}

internal fun getStorageLocation(applicationContext: Context):File = applicationContext.filesDir
internal fun getFile(applicationContext: Context, fileName:String):File = File(getStorageLocation(applicationContext), fileName)
internal fun base64Encode(data: String):String{
    return android.util.Base64.encodeToString(data.toByteArray(), android.util.Base64.DEFAULT)
}

internal fun base64Decode(data: String):String{
    return String(android.util.Base64.decode(data, android.util.Base64.DEFAULT))
}