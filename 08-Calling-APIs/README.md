# Calling APIs 

- [Full Tutorial](https://auth0.com/docs/quickstart/native/android/08-calling-apis)

The idea of this project is to perform authenticated requests by attaching the `idToken`, obtained upon login, into an authorization header.

This sample can be seen as a template where you'll have to set your own stuff in order to get it working. Pay attention to the snippets where you have to do that.

Also, you will need a server that accepts authenticated APIs with an endpoint capable of checking whether or not a request has been properly authenticated. You can use your own or [this nodeJS one](https://github.com/auth0-samples/auth0-angularjs2-systemjs-sample/tree/master/Server), whose setup is quite simple.

#### Important Snippets

##### 1. Call your API

The only important snippet you need to be aware of: making up an authenticathed request for your API!

Look at `MainActivity.java`:

```java
RequestQueue queue = Volley.newRequestQueue(this);
String url = "your api url"; 
        
AuthorizationRequestObject authorizationRequest = new AuthorizationRequestObject(Request.Method.GET, url, App.getInstance().getUserCredentials().getIdToken(), null, 
new Response.Listener<JSONObject>() {

	@Override
	public void onResponse(JSONObject response) {
		// Parse Response
	}
}, 
new Response.ErrorListener() {

	@Override
	public void onErrorResponse(VolleyError error) {

	}
}); 
```

These are the specific lines of code that you have to configure:

First, set your API url here:

```java
RequestQueue queue = Volley.newRequestQueue(this);
String url = "your api url"; 
```

Also, pay attention to the `AuthorizationRequestObject.class`, in which you add the header required to authenticate:

```java
Map headers = new HashMap();
headers.put("Bearer \\"+headerTokenID, "Authorization");
return headers;
```

That string interpolation might vary depending on the standards that your API follows. 

> For further information on the authentication process, check out [the full documentation](https://auth0.com/docs/api/authentication).