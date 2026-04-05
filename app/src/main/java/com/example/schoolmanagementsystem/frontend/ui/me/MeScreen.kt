package com.example.schoolmanagementsystem.frontend.ui.me

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.schoolmanagementsystem.frontend.MainViewModel
import com.example.schoolmanagementsystem.R
import com.example.schoolmanagementsystem.frontend.ui.auth.AuthViewModel
import com.example.schoolmanagementsystem.frontend.ui.auth.BiometricAuthenticator
import com.example.schoolmanagementsystem.frontend.ui.dashboard.DashboardBottomNavigation
import com.example.schoolmanagementsystem.frontend.ui.navigation.Screen
import com.example.schoolmanagementsystem.frontend.ui.profile.ProfileViewModel
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
        containerColor = Color(0xFF090C0E), // Ultra dark background
        topBar = {
            TopAppBar(
                title = { Text("Me", fontWeight = FontWeight.Bold, color = Color.White) },
                actions = {
                    IconButton(onClick = { /* Help */ }) {
                        Icon(Icons.Rounded.HelpOutline, contentDescription = null, tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
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
            // Profile Header Card - Matching Siksha Elite Style
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(32.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF111619))
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .padding(vertical = 32.dp, horizontal = 24.dp)
                            .fillMaxWidth()
                    ) {
                        Surface(
                            modifier = Modifier.size(100.dp),
                            shape = CircleShape,
                            color = Color.Transparent,
                            border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFFFFD54F))
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.Rounded.Person, 
                                    contentDescription = null, 
                                    tint = Color(0xFFFFD54F), 
                                    modifier = Modifier.size(60.dp)
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(20.dp))
                        
                        Text(
                            currentUser?.name ?: profileState.name, 
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Surface(
                            color = Color(0xFF1B2D24),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                "ELITE STUDENT", 
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                color = Color(0xFF4CAF50), 
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.ExtraBold, 
                                    letterSpacing = 1.sp
                                )
                            )
                        }
                    }
                }
            }

            Text(
                "Account Settings".uppercase(),
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
                style = MaterialTheme.typography.labelSmall.copy(
                    letterSpacing = 1.2.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray
                )
            )

            SettingsItem(
                icon = Icons.Rounded.Person,
                title = "Elite Profile",
                onClick = { onNavigate(Screen.Profile.route) }
            )

            if (authenticator.isBiometricAvailable()) {
                SettingsItem(
                    icon = Icons.Rounded.Fingerprint,
                    title = "Security & Biometrics",
                    subtitle = if (isBiometricEnabled) "Authentication active" else "Biometrics disabled",
                    onClick = { authViewModel.setBiometricEnabled(!isBiometricEnabled) }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                "App Preferences".uppercase(),
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
                style = MaterialTheme.typography.labelSmall.copy(
                    letterSpacing = 1.2.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray
                )
            )

            SettingsItem(
                icon = Icons.Rounded.DarkMode,
                title = "App Theme",
                subtitle = when(themeMode) {
                    "light" -> "Platinum Light"
                    "dark" -> "Siksha Dark"
                    else -> "System Default"
                },
                onClick = { showThemeDialog = true }
            )

            SettingsItem(
                icon = Icons.Rounded.Language,
                title = stringResource(R.string.language),
                subtitle = if (languageCode == "en") "English" else "Hindi",
                onClick = { showLanguageDialog = true }
            )

            Spacer(modifier = Modifier.height(32.dp))
            
            Button(
                onClick = { authViewModel.logout() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.1f), 
                    contentColor = MaterialTheme.colorScheme.error
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Rounded.Logout, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    stringResource(R.string.logout), 
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }
            
            Spacer(modifier = Modifier.height(48.dp))
        }
    }

    if (showThemeDialog) {
        AlertDialog(
            onDismissRequest = { showThemeDialog = false },
            containerColor = Color(0xFF111619),
            titleContentColor = Color.White,
            textContentColor = Color.Gray,
            title = { Text(stringResource(R.string.select_theme), fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    ThemeOption(stringResource(R.string.system_default), "system", themeMode) { mainViewModel.setThemeMode("system"); showThemeDialog = false }
                    ThemeOption("Platinum Light", "light", themeMode) { mainViewModel.setThemeMode("light"); showThemeDialog = false }
                    ThemeOption("Siksha Dark", "dark", themeMode) { mainViewModel.setThemeMode("dark"); showThemeDialog = false }
                }
            },
            confirmButton = {
                TextButton(onClick = { showThemeDialog = false }) { 
                    Text(stringResource(R.string.cancel), color = Color(0xFF4FC3F7)) 
                }
            }
        )
    }

    if (showLanguageDialog) {
        AlertDialog(
            onDismissRequest = { showLanguageDialog = false },
            containerColor = Color(0xFF111619),
            titleContentColor = Color.White,
            textContentColor = Color.Gray,
            title = { Text(stringResource(R.string.select_language), fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    LanguageOption(stringResource(R.string.english), "en", languageCode) { mainViewModel.setLanguageCode("en"); showLanguageDialog = false }
                    LanguageOption(stringResource(R.string.hindi), "hi", languageCode) { mainViewModel.setLanguageCode("hi"); showLanguageDialog = false }
                }
            },
            confirmButton = {
                TextButton(onClick = { showLanguageDialog = false }) { 
                    Text(stringResource(R.string.cancel), color = Color(0xFF4FC3F7)) 
                }
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
            .padding(horizontal = 20.dp, vertical = 6.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF111619))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(44.dp),
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFF1A2127)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, contentDescription = null, tint = Color(0xFF4FC3F7), modifier = Modifier.size(22.dp))
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
                if (subtitle != null) {
                    Text(
                        text = subtitle, 
                        style = MaterialTheme.typography.bodySmall, 
                        color = Color.Gray
                    )
                }
            }
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                contentDescription = null,
                tint = Color.Gray.copy(alpha = 0.5f)
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
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = mode == currentMode, 
            onClick = onClick,
            colors = RadioButtonDefaults.colors(
                selectedColor = Color(0xFF4FC3F7),
                unselectedColor = Color.Gray
            )
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text, 
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = if(mode == currentMode) FontWeight.Bold else FontWeight.Normal,
                color = if(mode == currentMode) Color.White else Color.Gray
            )
        )

    }
}

@Composable
fun LanguageOption(text: String, code: String, currentCode: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically) {
        RadioButton(
            selected = code == currentCode, 
            onClick = onClick,
            colors = RadioButtonDefaults.colors(
                selectedColor = Color(0xFF4FC3F7),
                unselectedColor = Color.Gray
            )
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text, 
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = if(code == currentCode) FontWeight.Bold else FontWeight.Normal,
                color = if(code == currentCode) Color.White else Color.Gray
            )
        )

    }
}
