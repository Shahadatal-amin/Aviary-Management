# Aviary Management

A minimal Android app for managing aviary birds, built in Kotlin with Jetpack Compose. The app includes a modern bird entry form with a genetics dropdown inspired by public lovebird genetics references and a warm, breeder-friendly UI palette.

## What is included
- Birds form with species selector
- Genetics / mutation dropdown menu
- Aviary and notes fields
- Clean Material 3 design with a green and gold aesthetic inspired by public lovebird breeding resources
- Data model ready for expansion into Room, Firebase, or custom API sync

## Data note
This starter app uses a curated local genetics catalog derived from public lovebird genetics references. It is intended for app scaffolding and UI demonstration. If you have a verified genetics dataset or site permission, replace `app/src/main/java/com/aviary/management/GeneticsCatalog.kt` with your licensed/exported data.

## Tech stack
- Kotlin
- Jetpack Compose
- Material 3
- Android Studio

## Running the app
1. Open the repository in Android Studio.
2. Let Gradle sync.
3. Run the `app` configuration on an emulator or physical device.

## Suggested next steps
- Add Room database for offline bird records
- Add Firebase or REST sync
- Add bird photos and gallery support
- Add health logs and feeding schedules
- Add export/import of aviary records
