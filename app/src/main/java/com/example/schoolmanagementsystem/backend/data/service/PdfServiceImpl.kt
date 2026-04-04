package com.example.schoolmanagementsystem.backend.data.service

import android.content.Context
import com.example.schoolmanagementsystem.backend.domain.model.FeePayment
import com.example.schoolmanagementsystem.backend.domain.model.Result
import com.example.schoolmanagementsystem.backend.domain.model.Student
import com.example.schoolmanagementsystem.backend.domain.service.PdfService
import com.lowagie.text.Document
import com.lowagie.text.Element
import com.lowagie.text.Font
import com.lowagie.text.Paragraph
import com.lowagie.text.pdf.PdfPTable
import com.lowagie.text.pdf.PdfWriter
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

class PdfServiceImpl @Inject constructor() : PdfService {

    override suspend fun generateResultPdf(
        context: Context,
        student: Student,
        results: List<Result>
    ): String? {
        val fileName = "result_${student.id}.pdf"
        val file = File(context.cacheDir, fileName)

        return try {
            val document = Document()
            PdfWriter.getInstance(document, FileOutputStream(file))
            document.open()

            // Header
            val headerFont = Font(Font.HELVETICA, 18f, Font.BOLD)
            val header = Paragraph("School Management System - Result Card", headerFont)
            header.alignment = Element.ALIGN_CENTER
            document.add(header)
            document.add(Paragraph(" "))

            // Student Info
            document.add(Paragraph("Student Name: ${student.firstName} ${student.lastName}"))
            document.add(Paragraph("Roll Number: ${student.rollNumber}"))
            document.add(Paragraph("Class: ${student.className}"))
            document.add(Paragraph(" "))

            // Results Table
            val table = PdfPTable(3)
            table.widthPercentage = 100f
            table.addCell("Subject ID")
            table.addCell("Marks Obtained")
            table.addCell("Grade")

            results.forEach { result ->
                table.addCell(result.subjectId)
                table.addCell(result.marksObtained.toString())
                table.addCell(result.grade)
            }

            document.add(table)
            document.close()
            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override suspend fun generateFeeReceiptPdf(
        context: Context,
        student: Student,
        payment: FeePayment
    ): String? {
        val fileName = "receipt_${payment.id}.pdf"
        val file = File(context.cacheDir, fileName)

        return try {
            val document = Document()
            PdfWriter.getInstance(document, FileOutputStream(file))
            document.open()

            // Header
            val headerFont = Font(Font.HELVETICA, 18f, Font.BOLD)
            val header = Paragraph("School Management System - Fee Receipt", headerFont)
            header.alignment = Element.ALIGN_CENTER
            document.add(header)
            document.add(Paragraph(" "))

            document.add(Paragraph("Student: ${student.firstName} ${student.lastName}"))
            document.add(Paragraph("Payment Date: ${payment.paymentDate}"))
            document.add(Paragraph("Amount Paid: ${payment.amountPaid}"))
            document.add(Paragraph("Payment Method: ${payment.paymentMethod}"))
            document.add(Paragraph("Status: ${payment.status}"))

            document.close()
            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}

