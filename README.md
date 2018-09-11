# Quovo-AndroidSDK
## Installation

### Manual Installation
Prerequisite: This library requires a `minSdkVersion 21` in your project-level Gradle file
1. Open your project in Android Studio.
2. Add QuovoConnectSDK as a dependency into your project

   `implementation 'com.quovo.connect:connect-android:1.1.0'`
3. Build the project


## Initialize the SDK

```Android
import com.quovo.sdk.QuovoConnectSdk;
QuovoConnectSdk.Builder quovoConnectSdk = new QuovoConnectSdk.Builder(this); //this is context
```
A good place to initialize the SDK is upon app launch or in the launch method of a view.


## Create a Completion Handler

```Android
import com.quovo.sdk.listeners.OnCompleteListener
quovoConnectSdk.setOnCompleteListener(new OnCompleteListener() {
          @Override
          public void onComplete(String callback, String response) {
              Log.d("callback", callback);
              Log.d("response", response);
          }
       });

```
The completion handler will allow your app to listen for events that will be fired by the QuovoConnectSDK.  The handler has 2 parameters: a "callback" method name and an optional "response" payload. The "callback" string will be one of the following:

* open
* load
* close
* cancel
* add
* sync

In the case of "add" and "sync" a response payload of type NSDictionary will be returned.

Here are some examples:

"Add" event fired:

```Android
[
"connection": [
"id": 2135634,
"institution": 34,
"user": 1123,
],
"timeStamp": 1496879583157,
]
```

"Sync" event fired
```Android
[
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
]
```

The other callbacks will yield an empty response. For more information on these events, please see:

(https://api.quovo.com/docs/connect/#custom-integrations)

## Launch the SDK

Launching the QuovoConnectSDK will instantiate a WebView experience that allows users to sync and manage their accounts. The minimum required parameter for launching the WebView is an Iframe Token.  This token must be generated via the API and will expire after its first use.

```Android
quovoConnectSdk.launch(userToken);
```

## Customization

You can optionally pass in a set of parameters that control the appearance and functionality of the WebView experience.  An example of this is:

```Android
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
| syncType             | String       |                | Choose what type of connection syncs are performed within Connect. Possible values are agg, auth, or both, which will simultaneously run an agg AND auth sync on new connections. This parameter is optional and will default to agg. More information on integrating account verification with Connect can be found here. (https://api.quovo.com/docs/v3/ui/#auth)
|

## Preselect an Institution

You may want to direct users to add Accounts onto specific institutions. With Connect, you can preselect an institution for users and bypass the search page entirely.

Pass the desired Quovo Brokerage ID as the value.

```Android
HashMap<String, Object> options = new HashMap<>();
// Connect will bypass the search page and open directly to the page to
// add a "Fidelity NetBenefits" Account (which has a Brokerage ID of 23).
options.put("openInstitution", 23);
quovoConnectSdk.launch(userToken, options);
```

## Update or Resolve Issues on an Existing Connection

You may want users to update or resolve issues on existing connections. They may need to supply additional MFA answers or update recently changed login credentials. With Connect, you can simply pass an Account ID to direct users to fix these issues, allowing their Accounts to continue syncing. Connections with a "login" status will be taken to a screen where users can update their credentials, while connections with a "questions" status will be taken to a screen where users are prompted to answer additional MFA questions.

If both `openConnection` and `openInstitution` arguments are supplied to `launch`, the `openConnection` workflow will take priority.

```Android
HashMap<String, Object> options = new HashMap<>();
// Account 813981 has a status of "questions", so Connect will open to a
// page where the user can answer any outstanding MFA questions and resync
// the Account accordingly.
options.put("openConnection", 813981);
quovoConnectSdk.launch(userToken, options);
```

## Custom Navbar Title

You also have the option to customize the navbar title for the QuovoConnect WebView:

```Android
quovoConnectSdk.customTitle("Connect your accounts");
```
