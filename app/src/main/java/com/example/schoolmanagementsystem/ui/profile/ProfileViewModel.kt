package com.example.schoolmanagementsystem.ui.profile

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(ProfileState())
    val state = _state.asStateFlow()

    data class ProfileState(
        val name: String = "Kunal Mishra",
        val subtitle: String = "Student : Class VII",
        val admissionNumber: String = "28_21",
        val className: String = "Class VII",
        val batch: String = "A 2025-2026",
        val admissionDate: String = "16 March 2021",
        val guardianName: String = "Rajan Kumar Mishra",
        val gender: String = "Male",
        val dob: String = "10 January 2012",
        val bloodGroup: String = "O+",
        val birthPlace: String = "Jharsuguda",
        val nationality: String = "India",
        val religion: String = "Hindu",
        val language: String = "Odia",
        val aadharNumber: String = "965923733300",
        val pen: String = "21468989953",
        val apaarId: String = "729710203215",
        val modeOfTransport: String = "Local",
        val houseName: String = "Yellow",
        val height: String = "154",
        val weight: String = "53",
        val addressHome: String = "Dutta Building, Shanti Nagar",
        val addressCity: String = "Jharsuguda",
        val addressState: String = "Odisha",
        val addressPin: String = "768202",
        val phone: String = "7852918709",
        val phone2: String = "9337960897",
        val guardianMobile: String = "9776742093",
        val email: String = "Mishra123@gmail.com"
    )
}
