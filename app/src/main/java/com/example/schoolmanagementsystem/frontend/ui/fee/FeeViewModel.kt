package com.example.schoolmanagementsystem.frontend.ui.fee

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.schoolmanagementsystem.backend.domain.model.FeePayment
import com.example.schoolmanagementsystem.backend.domain.model.FeeStructure
import com.example.schoolmanagementsystem.backend.domain.repository.AuthRepository
import com.example.schoolmanagementsystem.backend.domain.repository.FeeRepository
import com.example.schoolmanagementsystem.backend.domain.repository.StudentRepository
import com.example.schoolmanagementsystem.backend.domain.service.PdfService
import com.example.schoolmanagementsystem.backend.domain.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class FeeViewModel @Inject constructor(
    private val feeRepository: FeeRepository,
    private val authRepository: AuthRepository,
    private val studentRepository: StudentRepository,
    private val pdfService: PdfService
) : ViewModel() {

    private val _state = MutableStateFlow(FeeState())
    val state = _state.asStateFlow()

    private val _saveState = MutableStateFlow<Resource<Unit>>(Resource.Success(Unit))
    val saveState = _saveState.asStateFlow()

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            val user = authRepository.getCurrentUser().firstOrNull() ?: return@launch
            
            // 1. Get Fee Structures and calculate pending
            combine(
                feeRepository.getFeeStructures(),
                feeRepository.getPaymentsByStudent(user.id),
                studentRepository.getStudentById(user.id).asFlow()
            ) { structuresRes, paymentsRes, studentRes ->
                if (structuresRes is Resource.Success && paymentsRes is Resource.Success && studentRes is Resource.Success) {
                    val student = studentRes.data
                    val allStructures = structuresRes.data ?: emptyList()
                    val payments = paymentsRes.data ?: emptyList()
                    
                    // Filter structures for the student's class
                    val studentStructures = allStructures.filter { it.classId == student?.classId }
                    
                    // Calculate pending fees
                    val pendingItems = mutableListOf<FeeItem>()
                    var totalDueAmount = 0.0
                    
                    studentStructures.forEach { structure ->
                        val amountPaidForThis = payments.filter { it.feeStructureId == structure.id }.sumOf { it.amountPaid }
                        val remaining = structure.amount - amountPaidForThis
                        
                        if (remaining > 0) {
                            pendingItems.add(
                                FeeItem(
                                    id = structure.id,
                                    title = structure.feeName,
                                    dueDate = structure.dueDate ?: "No Date",
                                    amount = "₹ $remaining",
                                    isPaid = false
                                )
                            )
                            totalDueAmount += remaining
                        }
                    }
                    
                    val paidItems = payments.map { 
                        FeeItem(it.id, "Payment: ${allStructures.find { s -> s.id == it.feeStructureId }?.feeName ?: "Fee"}", it.paymentDate, "₹ ${it.amountPaid}", true) 
                    }
                    
                    _state.update { it.copy(
                        dueFees = pendingItems,
                        paidFees = paidItems,
                        totalDue = "₹ $totalDueAmount",
                        feeStructures = structuresRes
                    ) }
                } else {
                    _state.update { it.copy(feeStructures = structuresRes) }
                }
            }.launchIn(viewModelScope)
        }
    }

    private fun <T> Resource<T>.asFlow(): Flow<Resource<T>> = flow { emit(this@asFlow) }

    fun getFeeStructures() {
        feeRepository.getFeeStructures().onEach { result ->
            _state.value = _state.value.copy(feeStructures = result)
        }.launchIn(viewModelScope)
    }

    fun addFeeStructure(
        classId: String,
        feeName: String,
        amount: String,
        dueDate: String,
        description: String
    ) {
        val amountDouble = amount.toDoubleOrNull()
        if (amountDouble == null) {
            viewModelScope.launch {
                _eventFlow.emit(UiEvent.ShowSnackbar("Invalid amount"))
            }
            return
        }

        viewModelScope.launch {
            _saveState.value = Resource.Loading()
            val user = authRepository.getCurrentUser().firstOrNull()
            if (user == null) {
                _saveState.value = Resource.Error("User not found")
                return@launch
            }

            val feeStructure = FeeStructure(
                id = UUID.randomUUID().toString(),
                schoolId = user.schoolId,
                classId = classId,
                feeName = feeName,
                amount = amountDouble,
                dueDate = dueDate,
                description = description
            )

            val result = feeRepository.addFeeStructure(feeStructure)
            _saveState.value = result
            
            if (result is Resource.Success) {
                _eventFlow.emit(UiEvent.SaveSuccess)
            } else if (result is Resource.Error) {
                _eventFlow.emit(UiEvent.ShowSnackbar(result.message ?: "Unknown error"))
            }
        }
    }

    data class FeeState(
        val selectedTab: Int = 0,
        val totalDue: String = "₹ 0.00",
        val dueFees: List<FeeItem> = emptyList(),
        val paidFees: List<FeeItem> = emptyList(),
        val feeStructures: Resource<List<FeeStructure>> = Resource.Loading()
    )

    data class FeeItem(
        val id: String,
        val title: String,
        val dueDate: String,
        val amount: String,
        val isPaid: Boolean = false
    )

    fun onTabSelected(index: Int) {
        _state.update { it.copy(selectedTab = index) }
    }

    fun downloadReceipt(context: Context, feeItemId: String) {
        viewModelScope.launch {
            val user = authRepository.getCurrentUser().firstOrNull() ?: return@launch
            val studentResult = studentRepository.getStudentById(user.id)
            
            if (studentResult is Resource.Success) {
                val student = studentResult.data ?: return@launch
                // In a real scenario, we'd fetch the full FeePayment object from the DB using feeItemId
                val dummyPayment = FeePayment(
                    id = feeItemId,
                    schoolId = student.schoolId,
                    studentId = student.id,
                    feeStructureId = "",
                    amountPaid = 8725.0,
                    paymentDate = "2025-04-15",
                    paymentMethod = "Online",
                    status = "PAID"
                )
                
                _eventFlow.emit(UiEvent.ShowSnackbar("Generating Receipt..."))
                val path = pdfService.generateFeeReceiptPdf(context, student, dummyPayment)
                if (path != null) {
                    openPdf(context, path)
                } else {
                    _eventFlow.emit(UiEvent.ShowSnackbar("Failed to generate receipt"))
                }
            }
        }
    }

    private fun openPdf(context: Context, path: String) {
        val file = File(path)
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/pdf")
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        }
        context.startActivity(Intent.createChooser(intent, "Open Fee Receipt"))
    }

    fun deleteFeeStructure(feeStructure: FeeStructure) {
        viewModelScope.launch {
            val result = feeRepository.deleteFeeStructure(feeStructure)
            if (result is Resource.Success) {
                getFeeStructures()
            } else if (result is Resource.Error) {
                _eventFlow.emit(UiEvent.ShowSnackbar(result.message ?: "Error deleting fee structure"))
            }
        }
    }

    fun payFee(feeStructureId: String, amount: Double) {
        viewModelScope.launch {
            val user = authRepository.getCurrentUser().firstOrNull() ?: return@launch
            _saveState.value = Resource.Loading()
            
            val payment = FeePayment(
                id = UUID.randomUUID().toString(),
                schoolId = user.schoolId,
                studentId = user.id,
                feeStructureId = feeStructureId,
                amountPaid = amount,
                paymentDate = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date()),
                paymentMethod = "Online",
                status = "PAID"
            )
            
            val result = feeRepository.addPayment(payment)
            _saveState.value = result
            
            if (result is Resource.Success) {
                _eventFlow.emit(UiEvent.ShowSnackbar("Payment successful!"))
            } else {
                _eventFlow.emit(UiEvent.ShowSnackbar("Payment failed: ${result.message}"))
            }
        }
    }

    sealed class UiEvent {
        object SaveSuccess : UiEvent()
        data class ShowSnackbar(val message: String) : UiEvent()
    }
}

