# Session Handling 

- [Full Tutorial](https://auth0.com/docs/quickstart/native/ios-swift/03-session-handling)

The idea of this sample is to show how to achieve session handling on your application, meaning, how to manage the authenticaion credentials to maintain the user logged in.

There are many approaches on how you can save the users credentials, with their pros and cons. In this sample project, it's saved dinamically in the `App.class`. Other means of preserving the data are excluded from the demo.

#### Important Snippets

##### 1. Save Credentials

Upon receiving credentials after a succesful login, in `LockActivity.class`:

```java
private LockCallback callback = new AuthenticationCallback() {
            @Override
            public void onAuthentication(Credentials credentials) {
				// Login Success response
				saveCredentials(credentials)
            }
				...
        };
```


##### 2. Two ways of refreshing the tokenID

With the `tokenID`, if it didn't expire.


```java
 private void getNewIDWithOldIDToken() {
        String idToken = App.getInstance().getUserCredentials().getIdToken();
        AuthenticationAPIClient authenticationClient = new AuthenticationAPIClient(new Auth0(getString(R.string.auth0_client_id), getString(R.string.auth0_domain)));
        authenticationClient.delegationWithIdToken(idToken).start(new BaseCallback<Delegation>() {
            @Override
            public void onSuccess(Delegation payload) {
            payload.getIdToken(); // New ID Token
            payload.getExpiresIn(); // New ID Token Expire Date
            }

            @Override
            public void onFailure(Auth0Exception error) {

            }
        });

    }
```

With the `refreshToken`, if the `tokenId` expired.


```java
private void getNewIDWithRefreshToken() {
        String refreshToken = App.getInstance().getUserCredentials().getRefreshToken();
        AuthenticationAPIClient authenticationClient = new AuthenticationAPIClient(new Auth0(getString(R.string.auth0_client_id), getString(R.string.auth0_domain)));
        authenticationClient.delegationWithRefreshToken(refreshToken).start(new BaseCallback<Delegation>() {
            @Override
            public void onSuccess(Delegation payload) {
                payload.getIdToken(); // New ID Token
                payload.getExpiresIn(); // New ID Token Expire Date
                Log.i("AUTH0", "new token id: " + payload.getIdToken().toString());
            }

            @Override
            public void onFailure(Auth0Exception error) {

            }
        });
```

##### 3. Log out

In `MainActivity.class`, you must simply delete the stored credentials.

```java
private void logout() {
        App.getInstance().setUserCredentials(null);
        startActivity(new Intent(this, auth0.sessiondemo.activities.StartActivity.class));
    }
```