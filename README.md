# Quovo-AndroidSDK

## Latest Release

### [v1.1.4](https://bintray.com/quovo/maven/connect-android/1.1.4)
* Fixed account-details for connect 2
* Fixed various bugs

## Table Of Contents
<!--ts-->
* [Installation](#installation)
* [Using The Demo Project](#using-the-demo-project)
* [Quovo Connect SDK](#quovo-connect-sdk)
    * [Initialize the SDK](#initialize-the-connect-sdk)
    * [Create a Completion Handler](#create-a-completion-handler-for-connect)
        * [Connect v1 Callbacks](#connect-v1-callbacks)
        * [Connect v2 Callbacks](#connect-v2-callbacks)
    * [Create an Error Handler](#create-an-error-handler-for-connect)
    * [Launch the SDK](#launch-the-connect-sdk)
    * [Close the SDK](#close-the-connect-sdk)
    * [Customization](#connect-customization)
        * [Custom Navbar Title](#custom-connect-navbar-title)
        * [Custom Navbar Background Color](#custom-connect-navbar-background-color)
        * [Enable or Disable the Progressbar](#enable-or-disable-the-progressbar-for-connect)
        * [Custom Timeout](#custom-connect-timeout)
        * [Custom Subdomain](#custom-connect-subdomain)
        * [Options for Connect v1](#options-for-connect-v1)
        * [Options for Connect v2](#options-for-connect-v2)
        * [Preselect an Institution](#preselect-an-institution)
        * [Update or Resolve Issues on an Existing Connection](#update-or-resolve-issues-on-an-existing-connection)
<!--te-->

## Installation

The SDK can be added by including the following line in the dependency block of your `build.gradle` file:
```gradle
dependencies {
    // ...
    implementation 'com.quovo.connect:connect-android:1.1.4'
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
The completion handler will allow your app to listen for events that will be fired by the QuovoConnectSDK.  The handler has 2 parameters: a "callback" method name and an optional "response" payload. 

### Connect v1 Callbacks

The "callback" strings supported by connect v1 are the following:

* open
* load
* close
* add
* sync
* onAuthenticate

In the case of "add", "sync" and "onAuthenticate" a Json response payload of type `String` will be returned.
Here are some examples:

### Connect v2 Callbacks

The "callback" strings supported by connect v2 are the following:

* open
* load
* close
* add
* sync
* onAuthenticate
* onAuthAccountSelected

In the case of "add", "sync",  "onAuthenticate" and onAuthAccountSelected a Json response payload of type `String` will be returned.

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

### Options for Connect v1

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
| singleSync           | boolean | 0             | If true, the back arrow on the top left will be removed and “Add Another” button will be hidden during the syncing workflow and the playback steps. This parameter is optional and will default to false. |
| hideTray  | integer (bit) | 0 |   If on, the tray showing notifications will be hidden |
| searchTest           | integer (bit) | 0             | If on, Quovo test institutions will be searchable within Connect. |
| openInstitution      | integer       |               | [See Preselect an Institution](#preselect-an-institution) |
| openConnection       | integer       |               | [See Update or Resolve Issues on an Existing Connection](#update-or-resolve-issues-on-an-existing-connection) |
| syncType             | String       |                | Choose what type of connection syncs are performed within Connect. Possible values are `agg`, `auth`, or `both`, which will simultaneously run an agg AND auth sync on new connections. This parameter is optional and will default to both. More information on integrating account verification with Connect can be found here. (https://api.quovo.com/docs/v3/ui/#auth)

### Options for Connect v2

You can optionally pass in a set of parameters that control the appearance and functionality of the WebView experience.  An example of this is:
```java
HashMap<String, Object> options = new HashMap<>();
options.put("testInstitutions", 1);
options.put("topInstitutions", "banks");

quovoConnectSdk.launch(userToken, options);
```

The following is a list of the optional parameters that can be supplied to the launch method for Connect2:

| Field                | Type          | Default       | Description |
| -------------------- | ------------- | ------------- | ----------- |
| topInstitutions      | string or array    | 'all'         | Choose what type of institutions, if any, will be displayed in the Top Institutions portion of the institution select screen. Possible values are `banks`, `brokerages`, `all`, or `none`. <br>If you'd like to customize the default institutions you can pass in an array of institution ids like: `[1249,1209,2779,2782]` |
| searchTest           | integer (bit) | 0             | If on, Quovo test institutions will be searchable within Connect. |
| openInstitution      | integer       |               | [See Preselect an Institution](#preselect-an-institution) |
| openConnection       | integer       |               | [See Update or Resolve Issues on an Existing Connection](#update-or-resolve-issues-on-an-existing-connection) |
| singleSync  | boolean | 0 |   If true, the back arrow on the top left will be removed and “Add Another” button will be hidden during the syncing workflow and the playback steps. This parameter is optional and will default to false. |
| hideTray  | integer (bit) | 0 |   If on, the tray showing notifications will be hidden|
| syncType             | String       |                | Choose what type of connection syncs are performed within Connect. Possible values are `agg`, `auth`, `aggBoth` or `authBoth`.  This parameter is optional and will default to agg.  Connect has specific screen flows that are configured for agg vs auth sync types if using  `aggBoth` or `authBoth` you will need to define which is the primary workflow for your users as it will simultaneously run an agg AND auth sync on new connections.  More information on integrating account verification with Connect can be found here. (https://api.quovo.com/docs/v3/ui/#auth)|
| headerText           | string|              | Choose the global header text. This parameter is optional and will default to Connect Accounts.|
| showManualAccounts   | integer (bit)|  0    | Choose whether the "Enter Manually" displays at bottom of landing page & search results. If False, this section will be hidden. This parameter is optional and will default to True.|
| confirmClose         | integer (bit)|  1    | Defaults to `true`, setting to `false` will hide the prompt asking the user to confirm that they’d like to close the Connect Widget will be presented when the "close" icon is clicked.|

<br>
<br>
<br>
On Enter Credentials screen, there is messaging below the password field that can be configured using the fields below:
<br>
<br>
<br>

| Field                | Type          | Default       | Description |
| -------------------- | ------------- | ------------- | ----------- |
| learnMoreIsHidden    | integer (bit) |    0          | Defaults to `false`, setting to `true` will hide the "We use bank-level encryption to keep your data secure. Learn More"|
| learnMoreInfoMessage           | string|           | Configure text for "We use bank-level encryption to keep your data secure. Learn More". This parameter is optional and will default to text above.|
| learnMoreText           | string|              | Configure text for "Learn More". This parameter is optional and will default to text above.|
| learnMoreUrl           | string|              | By default this is set to `false` and when clicking the text "Learn More", you will be re-directed to https://www.quovo.com/infosec/. Configure by entering url into this parameter.|

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
