package com.example.schoolmanagementsystem.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.schoolmanagementsystem.domain.util.Resource
import com.example.schoolmanagementsystem.ui.theme.spacing
import kotlinx.coroutines.flow.collectLatest

enum class LoginType {
    STUDENT, TEACHER, ADMIN
}

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var loginType by remember { mutableStateOf(LoginType.STUDENT) }
    
    val loginState by viewModel.loginState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is AuthViewModel.UiEvent.LoginSuccess -> onLoginSuccess()
                is AuthViewModel.UiEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(event.message)
                }
                is AuthViewModel.UiEvent.Logout -> { /* No-op on login screen */ }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            when(loginType) {
                                LoginType.STUDENT -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                                LoginType.TEACHER -> MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.3f)
                                LoginType.ADMIN -> MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f)
                            },
                            MaterialTheme.colorScheme.background
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(spacing().spacing6)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // App Logo or Icon
                Surface(
                    modifier = Modifier.size(80.dp),
                    shape = RoundedCornerShape(20.dp),
                    color = when(loginType) {
                        LoginType.STUDENT -> MaterialTheme.colorScheme.primary
                        LoginType.TEACHER -> MaterialTheme.colorScheme.tertiary
                        LoginType.ADMIN -> MaterialTheme.colorScheme.secondary
                    }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = when(loginType) {
                                LoginType.STUDENT -> Icons.Default.School
                                LoginType.TEACHER -> Icons.Default.Person
                                LoginType.ADMIN -> Icons.Default.AdminPanelSettings
                            },
                            contentDescription = null,
                            modifier = Modifier.size(40.dp),
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(spacing().spacing6))

                Text(
                    text = when(loginType) {
                        LoginType.STUDENT -> "Student Login"
                        LoginType.TEACHER -> "Teacher Login"
                        LoginType.ADMIN -> "Admin Login"
                    },
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    ),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = when(loginType) {
                        LoginType.STUDENT -> "Access your learning dashboard"
                        LoginType.TEACHER -> "Manage your classes and students"
                        LoginType.ADMIN -> "System administration portal"
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(spacing().spacing8))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email Address") },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(spacing().spacing4))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                    trailingIcon = {
                        IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                            Icon(
                                imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = null
                            )
                        }
                    },
                    visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(spacing().spacing2))

                TextButton(
                    onClick = { /* Handle forgot password */ },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text(
                        "Forgot Password?",
                        style = MaterialTheme.typography.labelLarge,
                        color = when(loginType) {
                            LoginType.STUDENT -> MaterialTheme.colorScheme.primary
                            LoginType.TEACHER -> MaterialTheme.colorScheme.tertiary
                            LoginType.ADMIN -> MaterialTheme.colorScheme.secondary
                        }
                    )
                }

                Spacer(modifier = Modifier.height(spacing().spacing6))

                Button(
                    onClick = { viewModel.login(email, password) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    enabled = loginState !is Resource.Loading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = when(loginType) {
                            LoginType.STUDENT -> MaterialTheme.colorScheme.primary
                            LoginType.TEACHER -> MaterialTheme.colorScheme.tertiary
                            LoginType.ADMIN -> MaterialTheme.colorScheme.secondary
                        }
                    )
                ) {
                    if (loginState is Resource.Loading) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            "Login",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(spacing().spacing4))

                // Role Selector
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    TextButton(onClick = { loginType = LoginType.STUDENT }) {
                        Text("Student", color = if(loginType == LoginType.STUDENT) MaterialTheme.colorScheme.primary else Color.Gray)
                    }
                    TextButton(onClick = { loginType = LoginType.TEACHER }) {
                        Text("Teacher", color = if(loginType == LoginType.TEACHER) MaterialTheme.colorScheme.tertiary else Color.Gray)
                    }
                    TextButton(onClick = { loginType = LoginType.ADMIN }) {
                        Text("Admin", color = if(loginType == LoginType.ADMIN) MaterialTheme.colorScheme.secondary else Color.Gray)
                    }
                }
            }
        }
    }
}
