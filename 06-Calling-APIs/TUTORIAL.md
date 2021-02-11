# Tutorial - Calling APIs

_This content has been extracted from a previous version of the [Auth0 Android Quickstart](https://auth0.com/docs/quickstart/native/android) and demonstrates how to integrate Auth0 with an Android application using the [Auth0 Android](https://github.com/auth0/Auth0.Android) SDK._

You may want to restrict access to your API resources, so that only authenticated users with sufficient privileges can access them. Auth0 lets you manage access to these resources using [API Authorization](https://auth0.com/docs/authorization).

This tutorial shows you how to access protected resources in your API.

## Before You Start

Before you continue with this tutorial, make sure that you have completed the previous tutorials. This tutorial assumes that:
- You have completed the [Session Handling](../03-Session-Handling/TUTORIAL.md) tutorial and you know how to handle the `Credentials` object.
- You have set up a backend application as API. To learn how to do it, follow one of the [backend tutorials](https://auth0.com/docs/quickstart/backend).

## Create an Auth0 API

In the APIs section of the Auth0 dashboard, click **Create API**. Provide a name and an identifier for your API.
You will use the identifier later when you're preparing the Web Authentication.
For **Signing Algorithm**, select **RS256**.

## Add a Scope

By default, the Access Token does not contain any authorization information. To limit access to your resources based on authorization, you must use scopes. Read more about scopes in the [scopes documentation](https://auth0.com/docs/scopes).

In the Auth0 dashboard, in the APIs section, click **Scopes**. Add any scopes you need to limit access to your API resources.

> You can give any names to your scopes. A common pattern is `<action>:<resource>`. The example below uses the name `read:messages` for a scope.

## Get the User's Access Token

To retrieve an Access Token that is authorized to access your API, you need to specify the API Identifier you created in the Auth0 dashboard before. At the top of the class add the constants for accessing the API: `API_UR`L and `API_IDENTIFIER`

```java
// app/src/main/java/com/auth0/samples/LoginActivity.java

private static final String API_URL = "localhost:8080/secure";
private static final String API_IDENTIFIER = "https://api.mysite.com";

private void login() {
    Auth0 auth0 = new Auth0(this);
    auth0.setOIDCConformant(true);

    WebAuthProvider.login(auth0)
            .withScheme("demo")
            .withAudience(API_IDENTIFIER)
            .start(LoginActivity.this, new AuthCallback() {
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
                    // Verify tokens and Store credentials
                }
        });
}
```

> For instructions on how to authenticate a user, see the [Login](../00-Login/TUTORIAL.md) tutorial.

## Attach the Token

To give the authenticated user access to secured resources in your API, include the user's Access Token in the requests you send to the API.

> In this example, we use the [OkHttp](https://github.com/square/okhttp) library.

Create an instance of the `OkHttpClient` client and a new `Request`. Use the provided builder to customize the Http method, the URL and the headers in the request. Set the **Authorization** header with the token type and the user's Access Token. In the sample project an `accessToken` field is set upon authentication success with the `credentials.getAccessToken()` value.

> Depending on the standards in your API, you configure the authorization header differently. The code below is just an example.


```java
// app/src/main/java/com/auth0/samples/MainActivity.java

OkHttpClient client = new OkHttpClient();
Request request = new Request.Builder()
        .get()
        .url(API_URL)
        .addHeader("Authorization", "Bearer " + accessToken)
        .build();
```

## Send the Request

Tell the client to create a new `Call` with the request you created. Call the `enqueue` function to execute the request asynchronously.

```java
// app/src/main/java/com/auth0/samples/MainActivity.java

client.newCall(request).enqueue(new Callback() {
    @Override
    public void onFailure(Request request, final IOException e) {
        // Show error
    }

    @Override
    public void onResponse(final Response response) throws IOException {
        if (response.isSuccessful()) {
            // API call success
        } else {
            // API call failed. Check http error code and message
        }
    }
});
```

You need to configure your backend application to protect your API endpoints with the key for your Auth0 application, API identifier and API scopes. In this example, you can use the user's Access Token issued by Auth0 to call your own APIs.
