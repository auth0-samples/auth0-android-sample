# Seed Project 

- [Full Tutorial](https://auth0.com/docs/quickstart/native/android/00-introduction)

This is a Seed Project that you can use as the kick-off of any of your projects.
It only includes an empty Start Activity and the basic Lock configuration to start working in any of the tutorials.

Remember to modify the `app_name`, `client_id` and `client_domain` inside `Strings.xml`.

Also, you can modify the package path. Remember to do it in the folder path, the `android-manifest.xml` and the `app.gradle`.

#### Important Snippets

##### 1. Add Lock dependency
Inside the `app.gradle` dependencies:


```xml
compile 'com.auth0.android:lock:2.0.0-beta.3'   
```

> From here on, try any of the quickstarts!
> We suggest you to start with the [Login Quickstart](/quickstart/native/android)