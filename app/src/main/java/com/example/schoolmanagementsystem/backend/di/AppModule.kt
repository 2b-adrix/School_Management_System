package com.example.schoolmanagementsystem.backend.di

import com.example.schoolmanagementsystem.backend.data.repository.*
import com.example.schoolmanagementsystem.backend.data.service.AttendanceAIServiceImpl
import com.example.schoolmanagementsystem.backend.data.service.GenerativeAIServiceImpl
import com.example.schoolmanagementsystem.backend.data.service.PdfServiceImpl
import com.example.schoolmanagementsystem.backend.domain.repository.*
import com.example.schoolmanagementsystem.backend.domain.service.AttendanceAIService
import com.example.schoolmanagementsystem.backend.domain.service.GenerativeAIService
import com.example.schoolmanagementsystem.backend.domain.service.PdfService
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindStudentRepository(
        studentRepositoryImpl: StudentRepositoryImpl
    ): StudentRepository

    @Binds
    @Singleton
    abstract fun bindTeacherRepository(
        teacherRepositoryImpl: TeacherRepositoryImpl
    ): TeacherRepository

    @Binds
    @Singleton
    abstract fun bindClassRepository(
        classRepositoryImpl: ClassRepositoryImpl
    ): ClassRepository

    @Binds
    @Singleton
    abstract fun bindSubjectRepository(
        subjectRepositoryImpl: SubjectRepositoryImpl
    ): SubjectRepository

    @Binds
    @Singleton
    abstract fun bindAttendanceRepository(
        attendanceRepositoryImpl: AttendanceRepositoryImpl
    ): AttendanceRepository

    @Binds
    @Singleton
    abstract fun bindExamRepository(
        examRepositoryImpl: ExamRepositoryImpl
    ): ExamRepository

    @Binds
    @Singleton
    abstract fun bindFeeRepository(
        feeRepositoryImpl: FeeRepositoryImpl
    ): FeeRepository

    @Binds
    @Singleton
    abstract fun bindTimetableRepository(
        timetableRepositoryImpl: TimetableRepositoryImpl
    ): TimetableRepository

    @Binds
    @Singleton
    abstract fun bindStorageRepository(
        storageRepositoryImpl: StorageRepositoryImpl
    ): StorageRepository

    @Binds
    @Singleton
    abstract fun bindAssignmentRepository(
        assignmentRepositoryImpl: AssignmentRepositoryImpl
    ): AssignmentRepository

    @Binds
    @Singleton
    abstract fun bindAnnouncementRepository(
        announcementRepositoryImpl: AnnouncementRepositoryImpl
    ): AnnouncementRepository

    @Binds
    @Singleton
    abstract fun bindSalaryRepository(
        salaryRepositoryImpl: SalaryRepositoryImpl
    ): SalaryRepository

    @Binds
    @Singleton
    abstract fun bindInventoryRepository(
        inventoryRepositoryImpl: InventoryRepositoryImpl
    ): InventoryRepository

    @Binds
    @Singleton
    abstract fun bindMessageRepository(
        messageRepositoryImpl: MessageRepositoryImpl
    ): MessageRepository

    @Binds
    @Singleton
    abstract fun bindPdfService(
        pdfServiceImpl: PdfServiceImpl
    ): PdfService

    @Binds
    @Singleton
    abstract fun bindAttendanceAIService(
        attendanceAIServiceImpl: AttendanceAIServiceImpl
    ): AttendanceAIService

    @Binds
    @Singleton
    abstract fun bindGenerativeAIService(
        generativeAIServiceImpl: GenerativeAIServiceImpl
    ): GenerativeAIService
}

