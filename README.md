# Rules 

The guts of this topic is actually found in the [full tutorial](https://auth0.com/docs/quickstart/native/ios-swift/06-rules), where it's exposed how to configure a rule from the Auth0 management website.

This sample on itself does not contain really valuable content; however, the only piece of code that we can stand out is how to get the information added by the rule in the example from the tutorial.

#### Important Snippets

##### 1. Get the extra info added by a rule

Check out `ProfileViewController.swift`:

```swift
override func viewDidLoad() {
    super.viewDidLoad()
    self.profile = SessionManager().storedProfile
    self.welcomeLabel.text = "Welcome, \(self.profile.name)"
    self.retrieveDataFromURL(self.profile.picture) { data, response, error in
        dispatch_async(dispatch_get_main_queue()) {
            guard let data = data where error == nil else { return }
            self.avatarImageView.image = UIImage(data: data)
        }
    }
    self.countryLabel.text = (self.profile.extraInfo["country"] as? String) ?? "<Country not detected>"
}
```

Mainly this line:

```swift
self.countryLabel.text = (self.profile.extraInfo["country"] as? String) ?? "<Country not detected>"
```

Notice the usage of the `extraInfo` dictionary there.