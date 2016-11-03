# Authorization 

[Full Tutorial](https://auth0.com/docs/quickstart/native/android/07-authorization)

Most of the tutorial is explained in the docs, this demo app is aimed to check if the created authorization rule, works properly.

Start by renaming the `strings.xml.example` file in `app/src/main/res/values` to `strings.xml` and provide your `app_name`, `client_id` and `client_domain`.

#### Important Snippets

##### 1. Check the user role

Look at `MainActivity.java`, if the user is an admin, we navigate it to the settings screen:

```java
private void toSettings() {
	String role = mUserProfile.getAppMetadata().get("roles").toString();

	if(role.contains("admin"))
		startActivity(new Intent(this, SettingsActivity.class));
	else
		Toast.makeText(MainActivity.this, "You don't have access rights to visit this page", Toast.LENGTH_SHORT).show();
    }
}
```
