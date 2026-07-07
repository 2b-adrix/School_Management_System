package com.example.schoolmanagementsystem.frontend.ui.auth

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.Fingerprint
import androidx.compose.material.icons.rounded.School
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.schoolmanagementsystem.backend.domain.util.Resource
import kotlinx.coroutines.flow.collectLatest

enum class LoginType {
    STUDENT, TEACHER, ADMIN
}

@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterial3Api::class)
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
                is AuthViewModel.UiEvent.Logout -> { }
            }
        }
    }

    Scaffold(
        containerColor = Color(0xFF090C0E),
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 32.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Siksha Logo
                Surface(
                    modifier = Modifier.size(100.dp),
                    shape = RoundedCornerShape(24.dp),
                    color = Color.Transparent,
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFFFD54F))
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize().padding(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.School,
                            contentDescription = null,
                            modifier = Modifier.size(56.dp),
                            tint = Color(0xFFFFD54F)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

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
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFFD54F),
                                letterSpacing = 0.5.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Access your premium dashboard",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(48.dp))

                // Login Form
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        placeholder = { Text("Email Address", color = Color.Gray) },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = Color(0xFFFFD54F)) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFFFD54F),
                            unfocusedBorderColor = Color.DarkGray,
                            cursorColor = Color(0xFFFFD54F),
                            focusedContainerColor = Color(0xFF111619),
                            unfocusedContainerColor = Color(0xFF111619),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        placeholder = { Text("Password", color = Color.Gray) },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFFFFD54F)) },
                        trailingIcon = {
                            IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                Icon(
                                    imageVector = if (isPasswordVisible) Icons.Rounded.Visibility else Icons.Rounded.VisibilityOff,
                                    contentDescription = null,
                                    tint = Color.Gray
                                )
                            }
                        },
                        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFFFD54F),
                            unfocusedBorderColor = Color.DarkGray,
                            cursorColor = Color(0xFFFFD54F),
                            focusedContainerColor = Color(0xFF111619),
                            unfocusedContainerColor = Color(0xFF111619),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )

                    TextButton(
                        onClick = { /* Handle forgot password */ },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text(
                            "Forgot Password?",
                            style = MaterialTheme.typography.labelLarge,
                            color = Color(0xFFFFD54F)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = { viewModel.login(email, password) },
                            modifier = Modifier
                                .weight(1f)
                                .height(58.dp),
                            shape = RoundedCornerShape(16.dp),
                            enabled = loginState !is Resource.Loading,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent,
                                contentColor = Color.Black
                            ),
                            contentPadding = PaddingValues()
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.horizontalGradient(
                                            colors = listOf(Color(0xFFE6AF2E), Color(0xFFFFD54F))
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (loginState is Resource.Loading) {
                                    CircularProgressIndicator(color = Color.Black, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                                } else {
                                    Text(
                                        "LOGIN",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            letterSpacing = 1.2.sp
                                        )
                                    )
                                }
                            }
                        }

                        if (isBiometricEnabled && authenticator.isBiometricAvailable()) {
                            IconButton(
                                onClick = {
                                    authenticator.promptBiometricAuth(
                                        title = "SIKSHA Authentication",
                                        subtitle = "Authenticate to access your account",
                                        negativeButtonText = "Use Password",
                                        onSuccess = { },
                                        onError = { _, _ -> },
                                        onFailed = { }
                                    )
                                },
                                modifier = Modifier
                                    .size(58.dp)
                                    .border(1.5.dp, Color.DarkGray, RoundedCornerShape(16.dp))
                                    .background(Color(0xFF111619), RoundedCornerShape(16.dp))
                            ) {
                                Icon(
                                    Icons.Rounded.Fingerprint,
                                    contentDescription = "Biometric Login",
                                    tint = Color(0xFFFFD54F),
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(48.dp))

                // Role Selector
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RoleSelectorItem("Student", loginType == LoginType.STUDENT) { loginType = LoginType.STUDENT }
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(modifier = Modifier.width(1.dp).height(16.dp).background(Color.DarkGray))
                    Spacer(modifier = Modifier.width(8.dp))
                    RoleSelectorItem("Faculty", loginType == LoginType.TEACHER) { loginType = LoginType.TEACHER }
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(modifier = Modifier.width(1.dp).height(16.dp).background(Color.DarkGray))
                    Spacer(modifier = Modifier.width(8.dp))
                    RoleSelectorItem("Admin", loginType == LoginType.ADMIN) { loginType = LoginType.ADMIN }
                }
            }
        }
    }
}

@Composable
fun RoleSelectorItem(label: String, isSelected: Boolean, onClick: () -> Unit) {
    Text(
        text = label,
        modifier = Modifier
            .clickable { onClick() }
            .padding(horizontal = 4.dp, vertical = 8.dp),
        color = if (isSelected) Color(0xFFFFD54F) else Color.DarkGray,
        style = MaterialTheme.typography.labelLarge.copy(
            fontWeight = if(isSelected) FontWeight.Bold else FontWeight.Normal,
            fontSize = 14.sp
        )
    )
}
