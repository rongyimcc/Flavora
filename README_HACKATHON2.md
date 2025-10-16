# Flavora — Hackathon 2

> An Android + Firebase social app for food sharing. **Open and run only the `./android` folder** in Android Studio (ignore other subfolders under `hackathon`).

---

## Table of Contents
- [Quick Start](#quick-start)
- [Test Environment](#test-environment)
- [Test Accounts](#test-accounts)
- [Architecture](#architecture)
- [Critical Dependency Notes](#critical-dependency-notes)
- [Code & UI Standards](#code--ui-standards)
- [Notes](#notes)

---

## Quick Start

1. **Requirements**
   - **Android Gradle Plugin (AGP): 8.13.0 — required**
   - Android Studio (Ladybug/Koala or newer recommended)
   - JDK 17+
   - A configured Firebase project (`google-services.json` placed at `./android/app/`)

2. **Open & Run**
   - In Android Studio, **open only** the `./android` directory.
   - Sync Gradle, attach a device or create an AVD.
   - Run the app (for first-time verification, start with sign-in/sign-up).

3. **Directory Layout (core)**
   ```
   android/
     ├─ app/src/main/java/.../
     │   ├─ model/            # Data models
     │   ├─ util/             # Utilities
     │   ├─ datasource/       # Firebase data source
     │   ├─ dao/              # DAO interfaces & implementations
     │   ├─ repository/       # Repositories
     │   ├─ facade/           # Business facades
     │   └─ ui/               # UI & adapters
     ├─ app/src/main/res/     # Layouts / icons / themes / navigation
     └─ build.gradle.kts      # Build & plugin config
   ```

---

## Test Environment

- **Host**: macOS (Apple Silicon, ARM CPU)
- **Android Virtual Device**: *Medium Phone 36.1* (system image 36.1)
- **Android Gradle Plugin (AGP)**: **8.13.0 (mandatory)**

> If your local AGP differs, align project `build.gradle.kts`/`settings.gradle.kts` to **8.13.0**, and update the Gradle Wrapper/plugins accordingly.

---

## Test Accounts

- Emails: `test1@test.com` … `test5@test.com`  
- Password: `123456`

> For functional verification only. Do not reuse in production.

---

## Architecture

Layered design: **Model → Util → DataSource + DAO Interfaces → DAO Implementations → Repository → Facade → UI**

**Approximate scale:**
- Java classes: 31
- Layout XMLs: 12
- Icon resources: ~30 (Material Icons)
- Config: Firebase, Gradle, runtime permissions, etc.

---

## Critical Dependency Notes

- **Foundations first**
  - `dao/DAO.java`: base interface for all DAOs  
  - `datasource/FirebaseDataSource.java`: unified data source used by DAOs  
  - **Model layer**: shared models used across all layers

- **Business loop**
  - `AuthRepository`: required by authentication screens  
  - `PostFacade`: required by creation & discovery flows  
  - `PostInteractionFacade`: required by like/favorite flows

- **UI framework before features**
  - `MainActivity`: fragment container & navigation entry  
  - `PostsAdapter`: shared adapter used in multiple screens  
  - Authentication screens: other features expect a signed-in user

---

## Code & UI Standards

1. **Naming**
   - Classes: PascalCase
   - Methods/variables: camelCase
   - Constants: UPPER_SNAKE_CASE

2. **Comments**
   - Provide JavaDoc for classes/methods  
   - Include `@param`, `@return`, and author tags where useful

3. **Architecture**
   - No cross-layer shortcuts  
   - Singletons use **Double-Checked Locking (DCL)**  
   - Fragments: handle lifecycle & null-safety diligently

4. **UI**
   - Use **ViewBinding** throughout
   - Clear references in `onDestroyView()`
   - Support dark mode (themes/resources)

---

## Notes

- **Open and run only `./android`**; ignore other folders under `hackathon`.
- `google-services.json` is in `.gitignore`.
