# SIKSHA: The Elite School Management System 🎓

SIKSHA is a high-performance, visually stunning, and feature-rich school management system built with modern Android technologies. Designed with an "Elite" aesthetic, it provides a seamless experience for Students, Teachers, and Administrators.

---

## ✨ Key Highlights

*   **Elite UI/UX**: Crafted with Jetpack Compose and Material 3, featuring dark mode optimization, smooth animations, and a premium "Elite" design language.
*   **AI-Powered Insights**: Integrated with Google Gemini (Siksha Brain) to provide academic performance analysis and automated insights.
*   **Multi-Role Architecture**: Tailored dashboards and functionalities for Students, Teachers, and Admins.
*   **Real-time Management**: Live attendance tracking, session management, and instant notifications.
*   **Secure & Robust**: Built on a solid foundation of Clean Architecture and Hilt for dependency injection.

---

## 🛠️ Technical Stack

*   **UI Framework**: Jetpack Compose (Material 3)
*   **Language**: 100% Kotlin
*   **Architecture**: MVVM + Clean Architecture
*   **Dependency Injection**: Hilt (Dagger)
*   **Database & Auth**: Supabase (PostgreSQL, Auth, Storage)
*   **Local Storage**: Room Persistence & DataStore
*   **AI Integration**: Google Generative AI (Gemini)
*   **Animations**: Lottie & Compose Animations
*   **Networking**: Retrofit & Ktor
*   **Image Loading**: Coil

---

## 📱 Features

### 👨‍🎓 Student Dashboard
*   **Attendance Tracking**: Circular progress indicators for real-time attendance monitoring.
*   **Live Sessions**: View current/upcoming classes with "LIVE" status indicators.
*   **Siksha AI Insight**: Personal academic assistant for performance tips.
*   **Academic Ledger**: Track all system activities (assignments, grades, etc.).
*   **Fee Management**: View dues and simulate secure payments via "Siksha Pay".

### 👩‍🏫 Teacher Portal
*   **Smart Attendance**: Effortless attendance marking for assigned classes.
*   **Exam Management**: Create and manage exams and grading.
*   **Resource Center**: Access subjects and announcements.
*   **Faculty Insights**: Quick stats on student counts and active classes.

### 🛡️ Admin Central Control
*   **Institutional Overview**: Real-time stats for the entire school (Total Students/Teachers/Classes).
*   **Inventory Management**: Track school assets and supplies.
*   **Fee Control**: Oversee financial records and fee structures.
*   **User Management**: Full CRUD operations for students and faculty.

---

## 📂 Project Structure

```
app/src/main/java/com/example/schoolmanagementsystem/
├── backend/            # Data & Domain Layers
│   ├── data/           # Repository implementations, Remote/Local sources
│   ├── domain/         # Models, Repository interfaces, Use cases
│   └── di/             # Hilt dependency injection modules
├── frontend/           # UI Layer
│   ├── ui/             # Compose Screens, ViewModels, Components
│   ├── navigation/     # App routing logic
│   └── theme/          # SIKSHA Elite design system (Colors, Typography)
└── SchoolApplication.kt # Application entry point
```

---

## 🚀 Getting Started

1.  **Clone the Repo**: `git clone https://github.com/your-username/siksha-app.git`
2.  **Configuration**: Add your `SUPABASE_URL` and `SUPABASE_KEY` to the `SupabaseModule`.
3.  **AI Setup**: Configure your Gemini API key in `GenerativeAIService`.
4.  **Build**: Open in Android Studio (Ladybug or newer) and sync Gradle.
5.  **Run**: Deploy to an emulator or device (API 26+ recommended).

---

## 🔮 Future Roadmap

- [ ] **Siksha Pay Integration**: Live payment gateway for real transactions.
- [ ] **Advanced Analytics**: Detailed graph-based performance reports.
- [ ] **Offline Mode**: Local caching for areas with poor connectivity.
- [ ] **Parental Portal**: Dedicated access for parents to track child progress.

---

## 📝 License

Distributed under the MIT License. See `LICENSE` for more information.

---

**SIKSHA** — *Empowering Education through Technology.* 🚀
