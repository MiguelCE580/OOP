# EduReach

**A Community Digital Literacy and Skill Assessment System**
Desktop application (JavaFX GUI) · single-file JSON persistence (Phase 2-3) · MySQL in Phase 4
Supports UN SDG 4: Quality Education (Target 4.4)

CS CS 002 – Advanced Object-Oriented Programming · First Semester S.Y. 2026-2027
Technological Institute of the Philippines – Quezon City

Proponents:
| Name | Role |
|---|---|
| Erese, Daniel Noel B. | Project Manager / System Analyst, UI/UX Designer & JavaFX Developer |
| Lantin, Gabriel Jose | Lead Programmer (Core OOP & Business Logic), QA & Technical Writer |
| Escobar, Miguel C. | Database Designer / DBA (MySQL schema & DAO layer) |

---

## Current build: Phase 2-3 (~70% complete)

See `docs/STATUS.md` for the full done / not-done checklist.

### Run it

Requirements: **JDK 17+** and **Maven 3.8+**. No database needed yet.

```bash
mvn javafx:run                 # normal launch
# or, plain classpath:
mvn compile dependency:build-classpath -Dmdep.outputFile=cp.txt
java -cp target/classes:$(cat cp.txt) edureach.Launcher
```

On first launch the app seeds its demo dataset into **one file**: `data/edureach.json`
(records + UI preferences). Delete the file to reset the demo.

### Demo accounts

| Role | Username | Password |
|---|---|---|
| Student | `maria.santos` | `learn123` |
| Student (failing → remedial) | `mark.villanueva` | `learn123` |
| Student (extended-time accommodation) | `mark.villanueva` | `learn123` |
| Instructor | `ms.reyes` | `teach123` |
| Administrator | `admin` | `admin123` |

### What works today

- **Login & role selection** with lockout after 3 failed attempts, privacy notice, RA 10173 note
- **Dark mode (default) + light mode**, high-contrast mode, 4 font sizes, EN/FIL labels - persisted in the data file
- **Student dashboard**: greeting, stats, course tiles, skill-domain mastery, badges, remedial banner
- **Course view**: lessons with media chips + mark-complete, quizzes with latest score, remedial plan panel
- **Quiz engine**: countdown timer, randomized item order, question navigator, extended time (+50%) for PWD learners, auto-submit, auto-scoring via the `Gradable` interface
- **Instant results & feedback**: score, pass/fail vs passing mark, time used, per-answer correct/wrong with the right answer shown
- **Curriculum module (new)**: optional K-12 path, Elementary / Junior High / Senior High, Mathematics flagship + Science & English topics, follow-a-track and mark-as-studied
- **Interventions module (new)**: auto-detects learners below 70%, builds remedial plans (weak skill domains + review lessons + simplified remedial quiz), tracks OPEN → IN_PROGRESS → COMPLETED; students see the plan in their course view
- **Quiz analytics (new)**: per-question attempt/correct/rate stats, "Hard item" flag below 50%, auto improvement suggestions incl. the most-picked wrong option
- **Course manager**: course picker, lessons table, edit title/category, clone course, new course, enroll learners
- **Attendance**: P/A/L marks per session date, sessions-attended counter, one-click save
- **Gradebook**: per-quiz + practical + total with status chips, learner search, **CSV export** (PDF export stubbed for Phase 4)
- **Certificates**: in-app preview with verification code, code verifier for staff/employers (PDF download stubbed)
- **Administration**: user list, audit log, backup/restore stubs
- **Audit logging** of logins and privileged actions

### Intentionally left for the remaining 30% (Phase 4)

- MySQL 8.0 schema + HikariCP pool + `MySql*DAO` implementations (the DAO interfaces and the
  `DatabaseManager` placeholder are already in place; swapping does not touch the UI)
- BCrypt salted hashing (currently SHA-256, isolated in `TimeUtil.hashPassword`)
- PDF certificate + PDF gradebook export (OpenPDF)
- Announcements, Item Banks, People screens
- Full FXML markup migration (screens currently build widgets in controllers - see TODO in each `.fxml`)
- Complete Filipino resource bundle, JUnit 5 suite, UML/ERD documentation set

### Project layout

```
src/main/java/edureach
├── Main.java, Launcher.java, ScreenshotHarness.java (dev-only UI snapshotter)
├── model/      abstract User hierarchy, Gradable, quizzes, curriculum, remedial plans, DB aggregate
├── data/       JsonStore (single file), DAO interfaces, file DAOs, SeedData, DatabaseManager (Phase 4 stub)
├── service/    Auth, Quiz, Progress, Analytics, Remedial, Curriculum, Certificate services
└── ui/         ThemeManager, I18N, Ui widget factory, controllers/ + resources/edureach/ui (FXML, CSS)
```

`shots/` contains PNG snapshots of every screen in both themes
(regenerate: `xvfb-run -a java -cp target/classes:$(cat cp.txt) edureach.HarnessLauncher`).
