package com.kancanakid.ataskassignment.app.ui.common

import android.content.Context
import com.kancanakid.ataskassignment.app.R
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date

/**
 * @author basyi
 * Created 6/7/2023 at 3:38 PM
 *
 */

fun Context.createImageFile(): File {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
    val imageFileName = "JPEG_" + timeStamp + "_"
    return File.createTempFile(
        imageFileName, ".jpg", externalCacheDir
    )
}

fun Context.getOutputDirectory(context: Context): File {
    val mediaDir = context.externalMediaDirs.firstOrNull()?.let {
        File(it, context.getString(R.string.app_name)).apply { mkdirs() }
    }

    return if (mediaDir != null && mediaDir.exists()) mediaDir else context.filesDir
}