# User Profile

[Full Tutorial](https://auth0.com/docs/quickstart/native/android/05-linking-accounts)

The idea of this sample is to show how to use Lock v2 to to link two different accounts for the same user.

Start by renaming the `strings.xml.example` file in `app/src/main/res/values` to `strings.xml` and provide your `app_name`, `client_id` and `client_domain`.

#### Important Snippets

##### 1. Link an account

After you make a succesful login, and save the credentials, you must make a login with a new account and merge the accounts using both `Credentials`.

```java
UsersAPIClient client = new UsersAPIClient(auth0, credentials.getIdToken());                client.link(App.getInstance().getUserCredentials().getIdToken(), secondaryCredentials.getIdToken());
```


##### 2. Unlink an account

Using an `UserApiClient` instance, you must add as parameters the main account ID and from the desired to be unliked account both ID and provider name.

```java
UsersAPIClient client = new UsersAPIClient(mAuth0, App.getInstance().getUserCredentials().getIdToken());
client.unlink(primaryUserId, secondaryUserId, secondaryProvider);
```
