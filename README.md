## Firebase sync setup

The project includes a FirebaseSyncManager that can upload local birds to Firestore and download/merge birds back into the local Room DB. To enable Firebase sync:

1. Create a Firebase project at https://console.firebase.google.com
2. Add an Android app to the Firebase project with package name `com.aviary.management`.
3. Download the generated `google-services.json` and place it in `app/`.
4. Add the Google Services Gradle plugin to the root build configuration and apply it in `app/build.gradle.kts`:

   - In root `build.gradle.kts` (project-level):
     buildscript {
       repositories { google(); mavenCentral() }
       dependencies {
         classpath("com.google.gms:google-services:4.4.0")
       }
     }

   - In `app/build.gradle.kts`, apply the plugin at the top:
     plugins {
       id("com.android.application")
       id("org.jetbrains.kotlin.android")
       id("org.jetbrains.kotlin.kapt")
       id("com.google.gms.google-services")
     }

5. Re-sync Gradle. You should be able to call `FirebaseSyncManager.initialize(context)` from `MainActivity` (or an Application class) to initialize the Firestore client.

Notes
- The current sync implementation is intentionally simple: it uploads all birds to a `birds` collection using document IDs of the form `bird_<localId>`, and `downloadAndMergeBirds` inserts documents as local Bird records. It does not perform advanced conflict resolution or per-field merges — those can be added later.

## Release build & signing

For a production release build you must create and keep a keystore locally. Do NOT commit your keystore to source control.

Steps to create a release keystore and produce a signed APK/AAB:

1. In Android Studio: Build -> Generate Signed Bundle / APK
2. Create a new Key store path, enter passwords and alias, and keep the keystore safely backed up.
3. Use the keystore information to sign your release.

You can also configure Gradle signing in `app/build.gradle.kts` with a `signingConfigs` entry referencing a local ` keystore.properties` file (do not commit it). Example (in Groovy):

// store the keystore in a secure place and reference it locally

## Exporting and importing CSV

- Export: use `CsvExporter.exportBirdsCsv(context, rows)` to write a CSV file to `cacheDir/exports`.
- Import: open a CSV file via the system file picker and call `CsvImporter.importBirdsFromCsv(context, uri)` (suspend function).

