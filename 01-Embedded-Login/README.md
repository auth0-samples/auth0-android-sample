# Login

This tutorial will show you how to integrate Lock v2 in your Android project in order to present an Embedded Login screen.

Tested using:
- Android Studio 2.3'
- Android SDK 25'
- Emulator - Nexus 5X - Android 6.0'


**IMPORTANT NOTICE**

Username/Email & Password authentication from native clients is disabled by default for new tenants as of 8 June 2017. Users are encouraged to use the [Hosted Login Page](https://auth0.com/docs/hosted-pages/login) and perform Web Authentication instead. If you still want to proceed you'll need to enable the Password Grant Type on your dashboard first. See [Client Grant Types](https://auth0.com/docs/clients/client-grant-types) for more information.


## Add the Lock Dependency

Your first step is to add [Lock](https://github.com/auth0/Lock.Android) into your project. Lock is a library for displaying a native UI in your app for logging in and signing up with different platforms via [Auth0](https://auth0.com/).


Inside the app's `build.gradle` dependencies section:

```xml
apply plugin: 'com.android.application'
android {
  //..
}
dependencies {
  //---> Add the next line
  compile 'com.auth0.android:lock:2.+'
  //<---
}
```

> You can check for the latest version on the repository [Readme](https://github.com/auth0/Lock.Android#install), in [Maven](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22lock%22%20g%3A%22com.auth0.android%22), or in [JCenter](https://bintray.com/auth0/android/lock).


Now add the _Manifest Placeholders_, required by the SDK to define internally an **intent-filter** to capture the authentication callback. You do that by adding the next line:

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

## Set Credentials

Your application needs some details about your client to communicate with Auth0. You can get these details from the **Settings** section for your client in the [Auth0 dashboard](https://manage.auth0.com/#/).

You need the following information:

* **Client ID**
* **Domain**

We suggest you do not hardcode these values as you may need to change them in the future. Instead, use [String Resources](https://developer.android.com/guide/topics/resources/string-resource.html), such as `@string/com_auth0_domain`, to define the values.

Edit your `res/values/strings.xml` file as follows:

```xml
<resources>
    <string name="com_auth0_client_id">{YOUR_CLIENT_ID}</string>
    <string name="com_auth0_domain">{YOUR_DOMAIN}</string>
</resources>
```

Remember to replace the values with your client's information.

Then, run **Sync Project with Gradle Files** inside Android Studio or `./gradlew clean assembleDebug` from the command line.

> For more information about Gradle usage, check [their official documentation](https://gradle.org/getting-started-android-build/).



## Configure the Manifest File

Declare the `LockActivity` in your project's `AndroidManifest.xml`:

```xml
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="com.auth0.samples">

    <uses-permission android:name="android.permission.INTERNET" />

    <application
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@style/AppTheme">

        <activity android:name="com.auth0.samples.MainActivity">
          <intent-filter>
              <action android:name="android.intent.action.MAIN" />
              <category android:name="android.intent.category.LAUNCHER" />
          </intent-filter>
        </activity>

        <activity
            android:name="com.auth0.android.lock.LockActivity"
            android:label="@string/app_name"
            android:launchMode="singleTask"
            android:screenOrientation="portrait"
            android:theme="@style/Lock.Theme"/>

    </application>

</manifest>
```

It's very important to specify the `android:launchMode="singleTask"` in your activity to ensure the authentication state it's not lost along redirects and that the result arrives back in the same activity instance that first requested it.

The next step is to whitelist the **Callback URL** of your client. Edit the "Allowed Callback URLs" section of the [Client settings](https://manage.auth0.com/#/clients) and add an URL that looks like this:

```text
demo://{YOUR_DOMAIN}/android/YOUR_APP_PACKAGE_NAME/callback
```

Like before, replace `YOUR_DOMAIN` with your account domain and `YOUR_APP_PACKAGE_NAME` with your application's package name, available in the app's `build.gradle` file as the `applicationId` attribute.

> Do not add `<android:noHistory="true">` to the `LockActivity` as this will alter the correct functionality of **Lock**.



## Implement the Login

At this point, you're all set to implement the login in any activity you want. Inside the activity, override the `onCreate` and `onDestroy` methods to initialize and release **Lock**, and define an instance of `LockCallback` to handle the authentication result. The `Auth0` instance holds the client information such as Client ID and Domain. If you've added the `R.string.com_auth0_client_id` and `R.string.com_auth0_domain` String resources, you'll be able to use the constructor that receives an android Context. If you prefer to hardcode them, use the constructor that receives both strings.

To ensure Open ID Connect compliant responses you must either request an `audience` or enable the **OIDC Conformant** switch in your Auth0 dashboard under `Client / Settings / Advanced OAuth`. You can read more about this [here](https://auth0.com/docs/api-auth/intro#how-to-use-the-new-flows).


```java
@Override
protected void onCreate(Bundle savedInstanceState) {
    Auth0 auth0 = new Auth0(this);
    lock = Lock.newBuilder(auth0, callback)
                    .withScheme("demo")
                    .withAudience(String.format("https://%s/userinfo", getString(R.string.com_auth0_domain)))
                    // Add parameters to the Lock Builder
                    .build(this);
}
```

```java
@Override
protected void onDestroy() {
    super.onDestroy();
    // Your own Activity code
    lock.onDestroy(this);
    lock = null;
}
```

```java
private LockCallback callback = new AuthenticationCallback() {
    @Override
    public void onAuthentication(Credentials credentials) {
        // Login Success response
    }

    @Override
    public void onCanceled() {
        // Login Cancelled response
    }

    @Override
    public void onError(LockException error){
        // Login Error response
    }
};
```

Finally, whenever you want to start the login widget, call:

```java
startActivity(lock.newIntent(this));
```


> If you require in-depth configuration, see [Lock Builder](/libraries/lock-android#lock-builder) for more information

> There are multiple ways of implementing the login dialog. What you see above is the default widget; however, if you want, you can use [your own UI](/quickstart/native/android/02-custom-login).


### Optional: Log In with Social Connections

To have a simple login mechanism through social connections, all you have to do is enable them in your account's [dashboard](${manage_url}/#/connections/social). Every social connection you switch on there, will appear in the login screen of your app.
