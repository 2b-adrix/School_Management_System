package com.example.schoolmanagementsystem.frontend.ui.student

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.schoolmanagementsystem.backend.domain.util.Resource
import com.example.schoolmanagementsystem.frontend.ui.components.AppTextField
import com.example.schoolmanagementsystem.frontend.ui.components.PrimaryButton
import com.example.schoolmanagementsystem.frontend.ui.components.SchoolTopAppBar
import com.example.schoolmanagementsystem.frontend.ui.components.SectionTitle
import com.example.schoolmanagementsystem.frontend.ui.theme.spacing
import kotlinx.coroutines.flow.collectLatest

@Composable
fun AddStudentScreen(
    onNavigateBack: () -> Unit,
    viewModel: AddStudentViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var rollNumber by remember { mutableStateOf("") }
    var className by remember { mutableStateOf("") }
    var parentName by remember { mutableStateOf("") }
    var parentContact by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var dob by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    // Real-time Validation States
    val firstNameError = remember(firstName) { if (firstName.isNotEmpty() && firstName.length < 2) "Name too short" else null }
    val rollError = remember(rollNumber) { if (rollNumber.isNotEmpty() && !rollNumber.all { it.isDigit() }) "Only digits allowed" else null }
    val contactError = remember(parentContact) { if (parentContact.isNotEmpty() && parentContact.length < 10) "Invalid contact number" else null }

    val snackbarHostState = remember { SnackbarHostState() }
    val saveState by viewModel.saveState.collectAsState()

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
    }

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is AddStudentViewModel.UiEvent.SaveSuccess -> {
                    onNavigateBack()
                }
                is AddStudentViewModel.UiEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(
                        message = event.message
                    )
                }
            }
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            SchoolTopAppBar(
                title = "Register Student",
                onBackClick = onNavigateBack
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(spacing().spacing4),
            verticalArrangement = Arrangement.spacedBy(spacing().spacing4),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Profile Image Picker
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .clickable { launcher.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                if (imageUri != null) {
                    AsyncImage(
                        model = imageUri,
                        contentDescription = "Profile Image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = Icons.Rounded.AddAPhoto,
                        contentDescription = "Add Photo",
                        modifier = Modifier.size(32.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Text(
                text = "Add Student Photo",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(spacing().spacing2))

            SectionTitle(title = "Student Login Credentials")
            AppTextField(
                label = "Student Email ID *",
                value = email,
                onValueChange = { email = it },
                leadingIcon = Icons.Rounded.Email,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
            )
            AppTextField(
                label = "Login Password *",
                value = password,
                onValueChange = { password = it },
                leadingIcon = Icons.Rounded.Lock,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
            )

            SectionTitle(title = "Basic Information")
            AppTextField(
                label = "First Name *",
                value = firstName,
                onValueChange = { firstName = it },
                leadingIcon = Icons.Rounded.Person,
                isError = firstNameError != null,
                errorMessage = firstNameError,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
            )
            AppTextField(
                label = "Last Name *",
                value = lastName,
                onValueChange = { lastName = it },
                leadingIcon = Icons.Rounded.Person,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
            )
            AppTextField(
                label = "Roll Number *",
                value = rollNumber,
                onValueChange = { rollNumber = it },
                leadingIcon = Icons.Rounded.Badge,
                isError = rollError != null,
                errorMessage = rollError,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
            )
            AppTextField(
                label = "Class ID *",
                value = className,
                onValueChange = { className = it },
                leadingIcon = Icons.Rounded.Class,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
            )

            SectionTitle(title = "Guardian Details")
            AppTextField(
                label = "Parent/Guardian Name",
                value = parentName,
                onValueChange = { parentName = it },
                leadingIcon = Icons.Rounded.FamilyRestroom,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
            )
            AppTextField(
                label = "Parent Contact",
                value = parentContact,
                onValueChange = { parentContact = it },
                leadingIcon = Icons.Rounded.Phone,
                isError = contactError != null,
                errorMessage = contactError,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone, imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
            )

            SectionTitle(title = "Additional Details")
            AppTextField(
                label = "Address",
                value = address,
                onValueChange = { address = it },
                leadingIcon = Icons.Rounded.LocationOn,
                singleLine = false,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
            )
            AppTextField(
                label = "Date of Birth (YYYY-MM-DD)",
                value = dob,
                onValueChange = { dob = it },
                leadingIcon = Icons.Rounded.Cake,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() })
            )

            Spacer(modifier = Modifier.height(spacing().spacing4))

            PrimaryButton(
                text = "Register Student & Create ID",
                onClick = {
                    val bytes = imageUri?.let { uri ->
                        context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
                    }
                    viewModel.saveStudent(
                        firstName = firstName,
                        lastName = lastName,
                        rollNumber = rollNumber,
                        classId = className,
                        className = className,
                        parentName = parentName,
                        parentContact = parentContact,
                        address = address,
                        dob = dob,
                        email = email,
                        password = password,
                        imageBytes = bytes
                    )
                },
                isLoading = saveState is Resource.Loading,
                enabled = firstNameError == null && rollError == null && contactError == null && firstName.isNotEmpty() && rollNumber.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()
            )
            
            Spacer(modifier = Modifier.height(spacing().spacing8))
        }
    }
}

