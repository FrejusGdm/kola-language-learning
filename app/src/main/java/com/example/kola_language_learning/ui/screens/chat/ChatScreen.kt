package com.example.kola_language_learning.ui.screens.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
//import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
//import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
//import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.kola_language_learning.ui.screens.chat.components.ChatBubble
import com.example.kola_language_learning.ui.screens.chat.components.WaveformAnimation
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
//import java.util.jar.Manifest
import android.Manifest
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import com.example.kola_language_learning.data.model.ChatUIState


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ChatScreen(
    onNavigateBack: () -> Unit,
    viewModel: ChatViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    val permissionState = rememberPermissionState(
        permission = Manifest.permission.RECORD_AUDIO,
        onPermissionResult = { isGranted ->
            if (isGranted) {
                viewModel.onPermissionGranted()
            }
        }
    )


    // Request permission when needed
    val permissionNeeded by viewModel.permissionNeeded.collectAsState()
    LaunchedEffect(permissionNeeded) {
        if (permissionNeeded) {
            permissionState.launchPermissionRequest()
        }
    }




    val isRecording by viewModel.isRecording.collectAsState()
    val messages by viewModel.messages.collectAsState()


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        ChatTopBar(
            onBackClick = onNavigateBack,
            currentTopic = "Small Talk",
            uiState = uiState
        )

        Box(modifier = Modifier.weight(1f)) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                reverseLayout = true
            ) {
                items(messages) { message ->
                    ChatBubble(
                        message = message,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
            }
        }

        StatusIndicator(uiState = uiState)

        ChatBottomBar(
            isRecording = uiState is ChatUIState.Recording,
            onRecordClick = viewModel::toggleRecording,
            enabled = uiState !is ChatUIState.Playing &&
                    uiState !is ChatUIState.Connecting
        )
    }
}

@Composable
private fun ChatTopBar(
    onBackClick: () -> Unit,
    currentTopic: String,
    uiState: ChatUIState
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBackClick) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back"
            )
        }
        Text(
            text = currentTopic,
            style = MaterialTheme.typography.titleLarge
        )
        IconButton(onClick = {}) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = "More options"
            )
        }
    }
}

@Composable
private fun ChatBottomBar(
    isRecording: Boolean,
    onRecordClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        WaveformAnimation(
            isRecording = isRecording,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            IconButton(
                onClick = onRecordClick,
                modifier = Modifier
                    .size(64.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = CircleShape
                    )
            ) {
                Icon(
                    imageVector = if (isRecording)
                        Icons.Default.Stop else Icons.Default.Mic,
                    contentDescription = if (isRecording)
                        "Stop recording" else "Start recording",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}


@Composable
private fun StatusIndicator(uiState: ChatUIState) {
    AnimatedVisibility(
        visible = uiState !is ChatUIState.Idle,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(16.dp),
                color = MaterialTheme.colorScheme.primary,
                strokeWidth = 2.dp
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = when (uiState) {
                    is ChatUIState.Connecting -> "Connecting..."
                    is ChatUIState.Recording -> "Recording..."
                    is ChatUIState.Processing -> "Processing..."
                    is ChatUIState.Playing -> "Playing response..."
                    is ChatUIState.Error -> "Error: ${uiState.message}"
                    else -> ""
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun ChatBottomBar(
    isRecording: Boolean,
    onRecordClick: () -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        WaveformAnimation(
            isRecording = isRecording,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            IconButton(
                onClick = onRecordClick,
                enabled = enabled,
                modifier = Modifier
                    .size(64.dp)
                    .background(
                        color = if (enabled)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                        shape = CircleShape
                    )
            ) {
                Icon(
                    imageVector = if (isRecording)
                        Icons.Default.Stop else Icons.Default.Mic,
                    contentDescription = if (isRecording)
                        "Stop recording" else "Start recording",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}