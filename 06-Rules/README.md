# Rules 

[Full Tutorial](https://auth0.com/docs/quickstart/native/android/06-rules)

This sample on itself does not contain really valuable content; however, the only piece of code that we can stand out is how to get the information added by the rule in the example from the tutorial.

Start by renaming the `strings.xml.example` file in `app/src/main/res/values` to `strings.xml` and provide your `app_name`, `client_id` and `client_domain`.

#### Important Snippets

##### 1. Get the extra info added by a rule

Check out `MainActivity.java`:

```java
@Override
public void onSuccess(final UserProfile payload) {
	MainActivity.this.runOnUiThread(new Runnable() {
		public void run() {
			((TextView)findViewById(R.id.username)).setText(payload.getName());
			((TextView)findViewById(R.id.usermail)).setText(payload.getEmail());
			ImageView userPicture = (ImageView)findViewById(R.id.userPicture);
			Picasso.with(getApplicationContext()).load(payload.getPictureURL()).into(userPicture);

			// Get the country from the user profile
			// This is included in the extra info... and must be enabled in the Auth0 rules web.
			try {
				((TextView)findViewById(R.id.userCountry)).setText(payload.getExtraInfo().get("country").toString());
			} catch (Exception e ){
				Log.e("AUTH0", "Failed assigning country info... check if country rule is enabled in Auth0 web");
			}
		}
	});
}
```

Mainly this line:

```java
((TextView)findViewById(R.id.userCountry)).setText(payload.getExtraInfo().get("country").toString());
```

Notice the usage of the `extraInfo` hashmap there.