package com.example.schoolmanagementsystem.frontend.ui.auth

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.Fingerprint
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.schoolmanagementsystem.backend.domain.util.Resource
import com.example.schoolmanagementsystem.frontend.ui.theme.EliteGoldGradient
import com.example.schoolmanagementsystem.frontend.ui.theme.glassmorphic
import com.example.schoolmanagementsystem.frontend.ui.theme.spacing
import kotlinx.coroutines.flow.collectLatest

enum class LoginType {
    STUDENT, TEACHER, ADMIN
}

@OptIn(ExperimentalAnimationApi::class)
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
    val isBiometricEnabled by viewModel.isBiometricEnabled.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    
    val context = LocalContext.current
    val authenticator = remember { BiometricAuthenticator(context) }

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
                            Color(0xFF1A1A1A), // Deep Charcoal
                            Color(0xFF121212)
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
                // Elite Logo Section
                Surface(
                    modifier = Modifier.size(100.dp),
                    shape = RoundedCornerShape(24.dp),
                    color = Color.Transparent
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(EliteGoldGradient)
                            .padding(2.dp)
                            .background(Color(0xFF121212), RoundedCornerShape(22.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = when(loginType) {
                                LoginType.STUDENT -> Icons.Default.School
                                LoginType.TEACHER -> Icons.Default.Person
                                LoginType.ADMIN -> Icons.Default.AdminPanelSettings
                            },
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = Color(0xFFD4AF37) // Elite Gold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(spacing().spacing6))

                AnimatedContent(
                    targetState = loginType,
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                    label = "LoginTypeAnimation"
                ) { targetType ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = when(targetType) {
                                LoginType.STUDENT -> "SIKSHA Student"
                                LoginType.TEACHER -> "SIKSHA Faculty"
                                LoginType.ADMIN -> "SIKSHA Admin"
                            },
                            style = MaterialTheme.typography.headlineLarge.copy(
                                brush = EliteGoldGradient,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 2.sp
                            )
                        )
                        Text(
                            text = when(targetType) {
                                LoginType.STUDENT -> "Access your premium dashboard"
                                LoginType.TEACHER -> "Manage your classes and students"
                                LoginType.ADMIN -> "School administration portal"
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.6f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(spacing().spacing8))

                // Glassmorphic Login Form
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .glassmorphic(),
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text("Email Address", color = Color.White.copy(alpha = 0.7f)) },
                            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = Color(0xFFD4AF37)) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFFD4AF37),
                                unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                cursorColor = Color(0xFFD4AF37),
                                focusedLabelColor = Color(0xFFD4AF37)
                            )
                        )

                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = { Text("Password", color = Color.White.copy(alpha = 0.7f)) },
                            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFFD4AF37)) },
                            trailingIcon = {
                                IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                    Icon(
                                        imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                        contentDescription = null,
                                        tint = Color.White.copy(alpha = 0.5f)
                                    )
                                }
                            },
                            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFFD4AF37),
                                unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                cursorColor = Color(0xFFD4AF37),
                                focusedLabelColor = Color(0xFFD4AF37)
                            )
                        )

                        TextButton(
                            onClick = { /* Handle forgot password */ },
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Text(
                                "Forgot Password?",
                                style = MaterialTheme.typography.labelLarge,
                                color = Color(0xFFD4AF37)
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(
                                onClick = { viewModel.login(email, password) },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(56.dp)
                                    .background(EliteGoldGradient, RoundedCornerShape(12.dp)),
                                shape = RoundedCornerShape(12.dp),
                                enabled = loginState !is Resource.Loading,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.Transparent,
                                    contentColor = Color(0xFF382900)
                                )
                            ) {
                                if (loginState is Resource.Loading) {
                                    CircularProgressIndicator(
                                        color = Color(0xFF382900),
                                        modifier = Modifier.size(24.dp),
                                        strokeWidth = 2.dp
                                    )
                                } else {
                                    Text(
                                        "LOGIN",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            letterSpacing = 2.sp
                                        )
                                    )
                                }
                            }
                            
                            // Elite Biometric Button
                            if (isBiometricEnabled && authenticator.isBiometricAvailable()) {
                                Surface(
                                    onClick = {
                                        authenticator.promptBiometricAuth(
                                            title = "Elite Authentication",
                                            subtitle = "Authenticate to access your account",
                                            negativeButtonText = "Use Password",
                                            onSuccess = {
                                                // In a real elite app, we'd log in with a stored token
                                                // For now, we simulate a success message or trigger a specific login
                                            },
                                            onError = { code, msg -> },
                                            onFailed = { }
                                        )
                                    },
                                    modifier = Modifier.size(56.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    color = Color.White.copy(alpha = 0.05f),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFD4AF37).copy(alpha = 0.3f))
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            Icons.Rounded.Fingerprint,
                                            contentDescription = "Biometric Login",
                                            tint = Color(0xFFD4AF37),
                                            modifier = Modifier.size(32.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(spacing().spacing8))

                // Elite Role Selector
                Card(
                    modifier = Modifier.glassmorphic(backgroundColor = Color.White.copy(alpha = 0.02f)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RoleSelectorItem("Student", loginType == LoginType.STUDENT) { loginType = LoginType.STUDENT }
                        VerticalDivider(modifier = Modifier.height(20.dp).padding(horizontal = 8.dp), color = Color.White.copy(alpha = 0.1f))
                        RoleSelectorItem("Faculty", loginType == LoginType.TEACHER) { loginType = LoginType.TEACHER }
                        VerticalDivider(modifier = Modifier.height(20.dp).padding(horizontal = 8.dp), color = Color.White.copy(alpha = 0.1f))
                        RoleSelectorItem("Admin", loginType == LoginType.ADMIN) { loginType = LoginType.ADMIN }
                    }
                }
            }
        }
    }
}

@Composable
fun RoleSelectorItem(label: String, isSelected: Boolean, onClick: () -> Unit) {
    TextButton(onClick = onClick) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = if(isSelected) FontWeight.Bold else FontWeight.Normal,
                brush = if(isSelected) EliteGoldGradient else null
            ),
            color = if(isSelected) Color.Unspecified else Color.Gray
        )
    }
}

