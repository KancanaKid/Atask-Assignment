package com.kancanakid.ataskassignment.app.ui.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.kancanakid.ataskassignment.app.ui.navigation.MainNavigation
import com.kancanakid.ataskassignment.app.ui.theme.AtaskAssignmentTheme
import com.kancanakid.ataskassignment.app.ui.viewmodels.SplashViewModel
import dagger.hilt.android.AndroidEntryPoint

/**
 * @author basyi
 * Created 6/6/2023 at 1:01 PM
 * purpose: Part as UI layer to handle user interaction and data inputs. It separated on any screens which carried by navigation
 */

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel:SplashViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen().apply {
            setKeepOnScreenCondition{
                viewModel.isLoading.value
            }
        }
        setContent {
            AtaskAssignmentTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainNavigation()
                }
            }
        }
    }
}