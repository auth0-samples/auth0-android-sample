# User Profile

[Full Tutorial](https://auth0.com/docs/quickstart/native/android/04-user-profile)

The idea of this sample is to show how to use Lock v2 to get the user's profile data in your android apps with Auth0.

Start by renaming the `strings.xml.example` file in `app/src/main/res/values` to `strings.xml` and provide your `app_name`, `client_id` and `client_domain`.

#### Important Snippets

##### 1. Get the user profile

Using an `AuthenticationApiClient` instance:

```java
 client.tokenInfo(App.getInstance().getUserCredentials().getIdToken())
                .start(new BaseCallback<UserProfile, AuthenticationException>()  {
	@Override
	public void onSuccess(UserProfile payload){
	}

	@Override
	public void onFailure(AuthenticationException error){
	}
```


##### 2. Update the user profile

Using an `UserApiClient` instance:

```java
userClient.updateMetadata(mUserProfile.getId(), userMetadata).start(new BaseCallback<UserProfile, ManagementException>() {
	@Override
	public void onSuccess(final UserProfile payload) {
	// As receive the updated profile here
	// You can react to this, and show the information to the user.
	}

	@Override
	public void onFailure(ManagementException error) {

	}
});
```
