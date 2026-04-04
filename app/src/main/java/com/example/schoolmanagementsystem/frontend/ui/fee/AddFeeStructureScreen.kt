package com.example.schoolmanagementsystem.frontend.ui.fee

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.schoolmanagementsystem.backend.domain.model.SchoolClass
import com.example.schoolmanagementsystem.backend.domain.util.Resource
import com.example.schoolmanagementsystem.frontend.ui.components.AppTextField
import com.example.schoolmanagementsystem.frontend.ui.components.PrimaryButton
import com.example.schoolmanagementsystem.frontend.ui.components.SchoolTopAppBar
import com.example.schoolmanagementsystem.frontend.ui.components.SectionTitle
import com.example.schoolmanagementsystem.frontend.ui.schoolclass.ClassListViewModel
import com.example.schoolmanagementsystem.frontend.ui.theme.EliteGoldGradient
import com.example.schoolmanagementsystem.frontend.ui.theme.glassmorphic
import com.example.schoolmanagementsystem.frontend.ui.theme.spacing
import kotlinx.coroutines.flow.collectLatest
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddFeeStructureScreen(
    onNavigateBack: () -> Unit,
    viewModel: FeeViewModel = hiltViewModel(),
    classListViewModel: ClassListViewModel = hiltViewModel()
) {
    val focusManager = LocalFocusManager.current
    
    var selectedClass by remember { mutableStateOf<SchoolClass?>(null) }
    var feeName by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var dueDate by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    
    var isClassDropdownExpanded by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }

    val classState by classListViewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val saveState by viewModel.saveState.collectAsState()

    // Validation
    val amountError = remember(amount) { 
        if (amount.isNotEmpty() && amount.toDoubleOrNull() == null) "Invalid amount" else null 
    }

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is FeeViewModel.UiEvent.SaveSuccess -> onNavigateBack()
                is FeeViewModel.UiEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(event.message)
                }
            }
        }
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        dueDate = sdf.format(Date(it))
                    }
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            SchoolTopAppBar(
                title = "Add Fee Structure",
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
            verticalArrangement = Arrangement.spacedBy(spacing().spacing4)
        ) {
            
            // Elite Header Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .glassmorphic(),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(modifier = Modifier.padding(spacing().spacing4)) {
                    Text(
                        text = "Fee Configuration",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            brush = EliteGoldGradient,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = "Define elite billing structures for premium accounts",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(spacing().spacing2))
            
            // Class Dropdown
            ExposedDropdownMenuBox(
                expanded = isClassDropdownExpanded,
                onExpandedChange = { isClassDropdownExpanded = !isClassDropdownExpanded }
            ) {
                OutlinedTextField(
                    value = selectedClass?.name ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Select Class *") },
                    leadingIcon = { Icon(Icons.Rounded.Class, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isClassDropdownExpanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    shape = RoundedCornerShape(12.dp)
                )

                ExposedDropdownMenu(
                    expanded = isClassDropdownExpanded,
                    onDismissRequest = { isClassDropdownExpanded = false }
                ) {
                    when (val result = classState) {
                        is Resource.Success -> {
                            result.data?.forEach { schoolClass ->
                                DropdownMenuItem(
                                    text = { Text(schoolClass.name) },
                                    onClick = {
                                        selectedClass = schoolClass
                                        isClassDropdownExpanded = false
                                    }
                                )
                            }
                        }
                        else -> { /* Loading/Error */ }
                    }
                }
            }
            
            AppTextField(
                value = feeName,
                onValueChange = { feeName = it },
                label = "Fee Name *",
                leadingIcon = Icons.Rounded.Title,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
            )
            
            AppTextField(
                value = amount,
                onValueChange = { amount = it },
                label = "Amount *",
                leadingIcon = Icons.Rounded.Payments,
                isError = amountError != null,
                errorMessage = amountError,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
            )
            
            // Date Picker Field
            Box {
                AppTextField(
                    value = dueDate,
                    onValueChange = { dueDate = it },
                    label = "Due Date (YYYY-MM-DD) *",
                    leadingIcon = Icons.Rounded.CalendarToday,
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
                )
                // Overlaid transparent box to trigger date picker
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clickable { showDatePicker = true }
                )
            }
            
            AppTextField(
                value = description,
                onValueChange = { description = it },
                label = "Description",
                leadingIcon = Icons.Rounded.Description,
                singleLine = false,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() })
            )

            Spacer(modifier = Modifier.height(spacing().spacing6))

            PrimaryButton(
                text = "Save Fee Structure",
                onClick = { 
                    selectedClass?.let { 
                        viewModel.addFeeStructure(it.id, feeName, amount, dueDate, description) 
                    }
                },
                isLoading = saveState is Resource.Loading,
                enabled = selectedClass != null && feeName.isNotBlank() && amount.isNotBlank() && dueDate.isNotBlank() && amountError == null
            )
        }
    }
}

