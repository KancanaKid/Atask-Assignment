package com.kancanakid.ataskassignment.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * @author basyi
 * Created 6/6/2023 at 12:32 PM
 */
@Entity
data class Expression(
    val expression:String,
    val result:String
){
    @PrimaryKey(autoGenerate = true)
    var uid:Int = 0
}


