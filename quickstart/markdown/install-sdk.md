Add the Auth0 Android SDK to `app/build.gradle.kts` and sync Gradle.

The `applicationId` is part of the callback URL (`%AUTH0_SCHEME%://%AUTH0_DOMAIN%/android/%APPLICATION_ID%/callback`), which Auth0 uses to match the authentication response to your app.
