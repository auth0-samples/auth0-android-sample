# Embedded Lock - Login

**IMPORTANT NOTICE**

Username/Email & Password authentication from native clients is disabled by default for new tenants as of 8 June 2017. Users are encouraged to use the [Hosted Login Page](https://auth0.com/docs/hosted-pages/login) and perform Web Authentication instead. If you still want to proceed you'll need to enable the Password Grant Type on your dashboard first. See [Client Grant Types](https://auth0.com/docs/clients/client-grant-types) for more information.

## Requirements
The `Lock.Android` library requires and app that:
- Targets Android API 21 and above
- Uses Java version 8 and above

## Running the sample
In order to run the sample, visit the [Auth0 Dashboard](https://manage.auth0.com/dashboard/) and create an Auth0 application of type "Native". From here, grab the `Client ID` and `Domain` values. 

Add `https://{YOUR_DOMAIN}/android/{YOUR_APP_ID}/callback` to the "Allowed Callback URLs" replacing the placeholders with the actual values from your Auth0 account and Android application, and then click the SAVE button.

Open the sample app project with Android Studio and replace the placeholders in the `res/values/strings.xml.example` file with the values previously obtained from the Auth0 Dashboard. Remove the `example` extension of this file before you try running the app.
 

## Detailed instructions
The features covered in this sample app are described in detail in the [Lock.Android: Get Started](https://auth0.com/docs/libraries/lock-android) article available in our docs. 