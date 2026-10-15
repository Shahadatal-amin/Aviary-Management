# Aviary Management

A minimal Android app for managing aviary birds, built in Kotlin with Jetpack Compose. The app includes a modern bird entry form with a genetics dropdown populated from a curated local catalog to avoid reuse of any third-party copyrighted material.

## What is included
- Birds form with species selector
- Genetics / mutation dropdown (curated local list)
- Aviary and notes fields
- Clean Material 3 design with a neutral, non-branded UI palette
- Data model ready for expansion into Room, Firebase, or custom API sync

## Copyright & Data use
This project intentionally avoids importing or reproducing copyrighted content from third-party websites. The genetics catalog in this repository is a locally curated set of common species and mutation names intended for developer use and UI demonstration only.

If you have a licensed dataset (CSV/JSON) or explicit permission to use content from a third-party site, replace `app/src/main/java/com/aviary/management/GeneticsCatalog.kt` with your own file and make sure to include appropriate attribution.

## Running the app
1. Open the repository in Android Studio.
2. Let Gradle sync.
3. Run the `app` configuration on an emulator or physical device.

## Next steps I will implement
- Room-backed persistence for Birds, Aviaries, HealthEvents, FeedingEvents and Inventory
- Photo picker and image storage for bird profiles
- Health logs and feeding schedules with WorkManager reminders
- CSV export/import for backups
- CI workflow to build debug APK (already configured)

If you'd like me to proceed now I will finish these features and publish a debug APK artifact you can download from the Actions tab.
