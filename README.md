# My Personal Assistant — Android starter

A Kotlin + Jetpack Compose starter project for the requested all-in-one personal assistant.

## Starter screens and functions
- Home dashboard with bottom navigation
- Notes, reminders/tasks, daily routine and Money Manager screens
- Add note, task and expense forms
- Android speech-recognition intent for voice-to-text
- Indian Rupee amounts

## Not production-ready yet
This starter currently keeps sample records in app memory; they reset when the app process is cleared. The next development stages are:
1. Room database for persistent offline records
2. Date/time reminder scheduling and notification channels
3. Birthday records, yearly recurrence and text-to-speech announcements
4. Expense categories, multiple accounts, budgets, recurring payments and charts
5. Export to CSV/PDF
6. Secure cloud backup and sync (requires a Firebase project and sign-in)
7. Permission handling, accessibility and real-device testing

## Build in Android Studio
1. Install Android Studio and Android SDK 35.
2. Open this folder in Android Studio and wait for Gradle sync.
3. Connect an Android phone with USB debugging enabled, or start an emulator.
4. Select Build > Build Bundle(s) / APK(s) > Build APK(s).
5. To distribute a release APK, use Build > Generate Signed Bundle / APK and configure your own signing key.

Package: `com.example.mypersonalassistant`
Minimum Android: 8.0 (API 26)

Voice recognition uses the Android speech-recognition service. Supported languages and offline recognition depend on the device and installed speech services. This starter does not listen continuously in the background.

## Build APK online without Android Studio (GitHub Actions)
1. Create or sign in to a free GitHub account at https://github.com.
2. Create a new repository named `MyPersonalAssistant` (Public or Private).
3. On the repository page choose **Add file > Upload files**.
4. Extract this ZIP first, then upload all files and folders inside it, including the hidden `.github` folder. Commit the upload to the `main` branch.
5. Open the repository's **Actions** tab. The workflow named **Build Android APK** should start automatically after the upload. If it does not, open that workflow and choose **Run workflow**.
6. When the run finishes with a green check mark, open the run, scroll to **Artifacts**, and download `MyPersonalAssistant-debug-apk`.
7. Extract the downloaded artifact ZIP. The file inside is `app-debug.apk`. Open it on your Android phone and allow installation from that source if Android asks.

This is a debug APK for personal testing, not a Play Store release. Keep the GitHub repository private if you do not want the source code public. The app itself is still a starter: data is currently held in memory and advanced reminder, birthday, persistent storage, and cloud-sync features are not implemented yet.
