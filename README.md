# Quovo-AndroidSDK

## Latest Release

### [v1.1.3](https://bintray.com/quovo/maven/connect-android/1.1.3)
* Connect v2 Support
* Custom Subdomain Option
* Added syncType and hideTray to options
* Prevent Multiple Instances
* Fixed various bugs

## Table Of Contents
<!--ts-->
* [Installation](#installation)
* [Using The Demo Project](#using-the-demo-project)
* [Quovo Connect SDK](#quovo-connect-sdk)
    * [Initialize the SDK](#initialize-the-connect-sdk)
    * [Create a Completion Handler](#create-a-completion-handler-for-connect)
    * [Create an Error Handler](#create-an-error-handler-for-connect)
    * [Launch the SDK](#launch-the-connect-sdk)
    * [Close the SDK](#close-the-connect-sdk)
    * [Customization](#connect-customization)
        * [Custom Navbar Title](#custom-connect-navbar-title)
        * [Custom Navbar Background Color](#custom-connect-navbar-background-color)
        * [Enable or Disable the Progressbar](#enable-or-disable-the-progressbar-for-connect)
        * [Custom Timeout](#custom-connect-timeout)
        * [Custom Subdomain](#custom-connect-subdomain)
        * [Options](#options-for-connect)
            * [Preselect an Institution](#preselect-an-institution)
            * [Update or Resolve Issues on an Existing Connection](#update-or-resolve-issues-on-an-existing-connection)
<!--te-->

## Installation

The SDK can be added by including the following line in the dependency block of your `build.gradle` file:
```gradle
dependencies {
    // ...
    implementation 'com.quovo.connect:connect-android:1.1.3'
}
```

You may also need to add the Quovo repository to your root `build.gradle`:
```gradle
allprojects {
    repositories {
        // ...
        maven { url 'https://dl.bintray.com/quovo/maven' }
    }
}
```

## Using the Demo Project

The demo project included with the SDK uses a configuration properties file to generate its user token. The file is git-ignored but should be added to your copy of the demo project in the root folder. The file should be named "configuration.properties" and should contain:

```
apiToken=_YOUR_API_TOKEN_
userId=_YOUR_USER_ID_
```


## Quovo Connect SDK

### Initialize the Connect SDK

```java
import com.quovo.sdk.QuovoConnectSdk;

QuovoConnectSdk.Builder quovoConnectSdk = new QuovoConnectSdk.Builder(this); // this is context
```
A good place to initialize the SDK is upon app launch or in the launch method of a view.

### Create a Completion Handler for Connect

```java
import com.quovo.sdk.listeners.OnCompleteListener

// Note: Be careful interacting with the activity in the listeners as they may get called after the activity is destroyed
quovoConnectSdk.setOnCompleteListener((callback, response) -> {
    Log.d("callback", callback);
    Log.d("response", response);
});

```
The completion handler will allow your app to listen for events that will be fired by the QuovoConnectSDK.  The handler has 2 parameters: a "callback" method name and an optional "response" payload. The "callback" string will be one of the following:

* open
* load
* close
* add
* sync

In the case of "add" and "sync" a response payload string will be returned.
Here are some examples:

"Add" event fired:
```java
{
    "connection": [
        "id": 2135634,
        "institution": 34,
        "user": 1123,
    ],
    "timeStamp": 1496879583157,
}
```

"Sync" event fired
```java
{
    "connection": [
        "id": 2135634,
        "institution": 34,
        "user": 1123,
    ],
    "sync": [
        "authenticated": false,
        "status": "questions",
    ],
    "timeStamp": 1496879583157,
}
```

The other callbacks will yield an empty response. For more information on these events, please see:
(https://api.quovo.com/docs/connect/#custom-integrations)

### Create an Error Handler for Connect

```java
import com.quovo.sdk.listeners.OnErrorListener;

// Note: Be careful interacting with the activity in the listeners as they may get called after the activity is destroyed
quovoConnectSdk.setOnErrorListener((errorType, code, message) -> {
    Log.d("errorType", errorType);
    Log.d("code", code);
    Log.d("message", message);
});

```
The errorType argument will be one of:

* general
* http
* application

For "general" errorType, codes are defined ERROR_* constants in WebViewClient class.
https://developer.android.com/reference/android/webkit/WebViewClient

For "http" errorType, it will be one of standard HTTP response codes in >=400 range.

For "application" errorType:

100 : ERROR_APPLICATION_NOT_FRAGMENT_ACTIVITY - context used is not a subclass of FragmentActivity when attempting to launch the SDK fragment.

### Launch the Connect SDK

Launching the QuovoConnectSDK will instantiate a WebView experience that allows users to sync and manage their accounts. The minimum required parameter for launching the WebView is an Iframe Token.  This token must be generated via the API and will expire after its first use.
```java
quovoConnectSdk.launch(userToken);
```

Embeddable fragment may be launched into a ViewGroup in your XML layout, e.g. LinearLayout.
```java
quovoConnectSdk.launchFragment(userToken, containerResId);
```

### Close the Connect SDK

The QuovoConnectSDK can be closed statically by using the QuovoConnectSDK class. This allows the SDK to be closed from the parent Activity as well as a broadcast or other external message.

```java
QuovoConnectSdk.close();
```

* This cannot be used to close the SDK fragment.

### Connect Customization

### Custom Connect Navbar Title

You also have the option to customize the navbar title for the QuovoConnect WebView:

```java
quovoConnectSdk.customTitle("Connect your accounts");
```

### Custom Connect Navbar Background Color

You also have the option to customize the navbar background for the QuovoConnect WebView:

```java
quovoConnectSdk.customHeaderBackground(Color.TRANSPARENT);
```

### Enable or Disable the progressbar for Connect

You also have the option to enable or disable the progressbar from the navbar for the QuovoConnect WebView:

```java
quovoConnectSdk.setProgressBarEnable(false);
```

### Custom Connect Timeout

By default the Quovo Connect WebView will timeout after 30 seconds of attempting to connect. There is an option to customize the timeout length in milliseconds by calling `setTimeoutLength`, which takes a `Integer` parameter . When a timeout occurs an error will be sent to the ErrorHandler and the WebView will display a simple page stating that the connection timed out. If you do not want to display the timeout page you can catch the error in the ErrorHandler and close the SDK before it appears or timeout before the SDK does.

```java
quovoConnect.setTimeoutLength(5000);
```
#### Custom Connect Subdomain

By default the Connect SDK will connect to the original Quovo Connect, however there is a way to use Connect v2. By calliing `setSubdomain` (which takes a `String`) you can set a custom subdomain to be used when loading connect. If you want to load Connect v2, you can pass in `connect2`.

```swift
quovoConnect.setSubdomain(subdomain:"connect2")
```

### Options for Connect

You can optionally pass in a set of parameters that control the appearance and functionality of the WebView experience.  An example of this is:
```java
HashMap<String, Object> options = new HashMap<>();
options.put("testInstitutions", 1);
options.put("topInstitutions", "banks");

quovoConnectSdk.launch(userToken, options);
```

The following is a list of the optional parameters that can be supplied to the launch method:

| Field                | Type          | Default       | Description |
| -------------------- | ------------- | ------------- | ----------- |
| topInstitutions      | string        | 'all'         | Choose what type of institutions, if any, will be displayed in the Top Institutions portion of the institution select screen. Possible values are `banks`, `brokerages`, `all`, or `none`. |
| enableAuthDeposits   | integer (bit) | 0             | If on, the [Auth Deposits](https://api.quovo.com/docs/auth/#auth_deposits) workflow will be enabled within Connect. This lets end users verify their bank accounts on any institution not covered by instant account verification. Note: This workflow is _not_ available by default. [Contact us](mailto:support@quovo.com) if you would like access to Auth Deposits within Connect. |
| singleSync           | integer (bit) | 0             | If on, the "Connect Another Account" button will be hidden. This button appears once an Account has been successfully synced to prompt the User to add any additional Accounts they may have. |
| searchTest           | integer (bit) | 0             | If on, Quovo test institutions will be searchable within Connect. |
| openInstitution      | integer       |               | [See Preselect an Institution](#preselect-an-institution) |
| openConnection       | integer       |               | [See Update or Resolve Issues on an Existing Connection](#update-or-resolve-issues-on-an-existing-connection) |
| hideTray  | integer (bit) | 0 |   If on, the tray showing notifications will be hidden (Note: Only applies to Connect v2) |
| syncType             | String       |                | Choose what type of connection syncs are performed within Connect. Possible values are `agg`, `auth`, or `both`, which will simultaneously run an agg AND auth sync on new connections. This parameter is optional and will default to agg. More information on integrating account verification with Connect can be found here. (https://api.quovo.com/docs/v3/ui/#auth)

#### Preselect an Institution

You may want to direct users to add Accounts onto specific institutions. With Connect, you can preselect an institution for users and bypass the search page entirely.

Pass the desired Quovo Instition ID as the value.

```java
HashMap<String, Object> options = new HashMap<>();
// Connect will bypass the search page and open directly to the page to
// add a "Fidelity NetBenefits" Account (which has a Brokerage ID of 23).
options.put("openInstitution", 23);

quovoConnectSdk.launch(userToken, options);
```

#### Update or Resolve Issues on an Existing Connection

You may want users to update or resolve issues on existing connections. They may need to supply additional MFA answers or update recently changed login credentials. With Connect, you can simply pass an Account ID to direct users to fix these issues, allowing their Accounts to continue syncing. Connections with a "login" status will be taken to a screen where users can update their credentials, while connections with a "questions" status will be taken to a screen where users are prompted to answer additional MFA questions.

If both `openConnection` and `openInstitution` arguments are supplied to `launch`, the `openConnection` workflow will take priority.

```java
HashMap<String, Object> options = new HashMap<>();
// Account 813981 has a status of "questions", so Connect will open to a
// page where the user can answer any outstanding MFA questions and resync
// the Account accordingly.
options.put("openConnection", 813981);

quovoConnectSdk.launch(userToken, options);
```

