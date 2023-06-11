package com.kancanakid.ataskassignment.app.ui.screens.home

import android.Manifest
import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.RadioButton
import androidx.compose.material.RadioButtonDefaults
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.kancanakid.ataskassignment.app.BuildConfig
import com.kancanakid.ataskassignment.app.R
import com.kancanakid.ataskassignment.app.data.model.Expression
import com.kancanakid.ataskassignment.app.ui.common.Screen
import com.kancanakid.ataskassignment.app.ui.common.UiState
import com.kancanakid.ataskassignment.app.ui.theme.AtaskAssignmentTheme
import com.kancanakid.ataskassignment.app.ui.theme.Gray200
import com.kancanakid.ataskassignment.app.ui.theme.Gray300
import com.kancanakid.ataskassignment.app.ui.theme.WhiteColor
import com.kancanakid.ataskassignment.app.ui.viewmodels.MainViewModel

/**
 * @author basyi
 * Created 6/6/2023 at 12:14 PM
 */
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun HomeScreen(navController: NavController, modifier: Modifier = Modifier, viewModel: MainViewModel = hiltViewModel()) {
    val scaffoldState: ScaffoldState = rememberScaffoldState()
    val isBuiltInCamera = BuildConfig.FLAVOR_type == "builtincamera"
    var checkPermissionState by remember {
        mutableStateOf(false)
    }
    val context = LocalContext.current
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val radioOptions = listOf("Use Database Storage", "Use File Storage")
    val (selectedOption, onOptionSelected) = remember {
        mutableStateOf(radioOptions[0])
    }
    var storageTypeSelection by rememberSaveable {
        mutableStateOf(Screen.DATABASE_STORAGE)
    }

    val listFromDb by produceState<UiState>(initialValue = UiState.Loading, key1 = lifecycle, key2 = viewModel ){
        lifecycle.repeatOnLifecycle(state = Lifecycle.State.STARTED){
            viewModel.uiListState.collect{value = it}
        }
    }

    val listFromFile by produceState<UiState>(initialValue = UiState.Loading, key1 = lifecycle, key2 = viewModel ){
        lifecycle.repeatOnLifecycle(state = Lifecycle.State.STARTED){
            viewModel.uiListFromFileState.collect{value = it}
        }
    }

    Scaffold(
        scaffoldState = scaffoldState,
        modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    checkPermissionState = true
                },
                backgroundColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(start = 8.dp, end = 8.dp)
            ) {
                Icon(
                    if (isBuiltInCamera) Icons.Default.Camera else Icons.Default.Image,
                    contentDescription = "", modifier = Modifier.size(30.dp), tint = WhiteColor
                )
            }
        }, topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(id = R.string.app_name), color = WhiteColor)
                },
                backgroundColor = MaterialTheme.colorScheme.primary,
                modifier = modifier.fillMaxWidth(),
                elevation = 8.dp
            )
        }) {
        if (checkPermissionState) {
            CheckingRequiredPermissions(
                isCamera = isBuiltInCamera,
                navController = navController, storageTypeSelection
            )
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
                .background(color = MaterialTheme.colorScheme.surface)
        ) {

            //Filter Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(5.dp))
                    .border(
                        BorderStroke(width = 1.dp, color = Gray300)
                    )
                    .background(color = Gray200)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 8.dp)
                ) {
                    radioOptions.forEach { text ->
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .selectable(
                                    selected = (text == selectedOption),
                                    onClick = {
                                        onOptionSelected(text)
                                        storageTypeSelection =
                                            if (text == radioOptions[0]) Screen.DATABASE_STORAGE
                                            else Screen.FILE_STORAGE
                                    })
                        ) {
                            Text(text = text)
                            RadioButton(
                                selected = (text == selectedOption),
                                onClick = {
                                    onOptionSelected(text)
                                    storageTypeSelection =
                                        if (text == radioOptions[0]) Screen.DATABASE_STORAGE
                                        else Screen.FILE_STORAGE
                                },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = MaterialTheme.colorScheme.primary,
                                    unselectedColor = Color.DarkGray,
                                    disabledColor = Color.LightGray
                                )
                            )
                        }
                    }
                }
            }

            Spacer(modifier = modifier.fillMaxWidth().height(16.dp))
            //Content List
            val listState = rememberLazyListState()
            Column(modifier = Modifier.fillMaxWidth()) {
                LazyColumn(state = listState) {
                    if(storageTypeSelection == Screen.DATABASE_STORAGE){
                        if(listFromDb is UiState.Success){
                            val items = (listFromDb as UiState.Success).data as List<Expression>
                            items(items) {
                                ResultItems(result = it)
                            }
                        }
                    }else{
                        if(listFromFile is UiState.Success){
                            val items = (listFromFile as UiState.Success).data as List<Expression>
                            items(items) {
                                ResultItems(result = it)
                            }
                        }
                    }

                }
            }
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
internal fun CheckingRequiredPermissions(
    isCamera: Boolean,
    navController: NavController,
    storageSelection: String
) {
    val permissionState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
    )
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(key1 = lifecycleOwner, effect = {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> {
                    permissionState.launchMultiplePermissionRequest()
                }

                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    })
    if (permissionState.allPermissionsGranted) {
        if (isCamera) {
            navController.navigate(
                Screen.CameraScreen.withArgs(
                    storageSelection
                )
            )
        } else navController.navigate(Screen.GalleryScreen.withArgs(storageSelection))
    }
}

@Preview
@Composable
fun HomeScreenPreview() {
    AtaskAssignmentTheme {
        HomeScreen(navController = rememberNavController())
    }
}
