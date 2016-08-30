# Custom Login 

- [Full Tutorial](https://auth0.com/docs/quickstart/native/android/02-custom-login)

This sample project shows how to make up a login and a sign up with a customized screen, created by you, using  [Auth0 for Android](https://github.com/auth0/auth0.android) library.

Here is important to take note on the `activity_login.xml`, which contains the design of the login, including the buttons and editexts required to perform it.

#### Important Snippets

##### 1. Instantiate AuthenticationAPI

First, in your customized login method, instantiate the Authentication API:

```java
Auth0 auth0 = new Auth0(AUTH0_CLIENT_ID, AUTH0_DOMAIN); 
AuthenticationAPIClient client = new AuthenticationAPIClient(auth0);        
        }      
```

##### 2. Perform a Login

In `LoginActivity.class`:

```java   
client.login(email, password).start(new BaseCallback<Credentials>() {
	@Override
	public void onSuccess(Credentials payload) {
		startActivity(new Intent(getApplicationContext(), MainActivity.class));
	}

	@Override
	public void onFailure(Auth0Exception error) {
	// Manage Login failure
	}
});
```
