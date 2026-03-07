# EduSync SaaS: Production-Grade School Management System

EduSync is a comprehensive, multi-tenant SaaS platform designed to streamline school operations. Built with modern technologies and a focus on scalability, it provides a centralized system for administrators, teachers, and students to manage academic and administrative tasks.

---

## 🚀 Commercial Value Proposition
**Market Ready & Scalable**: Engineered for a SaaS business model, allowing a single deployment to serve thousands of schools with complete data isolation.
*   **Multi-Tenancy**: Built-in `school_id` scoping at the repository layer.
*   **Enterprise Security**: Role-Based Access Control (RBAC) and JWT-based authentication via Supabase.
*   **Modern UX**: Clean, intuitive Material 3 interface for Android.

---

## 🛠️ Tech Stack

### Mobile (Android)
*   **Language**: Kotlin
*   **UI Framework**: Jetpack Compose (Material 3)
*   **Architecture**: MVVM + Clean Architecture
*   **Dependency Injection**: Hilt
*   **Networking**: Retrofit + Ktor (for Supabase)
*   **Backend-as-a-Service**: Supabase (Postgrest, Auth, Storage)
*   **Local Storage**: DataStore (Preferences & Session Management)

### Backend & Database
*   **Infrastructure**: Supabase (PostgreSQL)
*   **Storage**: Supabase Storage (for profiles and documents)
*   **Security**: Row Level Security (RLS) & Multi-tenant data scoping.

---

## 📖 System Features

### 1. Multi-Tenant Core
*   **Isolated Environments**: Every school has its own logical data space.
*   **Unified Auth**: Secure login with role redirection.
*   **Session Management**: Persistent school context across the app.

### 2. Administrator Module
*   **Stats Dashboard**: Real-time counts of students, teachers, and classes.
*   **Staff Management**: Add, update, and assign teachers.
*   **Academic Setup**: Configure classes, sections, and subjects.
*   **Fee Management**: Define fee structures and track collections.

### 3. Teacher Module
*   **Smart Attendance**: Quick, subject-wise attendance marking.
*   **Exam & Grading**: Create exams and enter student marks.
*   **Classroom Management**: View assigned class lists and timetables.
*   **Announcements**: Post notices to specific classes or the entire school.

### 4. Student/Parent Module
*   **Personal Dashboard**: View daily timetable, fee dues, and recent results.
*   **Performance Tracking**: Access report cards and exam schedules.
*   **Stay Informed**: Receive real-time announcements and school news.
*   **Profile Management**: Update personal details and view academic history.

---

## 📂 Project Structure (Clean Architecture)

```
app/src/main/java/com/example/schoolmanagementsystem/
├── data/
│   ├── manager/       # Session & DataStore management
│   ├── remote/        # Supabase API clients
│   └── repository/    # Multi-tenant scoped implementations
├── di/                # Hilt Modules (Supabase, Repositories)
├── domain/
│   ├── model/         # Multi-tenant data entities
│   ├── repository/    # Domain interfaces
│   └── util/          # Resource wrappers (Success/Error/Loading)
├── ui/
│   ├── theme/         # Material 3 Theme (Light/Dark/System)
│   ├── components/    # Reusable UI widgets
│   ├── navigation/    # Screen routes and NavHost
│   └── features/      # Feature modules (Dashboard, Attendance, etc.)
└── MainActivity.kt    # Root entry point with global state
```

---

## 🔐 Security & Multi-Tenancy Implementation

EduSync uses a "Shared Schema" approach with strict data filtering:
1.  **Repository Filtering**: Every data request automatically injects the logged-in user's `school_id`.
2.  **Context Injection**: New records (Students, Exams, etc.) automatically inherit the `school_id` from the secure session context.
3.  **Role Guarding**: UI components and navigation routes are protected based on the user's role (Super Admin, School Admin, Teacher, Student).

---

## 🛠️ Getting Started

### Prerequisites
*   Android Studio Ladybug+
*   JDK 17+
*   Supabase Project (URL & Service Role Key)

### Installation
1.  Clone the repository.
2.  Update `SupabaseModule.kt` with your Supabase credentials.
3.  Sync Gradle and build the project.
4.  Deploy to an Android Emulator or physical device.

---

## 📈 Roadmap
- [ ] Push Notifications for announcements.
- [ ] Offline data synchronization.
- [ ] AI-driven student performance analytics.
- [ ] Integration with payment gateways for fee collection.
```
