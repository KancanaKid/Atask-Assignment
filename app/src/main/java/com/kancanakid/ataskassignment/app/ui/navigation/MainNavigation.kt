package com.kancanakid.ataskassignment.app.ui.navigation

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.kancanakid.ataskassignment.app.ui.common.Screen
import com.kancanakid.ataskassignment.app.ui.screens.gallery.GalleryScreen
import com.kancanakid.ataskassignment.app.ui.screens.camera.CameraScreen
import com.kancanakid.ataskassignment.app.ui.screens.home.HomeScreen

/**
 * @author basyi
 * Created 6/6/2023 at 12:10 PM
 */

@Composable
fun MainNavigation(){
    val navController = rememberNavController()
    val systemUiController = rememberSystemUiController()
    val useDarkIcons = !isSystemInDarkTheme()
    val darkColor = MaterialTheme.colorScheme.primary

    SideEffect {
        systemUiController.setSystemBarsColor(
            color = darkColor,
            darkIcons = useDarkIcons
        )
    }
    NavHost(navController = navController, startDestination = Screen.HomeScreen.route){
        composable(Screen.HomeScreen.route){
            HomeScreen(navController = navController)
        }
        composable(Screen.CameraScreen.route + "/{storage}", arguments = listOf(
            navArgument("storage"){
                type = NavType.StringType
                defaultValue = Screen.DATABASE_STORAGE
                nullable = true
            }
        )){
            CameraScreen(navController = navController, storage = it.arguments?.getString("storage"))
        }
        composable(Screen.GalleryScreen.route + "/{storage}", arguments = listOf(
            navArgument("storage"){
                type = NavType.StringType
                defaultValue = Screen.DATABASE_STORAGE
                nullable = true
            }
        )){
            GalleryScreen(navController = navController, storage = it.arguments?.getString("storage"))
        }
    }
}