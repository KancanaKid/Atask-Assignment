package com.kancanakid.ataskassignment.app.ui.common

/**
 * @author basyi
 * Created 6/6/2023 at 12:22 PM
 */
sealed class Screen(val route:String) {
    object HomeScreen : Screen ("home_route")
    object GalleryScreen : Screen("gallery_route")
    object CameraScreen  :Screen("camera_new_rout")

    fun withArgs(vararg args:String):String{
        return buildString {
            append(route)
            args.forEach { arg ->
                append("/$arg")
            }
        }
    }

    companion object {
        const val DATABASE_STORAGE = "database"
        const val FILE_STORAGE = "file"
    }
}