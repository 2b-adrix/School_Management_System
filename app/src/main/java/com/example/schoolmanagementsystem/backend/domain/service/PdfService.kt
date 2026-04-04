package com.example.schoolmanagementsystem.backend.domain.service

import android.content.Context
import com.example.schoolmanagementsystem.backend.domain.model.FeePayment
import com.example.schoolmanagementsystem.backend.domain.model.Result
import com.example.schoolmanagementsystem.backend.domain.model.Student

interface PdfService {
    suspend fun generateResultPdf(
        context: Context,
        student: Student,
        results: List<Result>
    ): String? // Returns the file path of the generated PDF

    suspend fun generateFeeReceiptPdf(
        context: Context,
        student: Student,
        payment: FeePayment
    ): String?
}

