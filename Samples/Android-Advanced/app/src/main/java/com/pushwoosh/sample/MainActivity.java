package com.pushwoosh.sample;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
//import android.widget.AdapterView.
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;
import android.widget.Toast;
import org.json.JSONObject;
import android.util.Pair;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;

import java.io.*;
import java.net.*;
import java.util.*;
import android.util.Base64;


import org.json.JSONException;
import org.json.JSONObject;

import com.example.emarsysmobile.EmarsysMobile;


import com.pushwoosh.PushManager;
import com.pushwoosh.PushManager.RichPageListener;
import com.pushwoosh.BasePushMessageReceiver;
import com.pushwoosh.BaseRegistrationReceiver;
import com.pushwoosh.inapp.InAppFacade;
import com.pushwoosh.sample.ListItems;


public class MainActivity extends Activity
{
	private TextView mGeneralStatus;
	private EditText mfirstname;
	private EditText mlastname;
	private EditText memail;

	private Button mLoginButton;
	private Button mLogoutButton;
	private Button mAcceptPushButton;
	private Button mSendAddCart;
	private Button mCheckPoint;

	private InAppFacade Event;
	private String android_id;
	private ListView mCatalog;

	static final String[] Items = new String[] { "Apple", "Avocado", "Banana",
			"Blueberry", "Coconut", "Durian", "Guava", "Kiwifruit",
			"Jackfruit", "Mango", "Olive", "Pear", "Sugar-apple" };

	/**
	 * Called when the activity is first created.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.main);

		android_id = Secure.getString(this.getContentResolver(), Secure.ANDROID_ID);

		mGeneralStatus = (TextView) findViewById(R.id.general_status);
		mfirstname = (EditText) findViewById(R.id.fname);
		mlastname = (EditText) findViewById(R.id.lname);
		memail = (EditText) findViewById(R.id.email);

		mLoginButton = (Button) findViewById(R.id.login);
		mLogoutButton = (Button) findViewById(R.id.logout);
		mAcceptPushButton = (Button) findViewById(R.id.acceptPush);
		mSendAddCart = (Button) findViewById(R.id.AddtoCart);
		mCheckPoint = (Button) findViewById(R.id.checkpoint);

		mSendAddCart.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(MainActivity.this, "AddtoCart Sent", Toast.LENGTH_SHORT).show();
				Event.postEvent(MainActivity.this, "AddtoCart", new HashMap<String, Object>());

				new EmarsysMobile(MainActivity.this).event("AddtoCart", null);
			}
		});

		mLoginButton.setOnClickListener(new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			Toast.makeText(MainActivity.this, "LoginEvent Sent", Toast.LENGTH_SHORT).show();

			JSONObject contactdata =  new JSONObject();

			// Get System TELEPHONY service reference
			TelephonyManager tManager = (TelephonyManager) getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);

			// Get carrier name (Network Operator Name)
			String carrierName = tManager.getNetworkOperatorName();

			if (  (mfirstname.getText() != null) &&  (mlastname.getText() != null)  && (memail.getText() != null)  ){

				List<Pair<String, String>> params = new ArrayList<>();
				try {
					contactdata.put("1", mfirstname.getText().toString()); //Update First Name
					contactdata.put("2", mlastname.getText().toString());  //Update Last Name
					contactdata.put("3", memail.getText().toString());     //Update email address
					//contactdata.put("11790212", carrierName);     		   //Update current network provider

				} catch (JSONException e) {
					e.printStackTrace();
				}

				mfirstname.setFocusable(false);
				mlastname.setFocusable(false);
				memail.setFocusable(false);

				new EmarsysMobile(MainActivity.this).contact_update( 3, contactdata );

			} else {

				Toast.makeText(MainActivity.this, "First and Last name required!", Toast.LENGTH_SHORT).show();

			}


		}
	    });

		mLogoutButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(MainActivity.this, "LogoutEvent Sent", Toast.LENGTH_SHORT).show();

					mfirstname.setFocusable(true);
					mfirstname.setClickable(true);

					mlastname.setFocusable(true);
					mlastname.setClickable(true);

					memail.setFocusable(true);
					memail.setClickable(true);

					new EmarsysMobile(MainActivity.this).logout();
			}
		});


		mAcceptPushButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(MainActivity.this, "AcceptPush Sent", Toast.LENGTH_SHORT).show();
				new EmarsysMobile(MainActivity.this).acceptPush(MainActivity.this, PushManager.getPushToken(MainActivity.this).toString());

			}
		});

		mCheckPoint.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(MainActivity.this, "CheckoutSuccessful", Toast.LENGTH_SHORT).show();

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

		initPushwoosh();
		// Call the constructor -- calls install
		//new EmarsysMobile(MainActivity.this).execute();

		//EmarsysMobile app = new EmarsysMobile(this);
		//app.execute("g");

        Toast.makeText(MainActivity.this, "appOpen Sent", Toast.LENGTH_SHORT).show();
        Event.postEvent(MainActivity.this, "appOpen", new HashMap<String, Object>());

	}



	private void SendLogin() throws IOException {

		Log.d("Emarsys", "MobileEvent");

	}

	private void initPushwoosh()
	{
		//Register receivers for push notifications
		registerReceivers();

		final PushManager pushManager = PushManager.getInstance(this);

		class RichPageListenerImpl implements RichPageListener
		{
			@Override
			public void onRichPageAction(String actionParams)
			{
				Log.d("Pushwoosh", "Rich page action: " + actionParams);
			}

			@Override
			public void onRichPageClosed()
			{
				Log.d("Pushwoosh", "Rich page closed");
			}
		}

		pushManager.setRichPageListener(new RichPageListenerImpl());

		//pushManager.setNotificationFactory(new NotificationFactorySample());

		//Start push manager, this will count app open for Pushwoosh stats as well
		try
		{
			pushManager.onStartup(this);
		}
		catch (Exception e)
		{
			Log.e("Pushwoosh", e.getLocalizedMessage());
		}

		//Register for push!
		pushManager.registerForPushNotifications();

		//check launch notification (optional)
		String launchNotificatin = pushManager.getLaunchNotification();
		if (launchNotificatin != null)
		{
			Log.d("Pushwoosh", "Launch notification received: " + launchNotificatin);
		}
		else
		{
			Log.d("Pushwoosh", "No launch notification received");
		}

		//Check for start push notification in the Intent payload
		checkMessage(getIntent());

		//The commented code below shows how to use geo pushes
		//pushManager.startTrackingGeoPushes();

		//The commented code below shows how to use local notifications
		//PushManager.clearLocalNotifications(this);


		//easy way
		//PushManager.scheduleLocalNotification(this, "Your pumpkins are ready!", 30);

		//expert mode
		//Bundle extras = new Bundle();
		//extras.putString("b", "https://cp.pushwoosh.com/img/arello-logo.png");
		//extras.putString("u", "50");
		//PushManager.scheduleLocalNotification(this, "Your pumpkins are ready!", extras, 30);

		//Clear application badge number
		//pushManager.setBadgeNumber(0);

		// Start/stop geo pushes
//		mGeoPushButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//				if (isChecked) {
//					pushManager.startTrackingGeoPushes();
//				} else {
//					pushManager.stopTrackingGeoPushes();
//				}
//			}
//		});

		// Start/stop beacon pushes
//		mBeaconPushButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//				if (isChecked) {
//					pushManager.startTrackingBeaconPushes();
//				} else {
//					pushManager.stopTrackingBeaconPushes();
//
//				}
//			}
//		});
	}

	/**
	 * Called when the activity receives a new intent.
	 */
	public void onNewIntent(Intent intent)
	{
		super.onNewIntent(intent);

		//have to check if we've got new intent as a part of push notification
		checkMessage(intent);
	}

	//Registration receiver
	BroadcastReceiver mBroadcastReceiver = new BaseRegistrationReceiver()
	{
		@Override
		public void onRegisterActionReceive(Context context, Intent intent)
		{
			checkMessage(intent);
		}
	};

	//Push message receiver
	private BroadcastReceiver mReceiver = new BasePushMessageReceiver()
	{
		@Override
		protected void onMessageReceive(Intent intent)
		{
			//JSON_DATA_KEY contains JSON payload of push notification.
			doOnMessageReceive(intent.getExtras().getString(JSON_DATA_KEY));
		}
	};

	//Registration of the receivers
	public void registerReceivers()
	{
		IntentFilter intentFilter = new IntentFilter(getPackageName() + ".action.PUSH_MESSAGE_RECEIVE");
		registerReceiver(mReceiver, intentFilter, getPackageName() +".permission.C2D_MESSAGE", null);
		registerReceiver(mBroadcastReceiver, new IntentFilter(getPackageName() + "." + PushManager.REGISTER_BROAD_CAST_ACTION));
	}

	public void unregisterReceivers()
	{
		//Unregister receivers on pause
		try
		{
			unregisterReceiver(mReceiver);
		}
		catch (Exception e)
		{
			// pass.
		}

		try
		{
			unregisterReceiver(mBroadcastReceiver);
		}
		catch (Exception e)
		{
			//pass through
		}
	}

	@Override
	public void onResume()
	{

		Toast.makeText(MainActivity.this, "install Sent", Toast.LENGTH_SHORT).show();
		new EmarsysMobile(MainActivity.this).app_launch(MainActivity.this);

		super.onResume();

		//Re-register receivers on resume
		registerReceivers();
	}

	@Override
	public void onPause()
	{

        Toast.makeText(MainActivity.this, "close_app Sent", Toast.LENGTH_SHORT).show();
        Event.postEvent(MainActivity.this, "close_app", new HashMap<String, Object>());

		super.onPause();

		//Unregister receivers on pause
		unregisterReceivers();
	}

	/**
	 * Will check PushWoosh extras in this intent, and fire actual method
	 *
	 * @param intent activity intent
	 */
	private void checkMessage(Intent intent)
	{
		if (null != intent)
		{
			if (intent.hasExtra(PushManager.PUSH_RECEIVE_EVENT))
			{
				doOnMessageReceive(intent.getExtras().getString(PushManager.PUSH_RECEIVE_EVENT));
			}
			else if (intent.hasExtra(PushManager.REGISTER_EVENT))
			{
				doOnRegistered(intent.getExtras().getString(PushManager.REGISTER_EVENT));
			}
			else if (intent.hasExtra(PushManager.UNREGISTER_EVENT))
			{
				doOnUnregistered(intent.getExtras().getString(PushManager.UNREGISTER_EVENT));
			}
			else if (intent.hasExtra(PushManager.REGISTER_ERROR_EVENT))
			{
				doOnRegisteredError(intent.getExtras().getString(PushManager.REGISTER_ERROR_EVENT));
			}
			else if (intent.hasExtra(PushManager.UNREGISTER_ERROR_EVENT))
			{
				doOnUnregisteredError(intent.getExtras().getString(PushManager.UNREGISTER_ERROR_EVENT));
			}

			resetIntentValues();
		}
	}

	public void doOnRegistered(String registrationId)
	{
		mGeneralStatus.setText(getString(R.string.registered, registrationId));
	}

	public void doOnRegisteredError(String errorId)
	{
		mGeneralStatus.setText(getString(R.string.registered_error, errorId));
	}

	public void doOnUnregistered(String registrationId)
	{
		mGeneralStatus.setText(getString(R.string.unregistered, registrationId));
	}

	public void doOnUnregisteredError(String errorId)
	{
		mGeneralStatus.setText(getString(R.string.unregistered_error, errorId));
	}

	public void doOnMessageReceive(String message)
	{

		Log.d("Emarsys","in doOnMessageReceive");

		mGeneralStatus.setText(getString(R.string.on_message, message));

		// Parse custom JSON data string.
		// You can set background color with custom JSON data in the following format: { "r" : "10", "g" : "200", "b" : "100" }
		// Or open specific screen of the app with custom page ID (set ID in the { "id" : "2" } format)
		try
		{


			JSONObject messageJson = new JSONObject(message);
			JSONObject customJson = new JSONObject(messageJson.getString("u"));

			Log.d("Emarsys", "messageJson: " + messageJson.toString() );
			Log.d("Emarsys", "sid: " + customJson.toString());

			new EmarsysMobile(MainActivity.this).message_open(customJson.getString("sid"));

			if (customJson.has("r") && customJson.has("g") && customJson.has("b"))
			{
				int r = customJson.getInt("r");
				int g = customJson.getInt("g");
				int b = customJson.getInt("b");
				View rootView = getWindow().getDecorView().findViewById(android.R.id.content);
				rootView.setBackgroundColor(Color.rgb(r, g, b));
			}
			if (customJson.has("id"))
			{
				Intent intent = new Intent(this, SecondActivity.class);
				intent.putExtra(PushManager.PUSH_RECEIVE_EVENT, messageJson.toString());
				startActivity(intent);
			}
		}
		catch (JSONException e)
		{
			Log.d("Emarsys", "No Custom data!");
			new EmarsysMobile(MainActivity.this).message_open("null");
			// No custom JSON. Pass this exception
		}
	}

	/**
	 * Will check main Activity intent and if it contains any PushWoosh data, will clear it
	 */
	private void resetIntentValues()
	{
		Intent mainAppIntent = getIntent();

		if (mainAppIntent.hasExtra(PushManager.PUSH_RECEIVE_EVENT))
		{
			mainAppIntent.removeExtra(PushManager.PUSH_RECEIVE_EVENT);
		}
		else if (mainAppIntent.hasExtra(PushManager.REGISTER_EVENT))
		{
			mainAppIntent.removeExtra(PushManager.REGISTER_EVENT);
		}
		else if (mainAppIntent.hasExtra(PushManager.UNREGISTER_EVENT))
		{
			mainAppIntent.removeExtra(PushManager.UNREGISTER_EVENT);
		}
		else if (mainAppIntent.hasExtra(PushManager.REGISTER_ERROR_EVENT))
		{
			mainAppIntent.removeExtra(PushManager.REGISTER_ERROR_EVENT);
		}
		else if (mainAppIntent.hasExtra(PushManager.UNREGISTER_ERROR_EVENT))
		{
			mainAppIntent.removeExtra(PushManager.UNREGISTER_ERROR_EVENT);
		}

		setIntent(mainAppIntent);
	}
}
