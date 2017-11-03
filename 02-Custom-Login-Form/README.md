# Custom Login

This tutorial will show you how to integrate Lock v2 in your Android project in order to present an Embedded Login screen.

Tested using:
- Android Studio 2.3'
- Android SDK 25'
- Emulator - Nexus 5X - Android 6.0'


## Before Starting

You'll first need to whitelist the **Callback URL** in the "Allowed Callback URLs" section of the [Client settings](https://manage.auth0.com/#/clients) by adding the URL below. Remember to replace `YOUR_APP_PACKAGE_NAME` with your actual application's package name, available in the `app/build.gradle` file as the `applicationId` attribute:

```text
demo://{YOUR_DOMAIN}/android/YOUR_APP_PACKAGE_NAME/callback
```

## Add the Auth0.Android Dependency

Add the [Auth0 Android](https://github.com/auth0/Auth0.Android) SDK into your project. The library makes requests to the Auth0's Authentication and Management APIs.

In your app's `build.gradle` dependencies section, add the following:

```xml
apply plugin: 'com.android.application'
android {
  //..
}
dependencies {
  //---> Add the next line
  compile 'com.auth0.android:auth0:1.+'
  //<---
}
```

> You can check for the latest version on the [repository Readme](https://github.com/auth0/auth0.android#installation), in [Maven](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22auth0%22%20g%3A%22com.auth0.android%22), or in [JCenter](https://bintray.com/auth0/android/auth0).

Add manifest placeholders required by the SDK. The placeholders are used internally to define an `intent-filter` that captures the authentication callback URL.

To add the manifest placeholders, add the next line:

```xml
apply plugin: 'com.android.application'
android {
    compileSdkVersion 25
    buildToolsVersion "25.0.3"
    defaultConfig {
        applicationId "com.auth0.samples"
        minSdkVersion 15
        targetSdkVersion 25
        //...

        //---> Add the next line
        manifestPlaceholders = [auth0Domain: "@string/com_auth0_domain", auth0Scheme: "demo"]
        //<---
    }
}
```

Run **Sync Project with Gradle Files** inside Android Studio or execute `./gradlew clean assembleDebug` from the command line.

> For more information about using Gradle, check the [Gradle official documentation](https://gradle.org/getting-started-android-build/).


### Configure Your Manifest File

You need to add the INTERNET permission inside the `AndroidManifest.xml`:

```xml
<uses-permission android:name="android.permission.INTERNET" />
```

## Implement The Login

At this point, you're all set to implement the login in any activity you want.


### Using the Hosted Login Page

You'll use the `WebAuthProvider` class. This class allows you to easily setup a call to the /authorize endpoint and use the [hosted Lock](https://auth0.com/docs/hosted-pages/login) to prompt the user to log in using any of the client's configured connections. The builder includes methods to customize this call, i.e. if you know in advance which connection to use. In the code we show how by calling `whichConnection` we request a login with Twitter. Make sure to use a connection that is enabled in your client!

```java
private void login() {
    Auth0 auth0 = new Auth0(this);
    auth0.setOIDCConformant(true);
    WebAuthProvider.init(auth0)
                  .withScheme("demo")
                  .withAudience(String.format("https://%s/userinfo", getString(R.string.com_auth0_domain)))
                  .withConnection("twitter")
                  .start(this, new AuthCallback() {
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

In the snippet above, we also request a "demo" scheme for the callback URL we've setup at the beginning, and the userinfo audience to obtain a rich user's profile. There are many options to customize the authentication. Make sure to check them [here](https://auth0.com/docs/libraries/auth0-android#implementing-web-based-auth).


### Using a Database connection

**IMPORTANT NOTICE**

Username/Email & Password authentication from native clients is disabled by default for new tenants as of 8 June 2017. Users are encouraged to use the [Hosted Login Page](https://auth0.com/docs/hosted-pages/login) and perform Web Authentication instead. If you still want to proceed you'll need to enable the Password Grant Type on your dashboard first. See [Client Grant Types](https://auth0.com/docs/clients/client-grant-types) for more information.

First, you'll need to instantiate the Authentication API:

```java
Auth0 auth0 = new Auth0(this);
auth0.setOIDCConformant(true);
AuthenticationAPIClient client = new AuthenticationAPIClient(auth0);
```

Then, login using the username and password.

```java
private void login(String email, String password) {
    //...
    String connectionName = "Username-Password-Authentication";
    client.login(email, password, connectionName)
        .setAudience(String.format("https://%s/userinfo", getString(R.string.com_auth0_domain)))
        .start(new BaseCallback<Credentials, AuthenticationException>() {
            @Override
            public void onSuccess(Credentials payload) {
                // Store credentials
                // Navigate to your main activity
            }

            @Override
            public void onFailure(AuthenticationException error) {
                // Show error to user
            }
        });
}
```

This example uses an Auth0 Database Connection called "Username-Password-Authentication" for logging in. You can also [create your own](https://manage.auth0.com/#/connections/database/new) connection.

> There are multiple ways of designing a customized login screen which are not covered in this tutorial. You can take the [Android Studio's login template](https://developer.android.com/studio/projects/templates.html) as an example.
