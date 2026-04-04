package com.example.schoolmanagementsystem.ui.me

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import com.example.schoolmanagementsystem.ui.theme.PremiumBlueGradient
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
        containerColor = Color(0xFF0F172A), // Premium Deep Slate
        topBar = {
            Column(
                modifier = Modifier
                    .background(PremiumBlueGradient)
                    .statusBarsPadding()
                    .padding(bottom = 20.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        stringResource(R.string.me),
                        color = Color.White,
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    
                    Surface(
                        color = Color.White.copy(alpha = 0.1f),
                        shape = CircleShape,
                        onClick = { /* Help */ }
                    ) {
                        Icon(
                            Icons.Rounded.HelpOutline,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.padding(8.dp).size(20.dp)
                        )
                    }
                }
            }
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
            // Header Section with Glass Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .glassmorphic(
                            backgroundColor = Color.White.copy(alpha = 0.05f),
                            borderColor = Color.White.copy(alpha = 0.1f)
                        ),
                    shape = RoundedCornerShape(32.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(vertical = 32.dp, horizontal = 24.dp).fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape)
                                .background(EliteGoldGradient)
                                .padding(3.dp)
                                .background(Color(0xFF0F172A), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Rounded.Person, 
                                contentDescription = null, 
                                tint = Color.White.copy(alpha = 0.9f), 
                                modifier = Modifier.size(60.dp)
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(20.dp))
                        
                        Text(
                            currentUser?.name ?: profileState.name, 
                            color = Color.White, 
                            fontSize = 24.sp, 
                            fontWeight = FontWeight.Bold
                        )
                        
                        Spacer(modifier = Modifier.height(4.dp))
                        
                        Surface(
                            color = Color(0xFFD4AF37).copy(alpha = 0.15f),
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(0.5.dp, Color(0xFFD4AF37).copy(alpha = 0.4f))
                        ) {
                            Text(
                                "ELITE ${currentUser?.role?.name?.uppercase() ?: profileState.subtitle.uppercase()}", 
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                color = Color(0xFFD4AF37), 
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 1.sp
                            )
                        }
                    }
                }
            }

            Text(
                stringResource(R.string.account_settings).uppercase(),
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
                style = MaterialTheme.typography.labelSmall.copy(
                    letterSpacing = 1.2.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFFD4AF37)
                )
            )

            SettingsItem(
                icon = Icons.Rounded.Person,
                title = stringResource(R.string.my_profile),
                onClick = { onNavigate(Screen.Profile.route) }
            )

            if (authenticator.isBiometricAvailable()) {
                SettingsItem(
                    icon = Icons.Rounded.Fingerprint,
                    title = "Security & Biometrics",
                    subtitle = if (isBiometricEnabled) "Authentication active" else "Touch ID disabled",
                    onClick = { authViewModel.setBiometricEnabled(!isBiometricEnabled) }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                stringResource(R.string.app_preferences).uppercase(),
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
                style = MaterialTheme.typography.labelSmall.copy(
                    letterSpacing = 1.2.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFFD4AF37)
                )
            )

            SettingsItem(
                icon = Icons.Rounded.DarkMode,
                title = "Aesthetic Theme",
                subtitle = when(themeMode) {
                    "light" -> "Platinum Light"
                    "dark" -> "Elite Charcoal"
                    else -> "System Default"
                },
                onClick = { showThemeDialog = true }
            )

            SettingsItem(
                icon = Icons.Rounded.Language,
                title = stringResource(R.string.language),
                subtitle = if (languageCode == "en") "English (Premium)" else "Hindi (Standard)",
                onClick = { showLanguageDialog = true }
            )

            Spacer(modifier = Modifier.height(40.dp))
            
            Button(
                onClick = { authViewModel.logout() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Red.copy(alpha = 0.05f), 
                    contentColor = Color.Red.copy(alpha = 0.8f)
                ),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.Red.copy(alpha = 0.1f))
            ) {
                Icon(Icons.Rounded.Logout, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    stringResource(R.string.logout), 
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }
            
            Spacer(modifier = Modifier.height(48.dp))
        }
    }

    if (showThemeDialog) {
        AlertDialog(
            onDismissRequest = { showThemeDialog = false },
            containerColor = Color(0xFF1E293B),
            titleContentColor = Color.White,
            textContentColor = Color.White.copy(alpha = 0.7f),
            title = { Text(stringResource(R.string.select_theme), fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    ThemeOption(stringResource(R.string.system_default), "system", themeMode) { mainViewModel.setThemeMode("system"); showThemeDialog = false }
                    ThemeOption("Platinum Light", "light", themeMode) { mainViewModel.setThemeMode("light"); showThemeDialog = false }
                    ThemeOption("Elite Charcoal", "dark", themeMode) { mainViewModel.setThemeMode("dark"); showThemeDialog = false }
                }
            },
            confirmButton = {
                TextButton(onClick = { showThemeDialog = false }) { 
                    Text(stringResource(R.string.cancel), color = Color(0xFFD4AF37)) 
                }
            }
        )
    }

    if (showLanguageDialog) {
        AlertDialog(
            onDismissRequest = { showLanguageDialog = false },
            containerColor = Color(0xFF1E293B),
            titleContentColor = Color.White,
            textContentColor = Color.White.copy(alpha = 0.7f),
            title = { Text(stringResource(R.string.select_language), fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    LanguageOption(stringResource(R.string.english), "en", languageCode) { mainViewModel.setLanguageCode("en"); showLanguageDialog = false }
                    LanguageOption(stringResource(R.string.hindi), "hi", languageCode) { mainViewModel.setLanguageCode("hi"); showLanguageDialog = false }
                }
            },
            confirmButton = {
                TextButton(onClick = { showLanguageDialog = false }) { 
                    Text(stringResource(R.string.cancel), color = Color(0xFFD4AF37)) 
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
            .glassmorphic(
                backgroundColor = Color.White.copy(alpha = 0.03f),
                borderColor = Color.White.copy(alpha = 0.08f)
            )
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(44.dp),
                shape = RoundedCornerShape(12.dp),
                color = Color.White.copy(alpha = 0.05f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, contentDescription = null, tint = Color.White.copy(alpha = 0.7f), modifier = Modifier.size(22.dp))
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
                        color = Color.White.copy(alpha = 0.4f)
                    )
                }
            }
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.2f)
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
                selectedColor = Color(0xFFD4AF37),
                unselectedColor = Color.White.copy(alpha = 0.3f)
            )
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text, 
            color = Color.White,
            fontWeight = if(mode == currentMode) FontWeight.Bold else FontWeight.Normal
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
                selectedColor = Color(0xFFD4AF37),
                unselectedColor = Color.White.copy(alpha = 0.3f)
            )
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text, 
            color = Color.White,
            fontWeight = if(code == currentCode) FontWeight.Bold else FontWeight.Normal
        )
    }
}
