# Auth0 Android SDK Sample Application - Kotlin

This sample demonstrates the integration of [Auth0 Android SDK](https://github.com/auth0/Auth0.Android) into a Kotlin Android application. The sample is a companion to the [Auth0 Android SDK Quickstart](https://auth0.com/docs/quickstart/native/android).

This sample demonstrates the following use cases:

- Login
- Logout
- Showing the user profile
- Geting the user metadata
- Updating the user metadata

## Requirements

Android API version 21 or later and Java 8+.

## Project setup

### Auth0 Credentials

Configure the application with details of the Auth0 domain and client ID of your application. Locate the `/app/src/main/res/values/strings.xml` file and replace the placeholder `{CLIENT_ID}` and `{DOMAIN}` values with your application's client ID and domain.

```xml

<resources>
    <string name="app_name">Login</string>

    <string name="com_auth0_client_id">{CLIENT_ID}</string>
    <string name="com_auth0_domain">{DOMAIN}</string>
    <string name="login">Log in</string>
    <string name="logout">Log out</string>
</resources>

```

### Callback and logout URLs

In the **Settings** tab of your Auth0 application, add the following to your **Allowed Callback URLs** and **Allowed Logout URLs**: `demo://{YOUR-AUTH0-DOMAIN}/android/com.auth0.androidsample/callback`. Be sure to click **Save Changes** before leaving the page.

## Running the app

Run the application using Android Studio. You can then login, view information about the user profile and user metadata, update the user metadata, and logout. 

## What is Auth0?

Auth0 helps you to:

* Add authentication with [multiple sources](https://auth0.com/docs/identityproviders), either social identity providers such as **Google, Facebook, Microsoft Account, LinkedIn, GitHub, Twitter, Box, Salesforce** (amongst others), or enterprise identity systems like **Windows Azure AD, Google Apps, Active Directory, ADFS, or any SAML Identity Provider**.
* Add authentication through more traditional **[username/password databases](https://auth0.com/docs/connections/database/custom-db)**.
* Add support for **[linking different user accounts](https://auth0.com/docs/users/user-account-linking)** with the same user.
* Support for generating signed [JSON Web Tokens](https://auth0.com/docs/tokens/json-web-tokens) to call your APIs and **flow the user identity** securely.
* Analytics of how, when, and where users are logging in.
* Pull data from other sources and add it to the user profile through [JavaScript rules](https://auth0.com/docs/rules).

## Create a Free Auth0 Account

1. Go to [Auth0](https://auth0.com) and click **Sign Up**.
2. Use Google, GitHub, or Microsoft Account to login.

## Issue Reporting

If you have found a bug or if you have a feature request, please report them at this repository issues section. Please do not report security vulnerabilities on the public GitHub issue tracker. The [Responsible Disclosure Program](https://auth0.com/responsible-disclosure-policy) details the procedure for disclosing security issues.

## Author

[Auth0](https://auth0.com)

## License

This project is licensed under the MIT license. See the [LICENSE](../LICENSE) file for more info.