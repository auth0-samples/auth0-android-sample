# Tutorial

_This content has been extracted from a previous version of the [Auth0 Android Quickstart](https://auth0.com/docs/quickstart/native/android) and demonstrates how to integrate Auth0 with an Android application using the [Auth0 Android](https://github.com/auth0/Auth0.Android) SDK._

## Configure Auth0

### Get Your Application Keys

When you signed up for Auth0, a new application was created for you, or you could have created a new one. You will need some details about that application to communicate with Auth0. You can get these details from the Application Settings section in the Auth0 dashboard.

> When using the Default App with a Native or Single Page Application, ensure to update the **Token Endpoint Authentication Method** to `None` and set the **Application Type** to either `SPA` or `Native`. 

You need the following information:

- Domain
- Client ID

We suggest you do not hardcode these values as you may need to change them in the future. Instead, use [String Resources](https://developer.android.com/guide/topics/resources/string-resource.html), such as `@string/com_auth0_domain`, to define the values. 

Edit your `res/values/strings.xml` file as follows:

```xml
<resources>
    <string name="com_auth0_client_id">{YOUR_CLIENT_ID}</string>
    <string name="com_auth0_domain">{YOUR_AUTH0_DOMAIN}</string>
</resources>
```

### Configure Callback URLs

A callback URL is a URL in your application where Auth0 redirects the user after they have authenticated. The callback URL for your app must be added to the **Allowed Callback URLs** field in your Application Settings. If this field is not set, users will be unable to log in to the application and will get an error.

### Configure Logout URLs

A logout URL is a URL in your application that Auth0 can return to after the user has been logged out of the authorization server. This is specified in the `returnTo` query parameter. The logout URL for your app must be added to the **Allowed Logout URLs** field in your Application Settings. If this field is not set, users will be unable to log out from the application and will get an error.

Replace `YOUR_APP_PACKAGE_NAME` with your application's package name, available as the `applicationId` attribute in the `app/build.gradle` file.

## Add the Auth0 Android Dependency

Add the [Auth0 Android](https://github.com/auth0/Auth0.Android) SDK into your project. The library will make requests to the Auth0's Authentication and Management APIs.

### Add Auth0 to Gradle

In your app's `build.gradle` dependencies section, add the following:

```groovy
apply plugin: 'com.android.application'
android {
  // ...
}
dependencies {
  // Add the Auth0 Android SDK
  implementation 'com.auth0.android:auth0:1.+'
}
```

> If Android Studio lints the `+` sign, or if you want to use a fixed version, check for the latest in [Maven](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22auth0%22%20g%3A%22com.auth0.android%22) or [JCenter](https://bintray.com/auth0/android/auth0). Remember to synchronize using the Android Studio prompt or run `./gradlew clean build` from the command line. For more information about Gradle usage, check [their official documentation](http://tools.android.com/tech-docs/new-build-system/user-guide).

Add manifest placeholders required by the SDK. The placeholders are used internally to define an `intent-filter` that captures the authentication callback URL.

To add the manifest placeholders, add the next line:

```
// app/build.gradle

apply plugin: 'com.android.application'
compileSdkVersion 28
android {
    defaultConfig {
        applicationId "com.auth0.samples"
        minSdkVersion 15
        targetSdkVersion 28
        // ...

        // ---> Add the next line
        manifestPlaceholders = [auth0Domain: "@string/com_auth0_domain", auth0Scheme: "demo"]
        // <---
    }
}
```

> You do not need to declare a specific `intent-filter` for your activity, because you have defined the manifest placeholders with your Auth0 **Domain** and **Scheme** values and the library will handle the redirection for you.

The `AndroidManifest.xml` file should look like this:

```xml
<!-- app/src/main/AndroidManifest.xml -->

<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="com.auth0.samples">

    <uses-permission android:name="android.permission.INTERNET" />

    <application
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:theme="@style/AppTheme">

        <activity android:name="com.auth0.samples.MainActivity">
          <intent-filter>
              <action android:name="android.intent.action.MAIN" />
              <category android:name="android.intent.category.LAUNCHER" />
          </intent-filter>
        </activity>

    </application>

</manifest>
```

Run **Sync Project with Gradle Files** inside Android Studio or execute `./gradlew clean assembleDebug` from the command line.

> For more information about using Gradle, check the [Gradle official documentation](https://gradle.org/getting-started-android-build/).

## Add Authentication with Auth0

[Universal Login](https://auth0.com/docs/universal-login) is the easiest way to set up authentication in your application. We recommend using it for the best experience, best security and the fullest array of features.

In the `onCreate` method, create a new instance of the `Auth0` class to hold user credentials and set it to be OIDC conformant.

You can use a constructor that receives an Android Context if you have added the following String resources:
- `R.string.com_auth0_client_id`
- `R.string.com_auth0_domain`

```java
// app/src/main/java/com/auth0/samples/MainActivity.java

private Auth0 auth0;

@Override
protected void onCreate(@Nullable Bundle savedInstanceState) {
    // ...
    auth0 = new Auth0(this);
    auth0.setOIDCConformant(true);
}
```

Otherwise, use the constructor that receives both strings.

Finally, create a `login` method and use the `WebAuthProvider` class to authenticate with any connection you enabled on your application in the Auth0 dashboard.

```java
// app/src/main/java/com/auth0/samples/LoginActivity.java

private void login() {
    WebAuthProvider.login(auth0)
            .withScheme("demo")
            .withAudience(String.format("https://%s/userinfo", getString(R.string.com_auth0_domain)))
            .start(MainActivity.this, new AuthCallback() {
                @Override
                public void onFailure(@NonNull Dialog dialog) {
                    // Show error Dialog to user
                }

                @Override
                public void onFailure(AuthenticationException exception) {
                    // Show error to user
                }

                @Override
                public void onSuccess(@NonNull Credentials credentials) {
                    // Store credentials
                    // Navigate to your main activity
                }
        });
}
```

After you call the `WebAuthProvider#start` function, the browser launches and shows the Authentication page. Once the user authenticates, the callback URL is called. The callback URL contains the final result of the authentication process.

## Logout

Use `WebAuthProvider` to remove the cookie set by the Browser at authentication time, so that the users are forced to re-enter their credentials the next time they try to authenticate.

Check in the `LoginActivity` if a boolean extra is present in the Intent at the Activity launch. This scenario triggered by the `MainActivity` dictates that the user wants to log out.

```java
// app/src/main/java/com/auth0/samples/MainActivity.java

private void logout() {
    Intent intent = new Intent(this, LoginActivity.class);
    intent.putExtra(LoginActivity.EXTRA_CLEAR_CREDENTIALS, true);

    startActivity(intent);
    finish();
}

// app/src/main/java/com/auth0/samples/LoginActivity.java

@Override
protected void onCreate(Bundle savedInstanceState) {
    // ...
    if (getIntent().getBooleanExtra(EXTRA_CLEAR_CREDENTIALS, false)) {
        logout();
    }
}

private void logout() {
    WebAuthProvider.logout(auth0)
            .withScheme("demo")
            .start(this, new VoidCallback() {
                @Override
                public void onSuccess(Void payload) {
                }

                @Override
                public void onFailure(Auth0Exception error) {
                    // Show error to user
                }
            });
}
```

The logout is achieved by using the `WebAuthProvider` class. This call will open the Browser and navigate the user to the logout endpoint. If the log out is cancelled, you might want to take the user back to where they were before attempting to log out.
