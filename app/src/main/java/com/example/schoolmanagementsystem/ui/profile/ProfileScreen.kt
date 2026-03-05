package com.example.schoolmanagementsystem.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavigateBack: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val primaryColor = MaterialTheme.colorScheme.primary
    val backgroundColor = Color(0xFFF5F5F5)

    Scaffold(
        containerColor = backgroundColor,
        topBar = {
            TopAppBar(
                title = { Text("Profile", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = primaryColor)
            )
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
                    .height(180.dp)
                    .background(primaryColor),
                contentAlignment = Alignment.BottomCenter
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Rounded.Person, contentDescription = null, tint = Color.White, modifier = Modifier.size(60.dp))
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(state.name, color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    Text(state.subtitle, color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
                }
            }

            // Details Sections
            ProfileSection("General Details") {
                DetailItem("Admission Number", state.admissionNumber)
                DetailItem("Class", state.className)
                DetailItem("Batch", state.batch)
                DetailItem("Admission date", state.admissionDate)
                DetailItem("Guardian name", state.guardianName)
            }

            ProfileSection("Personal Details") {
                DetailItem("Gender", state.gender)
                DetailItem("Date of birth", state.dob)
                DetailItem("Blood Group", state.bloodGroup)
                DetailItem("Birth place", state.birthPlace)
                DetailItem("Nationality", state.nationality)
                DetailItem("Religion", state.religion)
                DetailItem("Language", state.language)
            }

            ProfileSection("Additional details") {
                DetailItem("Aadhar Number", state.aadharNumber)
                DetailItem("PEN", state.pen)
                DetailItem("APAAR ID", state.apaarId)
                DetailItem("Mode Of Transport", state.modeOfTransport)
                DetailItem("House Name", state.houseName)
                DetailItem("Height", state.height)
                DetailItem("Weight", state.weight)
            }

            ProfileSection("Address") {
                DetailItem("Home", state.addressHome)
                DetailItem("City", state.addressCity)
                DetailItem("State", state.addressState)
                DetailItem("Pin", state.addressPin)
            }

            ProfileSection("Contact") {
                DetailItem("Phone", state.phone, showCopy = true)
                DetailItem("Phone 2", state.phone2, showCopy = true)
                DetailItem("Guardian mobile", state.guardianMobile, showCopy = true)
                DetailItem("Email", state.email, showCopy = true)
            }

            // Help Section
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Help", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = primaryColor)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "In case, If you have any concerns about your profile details or if you want to update any of your information, please contact the administrator of your institution",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun ProfileSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.primary
        )
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(0.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(content = content)
        }
    }
}

@Composable
fun DetailItem(label: String, value: String, showCopy: Boolean = false) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, style = MaterialTheme.typography.bodyMedium, color = Color.Gray, modifier = Modifier.weight(1f))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    value, 
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium), 
                    color = if (showCopy) MaterialTheme.colorScheme.primary else Color.Black
                )
                if (showCopy) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        Icons.Rounded.ContentCopy, 
                        contentDescription = "Copy", 
                        modifier = Modifier.size(16.dp),
                        tint = Color.Gray
                    )
                }
            }
        }
        HorizontalDivider(modifier = Modifier.fillMaxWidth(), thickness = 0.5.dp, color = Color.LightGray.copy(alpha = 0.3f))
    }
}
