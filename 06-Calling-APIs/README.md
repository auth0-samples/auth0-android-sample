# Calling APIs

This sample shows how to perform authentication against your API after logging in a user and obtaining its token.

A step by step [Tutorial](./TUTORIAL.md) is provided in our site. If you prefer to skip it and run the sample make sure to [setup](#setup) the project first.

### Requirements

This sample app runs on Android versions 21 and above.

# Setup

## Auth0 Credentials

The Auth0 SDK needs to know a few details from the Client to correctly display the Login Page. 

Locate the `res/values/strings.xml.example` file in your project resources and remove the `.example` extension. Add the Auth0 Domain and Client ID values present on your [client settings](https://manage.auth0.com/#/clients) page.

Given a Domain value of `"myuser.auth0.com"` and a Client ID value of `"1234567890abcdf"`, the file would look like:

```xml
<resources>
    <!-- ... -->
    <string name="com_auth0_client_id">1234567890abcdf</string>
    <string name="com_auth0_domain">myuser.auth0.com</string>
</resources>
```

## Manifest Placeholders

Locate the `app/build.gradle` file and ensure the `manifestPlaceholder` property is set inside the android defaultConfig object. The `auth0Domain` and `auth0Scheme` placeholders are used internally by the Auth0 SDK to create an Intent Filter that captures the Web Authentication result. 

```groovy
apply plugin: 'com.android.application'
android {
    compileSdkVersion 25
    buildToolsVersion "25.0.3"
    defaultConfig {
        applicationId "com.auth0.samples"
        targetSdkVersion 25
        //...

        //---> Add the next line
        manifestPlaceholders = [auth0Domain: "@string/com_auth0_domain", auth0Scheme: "demo"]
        //<---
    }
}
```

> Important! The manifest placeholders are required for the code to compile.

The placeholders in the snippet make use of the domain we've added in the [previous step](#auth0-credentials) and also hardcode a `"demo"` Url scheme. The scheme value can also be saved in the `strings.xml` file if that's prefered. If we change this value we also need to update the call to the `WebAuthProvider` class where we set the scheme that's going to be used.


## Callback URL

When authentication is requested the sample app will expect a call to the Callback URL with the result of the authentication. This URL is constructed using the values set in the manifest placeholders. In your [client settings](https://manage.auth0.com/#/clients) page, make sure to add under the "Allowed Callback URLs" section a value like the following:

```
{Scheme}://{Domain}/android/{ApplicationId}/callback
```

Replace the Scheme, Domain and ApplicationId values with the ones used in your application. Continuing the example from the previous section, the final Callback URL would look like:

```
demo://myuser.auth0.com/android/com.auth0.samples/callback
```

Make sure to click "Save Changes" before leaving the page.