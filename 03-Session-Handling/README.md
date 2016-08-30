# Session Handling 

- [Full Tutorial](https://auth0.com/docs/quickstart/native/ios-swift/03-session-handling)

The idea of this sample is to show how to achieve session handling on your application, meaning, how to manage the authenticaion credentials to maintain the user logged in.

There are many approaches on how you can save the users credentials, with their pros and cons. In this sample project, it's saved dinamically trough the `CredentialsManager.class`. Other means of preserving the data are excluded from the demo.

#### Important Snippets

##### 1. Save Credentials

Upon receiving credentials after a succesful login, in `LockActivity.class`:

```java
private LockCallback callback = new AuthenticationCallback() {
	@Override
	public void onAuthentication(Credentials credentials) {
		// Login Success response
		CredentialsManager.saveCredentials(credentials)
	}
	
	...
   
};
```
##### 2. Check if user session is still active

At startup, check out if the `idToken` exists.

```java
if(CredentialsManager.getCredentials(this).getIdToken() == null) {
	// Prompt Login screen.
} 
else {
	// Try to make an automatic login
}
```

And then, if it's valid:

```java
AuthenticationAPIClient aClient = new AuthenticationAPIClient(auth0);
aClient.tokenInfo(CredentialsManager.getCredentials(this).getIdToken())
       .start(new BaseCallback<UserProfile, AuthenticationException>() {
	@Override
	public void onSuccess(final UserProfile payload) {
		// Valid ID > Navigate to the app's MainActivity
		startActivity(new Intent(getApplicationContext(), MainActivity.class));
	}
	@Override
	public void onFailure(AuthenticationException error) {
		// Invalid ID Scenario		
	}
});
```

##### 3. Two ways of refreshing the tokenID

First, for both cases, you need to instantiate an `AuthenticationAPIClient`:

```java
AuthenticationAPIClient client = new AuthenticationAPIClient(
      new Auth0(AUTH0_CLIENT_ID, AUTH0_DOMAIN));
```

With the `tokenID`, if it didn't expire.

```java
private void getNewIDWithOldIDToken() {
	String idToken = CredentialsManager.getCredentials(this).getIdToken();
	client.delegationWithRefreshToken(refreshToken)
      .start(new BaseCallback<Delegation>() {
      
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
	String refreshToken = CredentialsManager.getCredentials(this).getRefreshToken();
	
	client.delegationWithRefreshToken(refreshToken)
      .start(new BaseCallback<Delegation>() {
      
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

##### 4. Log out

In `MainActivity.class`, you must simply delete the stored credentials.

```java
private void logout() {
	CredentialsManager.deleteCredentials();
	startActivity(new Intent(this, StartActivity.class));
}
```