package com.example.schoolmanagementsystem.ui.me

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.schoolmanagementsystem.MainViewModel
import com.example.schoolmanagementsystem.R
import com.example.schoolmanagementsystem.ui.auth.AuthViewModel
import com.example.schoolmanagementsystem.ui.auth.BiometricAuthenticator
import com.example.schoolmanagementsystem.ui.dashboard.DashboardBottomNavigation
import com.example.schoolmanagementsystem.ui.navigation.Screen
import com.example.schoolmanagementsystem.ui.profile.ProfileViewModel
import com.example.schoolmanagementsystem.ui.theme.EliteGoldGradient
import com.example.schoolmanagementsystem.ui.theme.glassmorphic
import com.example.schoolmanagementsystem.ui.theme.spacing
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MeScreen(
    onNavigate: (String) -> Unit,
    profileViewModel: ProfileViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel(),
    mainViewModel: MainViewModel
) {
    val profileState by profileViewModel.state.collectAsState()
    val themeMode by mainViewModel.themeMode.collectAsState()
    val languageCode by mainViewModel.languageCode.collectAsState()
    val currentUser by authViewModel.currentUser.collectAsState()
    val isBiometricEnabled by authViewModel.isBiometricEnabled.collectAsState()
    
    val context = LocalContext.current
    val authenticator = remember { BiometricAuthenticator(context) }
    
    var showThemeDialog by remember { mutableStateOf(false) }
    var showLanguageDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        authViewModel.eventFlow.collectLatest { event ->
            if (event is AuthViewModel.UiEvent.Logout) {
                onNavigate(Screen.Login.route)
            }
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        stringResource(R.string.me), 
                        fontWeight = FontWeight.Bold,
                        style = TextStyle(brush = EliteGoldGradient)
                    ) 
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = Color.Unspecified
                )
            )
        },
        bottomBar = {
            DashboardBottomNavigation(currentRoute = Screen.Me.route, onNavigate = onNavigate)
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            // Header Section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(EliteGoldGradient),
                contentAlignment = Alignment.BottomCenter
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(bottom = 24.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(110.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f))
                            .padding(2.dp)
                            .background(Color(0xFF1E1E1E), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Rounded.Person, 
                            contentDescription = null, 
                            tint = Color(0xFFD4AF37), 
                            modifier = Modifier.size(70.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        currentUser?.name ?: profileState.name, 
                        color = Color(0xFF382900), 
                        fontSize = 24.sp, 
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "Elite ${currentUser?.role?.name ?: profileState.subtitle}", 
                        color = Color(0xFF382900).copy(alpha = 0.8f), 
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                stringResource(R.string.account_settings).uppercase(),
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
                style = MaterialTheme.typography.labelMedium.copy(letterSpacing = 1.sp),
                color = MaterialTheme.colorScheme.primary
            )

            SettingsItem(
                icon = Icons.Rounded.Person,
                title = stringResource(R.string.my_profile),
                onClick = { onNavigate(Screen.Profile.route) }
            )

            if (authenticator.isBiometricAvailable()) {
                SettingsItem(
                    icon = Icons.Rounded.Fingerprint,
                    title = "Biometric Authentication",
                    subtitle = if (isBiometricEnabled) "Enabled" else "Disabled",
                    onClick = { authViewModel.setBiometricEnabled(!isBiometricEnabled) }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                stringResource(R.string.app_preferences).uppercase(),
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
                style = MaterialTheme.typography.labelMedium.copy(letterSpacing = 1.sp),
                color = MaterialTheme.colorScheme.primary
            )

            SettingsItem(
                icon = Icons.Rounded.DarkMode,
                title = stringResource(R.string.theme_mode),
                subtitle = when(themeMode) {
                    "light" -> stringResource(R.string.light_mode)
                    "dark" -> stringResource(R.string.dark_mode)
                    else -> stringResource(R.string.system_default)
                },
                onClick = { showThemeDialog = true }
            )

            SettingsItem(
                icon = Icons.Rounded.Language,
                title = stringResource(R.string.language),
                subtitle = if (languageCode == "en") stringResource(R.string.english) else stringResource(R.string.hindi),
                onClick = { showLanguageDialog = true }
            )

            Spacer(modifier = Modifier.height(40.dp))
            
            Button(
                onClick = { authViewModel.logout() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.1f), contentColor = MaterialTheme.colorScheme.error),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.2f))
            ) {
                Text(
                    stringResource(R.string.logout), 
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    if (showThemeDialog) {
        AlertDialog(
            onDismissRequest = { showThemeDialog = false },
            containerColor = MaterialTheme.colorScheme.surface,
            title = { Text(stringResource(R.string.select_theme), style = TextStyle(brush = EliteGoldGradient)) },
            text = {
                Column {
                    ThemeOption(stringResource(R.string.system_default), "system", themeMode) { mainViewModel.setThemeMode("system"); showThemeDialog = false }
                    ThemeOption(stringResource(R.string.light_mode), "light", themeMode) { mainViewModel.setThemeMode("light"); showThemeDialog = false }
                    ThemeOption(stringResource(R.string.dark_mode), "dark", themeMode) { mainViewModel.setThemeMode("dark"); showThemeDialog = false }
                }
            },
            confirmButton = {
                TextButton(onClick = { showThemeDialog = false }) { Text(stringResource(R.string.cancel)) }
            }
        )
    }

    if (showLanguageDialog) {
        AlertDialog(
            onDismissRequest = { showLanguageDialog = false },
            containerColor = MaterialTheme.colorScheme.surface,
            title = { Text(stringResource(R.string.select_language), style = TextStyle(brush = EliteGoldGradient)) },
            text = {
                Column {
                    LanguageOption(stringResource(R.string.english), "en", languageCode) { mainViewModel.setLanguageCode("en"); showLanguageDialog = false }
                    LanguageOption(stringResource(R.string.hindi), "hi", languageCode) { mainViewModel.setLanguageCode("hi"); showLanguageDialog = false }
                }
            },
            confirmButton = {
                TextButton(onClick = { showLanguageDialog = false }) { Text(stringResource(R.string.cancel)) }
            }
        )
    }
}

@Composable
fun SettingsItem(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 6.dp)
            .glassmorphic()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(22.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                if (subtitle != null) {
                    Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.outline
            )
        }
    }
}

@Composable
fun ThemeOption(text: String, mode: String, currentMode: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = mode == currentMode, 
            onClick = onClick,
            colors = RadioButtonDefaults.colors(selectedColor = Color(0xFFD4AF37))
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(text, fontWeight = if(mode == currentMode) FontWeight.Bold else FontWeight.Normal)
    }
}

@Composable
fun LanguageOption(text: String, code: String, currentCode: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically) {
        RadioButton(
            selected = code == currentCode, 
            onClick = onClick,
            colors = RadioButtonDefaults.colors(selectedColor = Color(0xFFD4AF37))
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(text, fontWeight = if(code == currentCode) FontWeight.Bold else FontWeight.Normal)
    }
}
