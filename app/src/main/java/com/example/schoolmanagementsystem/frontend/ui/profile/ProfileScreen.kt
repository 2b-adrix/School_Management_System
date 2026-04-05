package com.example.schoolmanagementsystem.frontend.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun ProfileScreen(
    onNavigateBack: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Column(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.background)
                    .statusBarsPadding()
                    .padding(bottom = 8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                    
                    Spacer(modifier = Modifier.weight(1f))
                    
                    if (state.isEditing) {
                        TextButton(onClick = { viewModel.saveProfile() }) {
                            Text("SAVE", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                        }
                    } else {
                        IconButton(onClick = { viewModel.toggleEdit() }) {
                            Icon(
                                Icons.Rounded.Edit, 
                                contentDescription = "Edit Profile", 
                                tint = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }
                }
                
                Text(
                    text = if (state.isEditing) "Edit Profile" else "Elite Profile",
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold),
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
                Text(
                    text = "Manage your institutional identity",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 4.dp)
                )
            }
        }
    ) { padding ->
        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Profile Image Section
                ProfileImageSection()

                Spacer(modifier = Modifier.height(24.dp))

                if (state.isEditing) {
                    EditFields(state, viewModel)
                } else {
                    DisplayProfile(state)
                }
                
                Spacer(modifier = Modifier.height(48.dp))
            }
        }
    }
}

@Composable
fun ProfileImageSection() {
    Box(
        modifier = Modifier
            .size(140.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
            .padding(4.dp)
            .background(MaterialTheme.colorScheme.background, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Rounded.Person,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun DisplayProfile(state: ProfileViewModel.ProfileState) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = state.name,
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onBackground
            )
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Surface(
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Rounded.Badge, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("ID: ${state.id}", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        InfoCard("ACADEMIC DETAILS") {
            ProfileInfoRow(Icons.Rounded.School, "Class", state.className)
            ProfileInfoRow(Icons.Rounded.Numbers, "Admission No", state.admissionNumber)
            ProfileInfoRow(Icons.Rounded.CalendarMonth, "Joined Date", state.joinedDate)
        }

        Spacer(modifier = Modifier.height(16.dp))

        InfoCard("PERSONAL CREDENTIALS") {
            ProfileInfoRow(Icons.Rounded.Email, "Official Email", state.email)
            ProfileInfoRow(Icons.Rounded.Phone, "Primary Phone", state.phone)
            ProfileInfoRow(Icons.Rounded.LocationOn, "Address", state.addressHome)
            ProfileInfoRow(Icons.Rounded.Cake, "Date of Birth", state.dob)
        }

        Spacer(modifier = Modifier.height(16.dp))

        InfoCard("GUARDIAN INFO") {
            ProfileInfoRow(Icons.Rounded.SupervisorAccount, "Guardian Name", state.guardianName)
            ProfileInfoRow(Icons.Rounded.ContactPhone, "Guardian Contact", state.guardianMobile)
        }
    }
}

@Composable
fun EditFields(state: ProfileViewModel.ProfileState, viewModel: ProfileViewModel) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        OutlinedTextField(
            value = state.firstName,
            onValueChange = { viewModel.onNameChange(it, state.lastName) },
            label = { Text("First Name") },
            modifier = Modifier.fillMaxWidth(),
            colors = editTextFieldColors(),
            shape = RoundedCornerShape(16.dp)
        )
        OutlinedTextField(
            value = state.lastName,
            onValueChange = { viewModel.onNameChange(state.firstName, it) },
            label = { Text("Last Name") },
            modifier = Modifier.fillMaxWidth(),
            colors = editTextFieldColors(),
            shape = RoundedCornerShape(16.dp)
        )
        OutlinedTextField(
            value = state.phone,
            onValueChange = { viewModel.onPhoneChange(it) },
            label = { Text("Phone") },
            modifier = Modifier.fillMaxWidth(),
            colors = editTextFieldColors(),
            shape = RoundedCornerShape(16.dp)
        )
        OutlinedTextField(
            value = state.guardianName,
            onValueChange = { viewModel.onGuardianNameChange(it) },
            label = { Text("Guardian Name") },
            modifier = Modifier.fillMaxWidth(),
            colors = editTextFieldColors(),
            shape = RoundedCornerShape(16.dp)
        )
        OutlinedTextField(
            value = state.addressHome,
            onValueChange = { viewModel.onAddressChange(it) },
            label = { Text("Address") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3,
            colors = editTextFieldColors(),
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Composable
fun InfoCard(title: String, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text(
                title,
                style = MaterialTheme.typography.labelSmall.copy(
                    letterSpacing = 1.2.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary
                )
            )
            Spacer(modifier = Modifier.height(12.dp))
            content()
        }
    }
}

@Composable
fun editTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = MaterialTheme.colorScheme.onSurface,
    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
    focusedBorderColor = MaterialTheme.colorScheme.primary,
    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
    focusedLabelColor = MaterialTheme.colorScheme.primary,
    unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
    cursorColor = MaterialTheme.colorScheme.primary
)

@Composable
fun ProfileInfoRow(icon: ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(40.dp),
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    icon, 
                    contentDescription = null, 
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(
                label, 
                style = MaterialTheme.typography.labelSmall, 
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Bold
            )
            Text(
                value, 
                style = MaterialTheme.typography.bodyLarge, 
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
