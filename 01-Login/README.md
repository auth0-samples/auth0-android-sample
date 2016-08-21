# Login 

- [Full Tutorial](https://auth0.com/docs/quickstart/native/android/01-login)

This sample project shows you how to present a login dialog using the Lock10 interface. Once you log in, you're taken to a very basic welcome screen.

#### Important Snippets

##### 1. Define the login activity

In `LockActivity.class`, inside the ``onCreate`` method:

```java
Auth0 auth0 = new Auth0(${account.clientId}, ${account.namespace});
            this.lock = Lock.newBuilder(auth0, callback)
                    // Add parameters to the Lock Builder
                    .build();
```

Add to the life cycle:

```java
 protected void onDestroy() {
            super.onDestroy();
            // Your own Activity code
            lock.onDestroy(this);
            lock = null;
        }
```

And then, callback method:

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

            startActivity(this.lock.newIntent(this));

```


##### 2. Start the activity

You can do this from any screen, ie. `StartActivity.class` whenever is required.

```java
            startActivity(this.lock.newIntent(this));
```
