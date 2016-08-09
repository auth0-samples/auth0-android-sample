# Customizing Lock

- [Full Tutorial](https://auth0.com/docs/quickstart/native/android/10-customizing-lock)

This demo project contains a basic structure that includes a customized lock activity.

### Important Snippets

How to create a theme:

```java
Theme customizedLockTheme = Theme.newBuilder()
	.withHeaderTitle(R.string.app_name)
	.build();
```

How can you add it to the lock activity:

```java
this.lock = Lock.newBuilder(auth0, callback)
	.withTheme(customizedLockTheme)
	.build();
```	