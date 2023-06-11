package com.kancanakid.ataskassignment.app.ui.screens.camera

import android.annotation.SuppressLint
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.kancanakid.ataskassignment.app.R
import com.kancanakid.ataskassignment.app.ui.common.DialogBoxLoading
import com.kancanakid.ataskassignment.app.ui.common.ProceedState
import com.kancanakid.ataskassignment.app.ui.common.Screen
import com.kancanakid.ataskassignment.app.ui.common.getOutputDirectory
import com.kancanakid.ataskassignment.app.ui.theme.BlackColor
import com.kancanakid.ataskassignment.app.ui.theme.Gray200
import com.kancanakid.ataskassignment.app.ui.theme.Gray300
import com.kancanakid.ataskassignment.app.ui.theme.WhiteColor
import com.kancanakid.ataskassignment.app.ui.viewmodels.MainViewModel
import kotlinx.coroutines.launch
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * @author basyi
 * Created 6/10/2023 at 4:55 PM
 * Purpose: to handle the arithmetic operation based on text recognition on image. It will calling CameraView to capture the image.
 */

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun CameraScreen(navController: NavController,
                 modifier: Modifier = Modifier,
                 viewModel: MainViewModel = hiltViewModel(),
                 storage:String?) {
    val scaffoldState: ScaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val outputDirectory: File = context.getOutputDirectory(context)
    val cameraExecutor: ExecutorService = Executors.newSingleThreadExecutor()
    var shouldShowCamera by remember {
        mutableStateOf(false)
    }
    var photoUri: Uri? = null
    var shouldShowPhoto by remember {
        mutableStateOf(false)
    }
    var proceedState by remember {
        mutableStateOf(ProceedState.TAKE_PICTURE)
    }
    var result by remember {
        mutableStateOf<Map<String, String>>(mapOf())
    }
    var resultBoxVisibility by remember {
        mutableStateOf(false)
    }
    val dialogOpen by viewModel.dialogOpen.observeAsState(initial = false)

    Scaffold(
        scaffoldState = scaffoldState,
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(id = R.string.camera_screen), color = WhiteColor)
                },
                backgroundColor = MaterialTheme.colorScheme.primary,
                modifier = modifier.fillMaxWidth(),
                navigationIcon = {
                    IconButton(onClick = {
                        navController.navigate(Screen.HomeScreen.route)
                        cameraExecutor.shutdown()
                    }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "", tint = WhiteColor)
                    }
                },
                elevation = 8.dp
            )
        }) {

        // the camera will only showing while the state is have true value
        if(shouldShowCamera){
            CameraView(
                outputDirectory = outputDirectory,
                executor = cameraExecutor,
                onImageCaptured = {
                    shouldShowCamera = false

                    photoUri = it
                    shouldShowPhoto = true
                    proceedState = ProceedState.PROCEED_RESULT
                } , onError = {
                    scope.launch {
                        scaffoldState.snackbarHostState.showSnackbar(context.getString(R.string.capture_failure_msg), duration = SnackbarDuration.Short)
                    }
                })
        }
        if (dialogOpen) DialogBoxLoading()

        // observe the live data for inserting process both for database or file as storage
        viewModel.insertLiveData.observe(LocalLifecycleOwner.current){
            if(it) navController.navigate(Screen.HomeScreen.route)
            else{
                scope.launch {
                    scaffoldState.snackbarHostState.showSnackbar(context.getString(R.string.save_failure_msg), duration = SnackbarDuration.Short)
                }
            }
        }

        // the view component will only showing while the camera is close or the image captured
        AnimatedVisibility(visible = !shouldShowCamera) {
            Column(modifier = modifier.padding(16.dp)) {
                Box(
                    modifier = modifier
                        .fillMaxWidth()
                        .height(400.dp)
                        .padding(8.dp)
                        .background(color = Gray300, shape = RoundedCornerShape(4.dp)),

                    ) {

                    if(shouldShowPhoto){
                        Image(painter = rememberAsyncImagePainter(photoUri), contentDescription = "", modifier = modifier.fillMaxSize())
                    }
                }
                Spacer(modifier = modifier
                    .fillMaxWidth()
                    .height(16.dp))
                AnimatedVisibility(visible = !resultBoxVisibility) {
                    OutlinedButton(onClick = {
                        if(proceedState == ProceedState.TAKE_PICTURE){
                            shouldShowCamera = true
                        }else{
                            viewModel.dialogOpen.value = true
                            viewModel.processImage(photoUri, context, onSuccess = {
                                result = it
                                resultBoxVisibility = true
                            }, onError = {
                                scope.launch {
                                    scaffoldState.snackbarHostState.showSnackbar(it, duration = SnackbarDuration.Short)
                                }
                                proceedState = ProceedState.TAKE_PICTURE
                            })
                        }
                    }, modifier = modifier.fillMaxWidth(), colors = ButtonDefaults.outlinedButtonColors(
                        MaterialTheme.colorScheme.primary
                    )) {
                        Text(
                            text = if(proceedState == ProceedState.TAKE_PICTURE) context.getString(R.string.take_picture) else context.getString(R.string.process_operation),
                            color = WhiteColor
                        )
                    }
                }
                Spacer(modifier = modifier
                    .fillMaxWidth()
                    .height(8.dp))
                AnimatedVisibility(visible = resultBoxVisibility) {
                    Column(modifier = modifier.fillMaxWidth()) {
                        Box (modifier = modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .clip(RoundedCornerShape(5.dp))
                            .border(BorderStroke(width = 1.dp, color = Gray300))
                            .background(color = Gray200)) {
                            Text(
                                text = if(result["result"] != null) "${result["mathExpression"]} = ${result["result"]}" else "",
                                style = MaterialTheme.typography.displayMedium,
                                color = BlackColor,
                                textAlign = TextAlign.Center,
                                modifier = modifier.fillMaxWidth()
                            )
                        }
                        Spacer(modifier = modifier
                            .fillMaxWidth()
                            .height(16.dp))
                        OutlinedButton(
                            modifier = modifier.fillMaxWidth(),
                            onClick = {
                                result.let {
                                    it["mathExpression"]?.let { it1 -> it["result"]?.let { it2 ->
                                        viewModel.insert(it1,
                                            it2, storage!!
                                        )
                                    } }
                                }

                            }, colors = ButtonDefaults.outlinedButtonColors(
                                MaterialTheme.colorScheme.primary
                            )) {
                            Text(
                                text = context.getString(R.string.save),
                                color = WhiteColor
                            )
                        }
                    }
                }
            }
        }
    }
}