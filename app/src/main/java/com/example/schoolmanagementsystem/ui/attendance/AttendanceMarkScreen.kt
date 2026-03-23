package com.example.schoolmanagementsystem.ui.attendance

import android.graphics.Bitmap
import android.graphics.Matrix
import androidx.camera.core.*
import androidx.camera.view.PreviewView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.schoolmanagementsystem.ui.components.AppCard
import com.example.schoolmanagementsystem.ui.components.LoadingScreen
import com.example.schoolmanagementsystem.ui.components.SchoolTopAppBar
import com.example.schoolmanagementsystem.ui.theme.spacing
import kotlinx.coroutines.flow.collectLatest
import java.util.concurrent.Executors

@Composable
fun AttendanceMarkScreen(
    onNavigateBack: () -> Unit,
    viewModel: AttendanceViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var showAiScanner by remember { mutableStateOf(false) }
    var currentAiMode by remember { mutableStateOf(AttendanceViewModel.AiMode.NONE) }

    LaunchedEffect(Unit) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is AttendanceViewModel.UiEvent.SaveSuccess -> onNavigateBack()
                is AttendanceViewModel.UiEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(event.message)
                }
            }
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            SchoolTopAppBar(
                title = "Mark Attendance",
                onBackClick = onNavigateBack,
                actions = {
                    IconButton(onClick = { 
                        showAiScanner = true 
                        currentAiMode = AttendanceViewModel.AiMode.FACE
                    }) {
                        Icon(Icons.Rounded.AutoAwesome, "AI Scanner", tint = MaterialTheme.colorScheme.primary)
                    }
                    Button(
                        onClick = { viewModel.saveAttendance() },
                        shape = RoundedCornerShape(12.dp),
                        enabled = !state.isLoading && !state.isSaving
                    ) {
                        if (state.isSaving) {
                            CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                        } else {
                            Text("Save")
                        }
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (state.isLoading) {
                LoadingScreen()
            } else {
                Column(modifier = Modifier.fillMaxSize()) {
                    // Summary Header
                    Surface(
                        color = MaterialTheme.colorScheme.surface,
                        tonalElevation = 2.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp).fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            val presentCount = state.attendanceRecords.values.count { it.isPresent }
                            AttendanceSummaryItem("Total", state.students.size.toString(), MaterialTheme.colorScheme.primary)
                            AttendanceSummaryItem("Present", presentCount.toString(), Color(0xFF4CAF50))
                            AttendanceSummaryItem("Absent", (state.students.size - presentCount).toString(), MaterialTheme.colorScheme.error)
                        }
                    }

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(state.students) { student ->
                            val record = state.attendanceRecords[student.id]
                            AttendanceRow(
                                studentName = "${student.firstName} ${student.lastName}",
                                rollNumber = student.rollNumber,
                                isPresent = record?.isPresent ?: false,
                                onToggle = { viewModel.onAttendanceChanged(student.id, it) }
                            )
                        }
                    }
                }
            }

            // --- AI Scanner Overlay ---
            if (showAiScanner) {
                AiScannerOverlay(
                    mode = currentAiMode,
                    onClose = { showAiScanner = false },
                    onCapture = { bitmap ->
                        when(currentAiMode) {
                            AttendanceViewModel.AiMode.FACE -> viewModel.onFaceCaptured(bitmap)
                            AttendanceViewModel.AiMode.QR -> viewModel.onQrScanned(bitmap)
                            else -> {}
                        }
                    },
                    onModeChange = { currentAiMode = it },
                    isProcessing = state.isAiProcessing
                )
            }
        }
    }
}

@Composable
fun AiScannerOverlay(
    mode: AttendanceViewModel.AiMode,
    onClose: () -> Unit,
    onCapture: (Bitmap) -> Unit,
    onModeChange: (AttendanceViewModel.AiMode) -> Unit,
    isProcessing: Boolean
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }
    val previewView = remember { PreviewView(context) }
    
    var imageCapture: ImageCapture? by remember { mutableStateOf(null) }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        AndroidView(
            factory = { previewView },
            modifier = Modifier.fillMaxSize()
        ) {
            val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()
                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }
                imageCapture = ImageCapture.Builder().build()
                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                try {
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, preview, imageCapture)
                } catch (e: Exception) { e.printStackTrace() }
            }, ContextCompat.getMainExecutor(context))
        }

        // UI Controls
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onClose) {
                    Icon(Icons.Rounded.Close, null, tint = Color.White)
                }
                Text("AI Attendance", color = Color.White, style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.width(48.dp))
            }

            Spacer(modifier = Modifier.weight(1f))

            // Mode Selector
            Surface(
                color = Color.Black.copy(alpha = 0.6f),
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                shape = RoundedCornerShape(24.dp)
            ) {
                Row(
                    modifier = Modifier.padding(8.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    AiModeButton(Icons.Rounded.Face, "Face", mode == AttendanceViewModel.AiMode.FACE) { 
                        onModeChange(AttendanceViewModel.AiMode.FACE) 
                    }
                    AiModeButton(Icons.Rounded.QrCodeScanner, "QR", mode == AttendanceViewModel.AiMode.QR) { 
                        onModeChange(AttendanceViewModel.AiMode.QR) 
                    }
                    AiModeButton(Icons.Rounded.Mic, "Voice", mode == AttendanceViewModel.AiMode.VOICE) { 
                        onModeChange(AttendanceViewModel.AiMode.VOICE) 
                    }
                }
            }

            // Capture Button
            Box(
                modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp),
                contentAlignment = Alignment.Center
            ) {
                if (isProcessing) {
                    CircularProgressIndicator(color = Color.White)
                } else {
                    IconButton(
                        onClick = {
                            imageCapture?.takePicture(cameraExecutor, object : ImageCapture.OnImageCapturedCallback() {
                                override fun onCaptureSuccess(image: ImageProxy) {
                                    val bitmap = image.toBitmap()
                                    onCapture(bitmap)
                                    image.close()
                                }
                            })
                        },
                        modifier = Modifier.size(72.dp).background(Color.White, CircleShape)
                    ) {
                        Icon(Icons.Rounded.CameraAlt, null, tint = Color.Black, modifier = Modifier.size(32.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun AiModeButton(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, isSelected: Boolean, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        IconButton(
            onClick = onClick,
            modifier = Modifier.background(if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent, CircleShape)
        ) {
            Icon(icon, null, tint = if (isSelected) Color.White else Color.Gray)
        }
        Text(label, color = if (isSelected) Color.White else Color.Gray, style = MaterialTheme.typography.labelSmall)
    }
}

@Composable
fun AttendanceSummaryItem(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), color = color)
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun AttendanceRow(studentName: String, rollNumber: String, isPresent: Boolean, onToggle: (Boolean) -> Unit) {
    AppCard {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(40.dp),
                shape = CircleShape,
                color = if (isPresent) Color(0xFF4CAF50).copy(alpha = 0.1f) else MaterialTheme.colorScheme.surfaceVariant
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = if (isPresent) Icons.Rounded.CheckCircle else Icons.Rounded.Person,
                        contentDescription = null,
                        tint = if (isPresent) Color(0xFF4CAF50) else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(studentName, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold))
                Text("Roll No: $rollNumber", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Switch(checked = isPresent, onCheckedChange = onToggle)
        }
    }
}

// Extension to convert ImageProxy to Bitmap
fun ImageProxy.toBitmap(): Bitmap {
    val buffer = planes[0].buffer
    val bytes = ByteArray(buffer.remaining())
    buffer.get(bytes)
    val bitmap = android.graphics.BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    val matrix = Matrix()
    matrix.postRotate(imageInfo.rotationDegrees.toFloat())
    return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
}
