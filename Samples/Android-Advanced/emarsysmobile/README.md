## Overall
The Emarsys Mobile CRM SDK allows developers with a simple interface between their mobile app and the Emarsys Mobile Engage solution.

The Emarsys Mobile SDK is designed to work with the PushWoosh push SDK which can be found here: https://github.com/Pushwoosh/pushwoosh-android-sdk

The SDK allows a developer to:

1.  Track App opens with Mobile Engage
2.  Update an Emarsys suite contact with any data available to mobile app
3.  Register a mobile push token with Mobile Engage
4.  Track app login and logout status
5.  Track any event in the mobile app
6.  Track message opens

## Code Examples
Here are some examples of how use the Emarsys SDK along side with PushWoosh Push SDK

### Import and configure the SDK
Import the Emarsys Package in your App
```
import com.example.emarsysmobile.EmarsysMobile;
```
configure the AndroidManifest.xml file with your PushWoosh Mobile AppID and Emarsys Mobile Engage API Token

```
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="com.example.emarsysmobile">

    <application
        android:allowBackup="true"
        android:label="@string/app_name"
        android:supportsRtl="true">

        <meta-data android:name="auth_token" android:value="[Your AuthToken]" />
        <meta-data android:name="app_id" android:value="[Your App ID]" />
    </application>

</manifest>
```

###  Track App install, opens and session times
Track App iactivity by placing the following code in your onResume() in MainActivity.jaava
```
	@Override
	public void onResume()
	{
   ..
   new EmarsysMobile(MainActivity.this).app_launch(MainActivity.this);
   ...
   
```
### Track Mobile User Events
Track any important event in your app with the event(). 
```
somethingImporant.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				JSONObject eventdata = new JSONObject();

				List<Pair<String, String>> params = new ArrayList<>();
				try {
					eventdata.put("currency","EUR");
					eventdata.put("amount", "50.00");
					eventdata.put("recipient_id", "WR187298216");
				} catch (JSONException e) {
					e.printStackTrace();
				}
				new EmarsysMobile(MainActivity.this).event("CheckoutSuccessful", eventdata);
			}
		});
...
```
###  Update or Create an Emarsys contact
Create a new contact or update an existing contact with using the update_contact() method.  Pass the Emarsys field which should be used for merging (this typically email or field 3 and the array of values to updated.  Any suite contact field can be updated.

```
List<Pair<String, String>> params = new ArrayList<>();
				try {
					contactdata.put("1", mfirstname.getText().toString()); //Update First Name
					contactdata.put("2", mlastname.getText().toString());  //Update Last Name
					contactdata.put("3", memail.getText().toString());     //Update email address
					contactdata.put("11790212", carrierName);     		     //Update current network provider

				} catch (JSONException e) {
					e.printStackTrace();
				}
				new EmarsysMobile(MainActivity.this).contact_update( 3, contactdata );
...
```

### Store Mobile Push token -- Required for batch or automated messages 
Once the user has accepted push messages call the following function to store the push token in the Emarsys Mobile Engage platform.  This example passed the push token aquired by the PushWoosh PushManager object.

```
	new EmarsysMobile(MainActivity.this).acceptPush(MainActivity.this, PushManager.getPushToken(MainActivity.this).toString());
```	

### Track push message Opens
Track message open rates by placing the follwing code in the app doOnMessageReceive() function in MainActivity.java
```
	public void doOnMessageReceive(String message)
	{
		try
		{

			JSONObject messageJson = new JSONObject(message);
			JSONObject customJson = new JSONObject(messageJson.getString("u"));
			
      //Emarsys will send the campaign ID as the sid field
			new EmarsysMobile(MainActivity.this).message_open(customJson.getString("sid"));
..
```

### Track app login and logout events
```
			new EmarsysMobile(MainActivity.this).logout();
```



## License

