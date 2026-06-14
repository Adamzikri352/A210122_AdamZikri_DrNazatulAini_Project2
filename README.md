# Smart Tenaga — Project 2

**Student:** Adam Zikri (`a210122`)  
**Supervisor:** Dr. Nazatul Aini  
**Course:** Mobile Application Development  
**App ID:** `com.example.a210122_nazatul_project2`

Smart Tenaga is an Android app that helps Malaysian households track appliance electricity use, estimate TNB monthly bills, set spending goals, and stay informed with live energy news. It extends Project 1 with four Project 2 pillars: **Room**, **Firebase Firestore**, **REST API**, and a **hardware sensor (camera)**.

---

## SDG Focus

Aligned with **SDG 7: Affordable and Clean Energy**, Smart Tenaga addresses a common household problem: electricity bills feel unpredictable because usage is hard to track appliance by appliance. The app makes energy use visible through logging, cost estimation, goal tracking, and educational content from live energy data sources.

---

## Features

| Feature | Description |
|---------|-------------|
| **Appliance logging** | Add appliances with name, wattage, and hours per day |
| **TNB bill estimate** | Calculates proportional monthly cost using Malaysian TNB-style tiers |
| **Bill goal** | Set a monthly RM target and track progress on Profile |
| **Camera capture** | Photograph appliances while adding them (CameraX sensor) |
| **Live energy news** | Fetches articles from the EIA *Today in Energy* RSS feed |
| **Local persistence** | Saves appliances and goals offline with Room |
| **Cloud sync** | Automatically backs up and restores user data via Firebase Firestore |
| **7-screen flow** | Login plus six main destinations after sign-in |

---

## Screens

| # | Screen | Route | Purpose |
|---|--------|-------|---------|
| 1 | **Login** | `login` | Username-based entry; triggers cloud restore |
| 2 | **MyHome** | `myhome` | Dashboard, bill summary, Quick Access, Discover More |
| 3 | **Bills** | `bills` | TNB estimate and per-appliance cost breakdown |
| 4 | **Add** | `add_appliance` | Log appliance details and optional photo |
| 5 | **History** | `appliance_history` | View saved appliances and captured images |
| 6 | **What's New** | `whats_new` | Live EIA energy articles; tap to open in browser |
| 7 | **Profile** | `profile` | Bill goal, progress indicator, logout |

**Sub-route:** `camera` — CameraX capture screen opened from Add Appliance.

**Bottom navigation (6 tabs):** MyHome · Bills · Add · History · What's New · Profile

---

## Project 2 Technical Pillars

### 1. Room (Local Database)

- Database: `smarttenaga.db` (version 2)
- Entities: `ApplianceEntity`, `BillGoalEntity`
- DAOs: `ApplianceDao`, `BillGoalDao`
- Repository: `ApplianceRepository`

Data persists locally after the app is closed or restarted.

### 2. Firebase Firestore (Cloud Sync)

- Collection structure: `users/{username}/appliances` and `users/{username}` for bill goal
- Automatic sync on login, add appliance, delete appliance, and save goal
- Implementation: `FirestoreRepository.kt`

### 3. REST API (Live Internet Data)

- Source: [EIA Today in Energy RSS](https://www.eia.gov/rss/todayinenergy.xml)
- Fetched with **OkHttp** and parsed as XML
- Implementation: `EiaTodayInEnergyService.kt`
- UI: `What'sNew.kt` — articles open in the device browser on tap

### 4. Hardware Sensor (Camera)

- **CameraX** preview and image capture
- Runtime camera permission via Accompanist Permissions
- Photo URI stored with each appliance record
- Implementation: `CameraScreen.kt`, integrated in `Addappliancescreen.kt`

---

## Tech Stack

- **Language:** Kotlin
- **UI:** Jetpack Compose + Material 3
- **Architecture:** ViewModel + Repository
- **Navigation:** Navigation Compose
- **Local DB:** Room + KSP
- **Cloud:** Firebase Firestore
- **Networking:** OkHttp
- **Camera:** CameraX
- **Images:** Coil
- **Async:** Kotlin Coroutines + StateFlow

**Requirements:** Android SDK 24+ · compileSdk 35 · JDK 17

---

## Project Structure

```
app/src/main/java/com/example/a210122_nazatul_lab1/
├── MainActivity.kt              # Navigation, MyHome, bottom nav
├── Login.kt                     # Login screen
├── Bills.kt                     # TNB bill breakdown
├── Addappliancescreen.kt        # Add appliance form
├── Appliancehistoryscreen.kt    # Appliance history list
├── What'sNew.kt                 # EIA articles screen
├── Profile.kt                   # Bill goal and logout
├── CameraScreen.kt              # CameraX sensor
├── AppViewModel.kt              # App state, TNB calc, API, sync
├── AppData.kt                   # Room entities and UI models
├── AppDao.kt                    # Room DAOs
├── AppRepository.kt             # Local repository
├── AppDatabase.kt               # Room database
├── FirestoreRepository.kt       # Firebase sync
└── EiaTodayInEnergyService.kt   # EIA RSS API client
```

---

## Getting Started

### Prerequisites

- Android Studio (latest stable recommended)
- JDK 17
- Android device or emulator with internet access
- Firebase project with Firestore enabled

### 1. Clone the repository

```bash
git clone <your-repo-url>
cd a210122_DrNazatul_Project2
```

### 2. Firebase setup

1. Create a Firebase project at [Firebase Console](https://console.firebase.google.com/).
2. Add an Android app with package name: `com.example.a210122_nazatul_project2`
3. Download `google-services.json`
4. Place it in: `app/google-services.json`

> If you fork this repo, replace `google-services.json` with your own Firebase config. Do not commit private production keys to a public repository unless intended.

### 3. Build and run

**Android Studio:** Open the project → Run on device/emulator

**Command line:**

```bash
./gradlew assembleDebug
```

On Windows (PowerShell):

```powershell
.\gradlew assembleDebug
```

Debug APK output:

```
app/build/outputs/apk/debug/app-debug.apk
```

### 4. Permissions

The app requests:

- `INTERNET` — EIA RSS feed and Firebase sync
- `CAMERA` — appliance photo capture (optional hardware)

---

## Usage Flow

1. **Login** with any username (e.g. your matric number).
2. On **MyHome**, review estimated bill and open Quick Access shortcuts.
3. **Add** an appliance — optionally capture a photo with the camera.
4. Check **Bills** for TNB-style cost breakdown.
5. View saved items in **History**.
6. Open **What's New** for live energy articles; tap an article to read it in the browser.
7. Set a monthly goal on **Profile** and track whether your estimate is within target.
8. Close and reopen the app — Room keeps local data; Firebase restores cloud backup on login.

---

## TNB Bill Calculation

The app uses a simplified Malaysian household electricity model in `AppViewModel.kt`:

- Base rate applied to monthly kWh
- Additional charge above 600 kWh
- Tiered rebates up to 1000 kWh

Appliance monthly kWh is estimated as:

```
(wattage × hoursPerDay × 30) / 1000
```

---

## Firebase Data Model

```
users/
  └── {username}/
        ├── billGoalRm: Double
        ├── updatedAt: Long
        └── appliances/
              └── {applianceId}/
                    ├── localId, name, wattage
                    ├── hoursPerDay, monthlyKwh
                    ├── photoUri, updatedAt
```

---

## API Reference

| Item | Value |
|------|-------|
| Feed URL | `https://www.eia.gov/rss/todayinenergy.xml` |
| Format | RSS/XML |
| Client | OkHttp + XmlPullParser |
| Article fields | title, description, link, pubDate |

---

## Version

- **versionName:** 2.0
- **versionCode:** 2

---

## License

Academic project for educational purposes.

---

## Acknowledgements

- [U.S. Energy Information Administration (EIA)](https://www.eia.gov/) — *Today in Energy* RSS feed
- [Tenaga Nasional Berhad (TNB)](https://www.tnb.com.my/) — Malaysian electricity tariff context
- Android Jetpack, Firebase, and CameraX documentation
