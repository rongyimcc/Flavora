# Flavora — Food Sharing App

Flavora is an Android-based food sharing application that allows users to upload, browse, and interact with food posts in real time.

## Features
- User authentication using Firebase
- Upload and share food post with images
- Real-time search and data updates
- Post rating and interaction system

## Tech Stack
- Java
- Android SDK
- Firebase (Authentication, Firestore)



## Architecture

This project follows a layered architecture:

- Data layer: Firebase integration and DAO abstraction
- Repository layer: handles business logic
- UI layer: Android activities and fragments

Real-time data synchronization is achieved through Firebase Firestore.

## Directory Layout
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

## My Contribution

- Designed and implemented Firebase-based backend logic
- Developed user authentication and data interaction features
- Applied layered architecture to separate data, business logic, and UI

## How to Run
1. Open the `./android` folder in Android Studio  
2. Sync Gradle  
3. Run the app on emulator or device

## Note
This project was developed as part of a team during a university hackathon.


