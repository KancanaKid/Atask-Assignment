package com.kancanakid.ataskassignment.app.ui.screens.gallery

import android.annotation.SuppressLint
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.kancanakid.ataskassignment.app.R
import com.kancanakid.ataskassignment.app.ui.common.DialogBoxLoading
import com.kancanakid.ataskassignment.app.ui.common.ProceedState
import com.kancanakid.ataskassignment.app.ui.common.Screen
import com.kancanakid.ataskassignment.app.ui.theme.BlackColor
import com.kancanakid.ataskassignment.app.ui.theme.Gray200
import com.kancanakid.ataskassignment.app.ui.theme.Gray300
import com.kancanakid.ataskassignment.app.ui.theme.WhiteColor
import com.kancanakid.ataskassignment.app.ui.viewmodels.MainViewModel
import kotlinx.coroutines.launch

/**
 * @author basyi
 * Created 6/7/2023 at 11:33 AM
 * Purpose: to handle the arithmetic operation based on text recognition on image. The image source are coming from gallery folders images .
 */

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun GalleryScreen(navController: NavController,
                  modifier: Modifier = Modifier,
                  viewModel: MainViewModel = hiltViewModel(),
                  storage:String?) {

    val context = LocalContext.current
    var hasImage by remember {
        mutableStateOf(false)
    }
    var imageUri by remember {
        mutableStateOf<Uri?>(null)
    }
    var result by remember {
        mutableStateOf<Map<String, String>>(mapOf())
    }
    var resultBoxVisibility by remember {
        mutableStateOf(false)
    }
    var proceedState by remember {
        mutableStateOf(ProceedState.TAKE_PICTURE)
    }
    var imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            hasImage = uri != null
            imageUri = uri
            proceedState = ProceedState.PROCEED_RESULT
        }
    )
    val scaffoldState: ScaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()
    val dialogOpen by viewModel.dialogOpen.observeAsState(initial = false)

    Scaffold(
        scaffoldState = scaffoldState,
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(id = R.string.browse_picture), color = WhiteColor)
                },
                backgroundColor = MaterialTheme.colorScheme.primary,
                modifier = modifier.fillMaxWidth(),
                navigationIcon = {
                    IconButton(onClick = { navController.navigate(Screen.HomeScreen.route) }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "", tint = WhiteColor)
                    }
                },
                elevation = 8.dp
            )
        }
    ) {
        if (dialogOpen) DialogBoxLoading()
        viewModel.insertLiveData.observe(LocalLifecycleOwner.current){
            if(it) navController.navigate(Screen.HomeScreen.route)
            else{
                scope.launch {
                    scaffoldState.snackbarHostState.showSnackbar("Saving Failure", duration = SnackbarDuration.Short)
                }
            }
        }

        Column(modifier = modifier.padding(16.dp)) {
            Box(modifier = modifier
                .fillMaxWidth()
                .height(400.dp)
                .padding(8.dp)
                .background(color = Gray300, shape = RoundedCornerShape(4.dp)),

                ) {
                if(hasImage && imageUri != null) {

                    AsyncImage(
                        model = imageUri,
                        modifier = modifier
                            .fillMaxSize(),
                        placeholder = painterResource(id = R.drawable.ic_launcher_background),
                        contentDescription = "Selected image")

                }
            }
            AnimatedVisibility(visible = !resultBoxVisibility) {
                OutlinedButton(
                    modifier = modifier.fillMaxWidth(),
                    onClick = {
                        if(proceedState == ProceedState.TAKE_PICTURE) {
                            // browse picture
                            imagePicker.launch("image/*")
                        }else{
                            // process the result
                            viewModel.dialogOpen.value = true
                            viewModel.processImage(imageUri, context, onSuccess = {
                                result = it
                                resultBoxVisibility = true
                            }, onError = {
                                scope.launch {
                                    scaffoldState.snackbarHostState.showSnackbar(it, duration = SnackbarDuration.Short)
                                }
                                proceedState = ProceedState.TAKE_PICTURE
                            })
                        }
                    }, colors = ButtonDefaults.outlinedButtonColors(
                        MaterialTheme.colorScheme.primary
                    )) {
                    Text(
                        text = if(proceedState == ProceedState.TAKE_PICTURE) context.getString(R.string.browse_picture) else context.getString(R.string.process_operation),
                        color = WhiteColor
                    )
                }
            }
            Spacer(modifier = modifier
                .fillMaxWidth()
                .height(16.dp))
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
                            text = "Save",
                            color = WhiteColor
                        )
                    }
                }
            }
        }
    }
}