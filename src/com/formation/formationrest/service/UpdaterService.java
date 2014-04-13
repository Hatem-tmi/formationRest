package com.formation.formationrest.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.formation.formationrest.R;
import com.formation.formationrest.activity.MainActivity;
import com.formation.formationrest.data.User;
import com.formation.formationrest.util.Utils;

public class UpdaterService extends Service {
	private static final String TAG = UpdaterService.class.getSimpleName();

	private static final String WS_URL = "http://10.0.2.2/projects/webservice-project/users.php";
	private static final int CONNECTION_TIMEOUT = 5000;
	private static final int DELAY = 60000; // 1 minute
	private static int nbreNotification = 0;

	private boolean runFlag = false;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.d(TAG, "onCreated");
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);
		Log.d(TAG, "onStarted");

		this.runFlag = true;
		loopInvokeWs();

		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d(TAG, "onDestroyed");

		this.runFlag = false;
	}

	private void loopInvokeWs() {

		if (this.runFlag) {

			new Handler().postDelayed(new Runnable() {

				@Override
				public void run() {
					Log.d(TAG, "loopInvokeWs is running");

					if (!getPackageName().equals(
							Utils.getForegroundApp(getApplicationContext()))) {

						int nbreUsers = countUsers();
						sendNotification(nbreUsers);
					}

					// loop
					loopInvokeWs();
				}
			}, DELAY);
		}

	}

	private int countUsers() {
		List<User> result = new ArrayList<User>();

		try {
			// Invoke ws to get data
			HttpParams httpParams = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParams,
					CONNECTION_TIMEOUT);
			HttpConnectionParams.setSoTimeout(httpParams, CONNECTION_TIMEOUT);

			// Instantiate an HttpClient
			HttpClient httpClient = new DefaultHttpClient(httpParams);
			HttpGet httpGet = new HttpGet(WS_URL);
			HttpResponse httpResponse = httpClient.execute(httpGet);

			// Get string data from response
			BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(httpResponse.getEntity().getContent()));
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = bufferedReader.readLine()) != null)
				sb.append(line);

			Log.d(TAG, "Result= " + sb.toString());

			// Parse received json data
			JSONObject jsonResult = new JSONObject(sb.toString());
			JSONArray usersArray = (JSONArray) jsonResult.get("users");
			for (int i = 0; i < usersArray.length(); i++) {
				JSONObject userObject = (JSONObject) usersArray.get(i);

				User user = new User();
				user.setId(Integer.parseInt(userObject.getString("id")));
				user.setName(userObject.getString("name"));
				user.setLogin(userObject.getString("login"));
				user.setPwd(userObject.getString("password"));
				user.setAge(Integer.parseInt(userObject.getString("age")));

				result.add(user);
			}
		} catch (MalformedURLException e) {
			Log.e(TAG, "", e);
		} catch (IOException e) {
			Log.e(TAG, "", e);
		} catch (JSONException e) {
			Log.e(TAG, "", e);
		} catch (Exception e) {
			Log.e(TAG, "", e);
		}

		return result.size();
	}

	/**
	 * Creates a notification in the notification bar
	 * 
	 * @param usersCount
	 */
	private void sendNotification(int usersCount) {
		Log.d(TAG, "send Notification");

		// Notification with a sound and a flashing light
		NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

		int icon = R.drawable.ic_launcher; // icon from resources
		long when = System.currentTimeMillis(); // notification time
		CharSequence contentTitle = getResources().getString(R.string.app_name); // title
		CharSequence contentText = usersCount + " Users in back-office";// message

		Intent notificationIntent = new Intent(getApplicationContext(),
				MainActivity.class);

		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				notificationIntent, 0);

		// the next two lines initialize the Notification, using the
		// configurations above
		Notification notification = new Notification(icon, contentText, when);
		notification.setLatestEventInfo(getApplicationContext(), contentTitle,
				contentText, contentIntent);

		notification.defaults |= Notification.DEFAULT_SOUND;
		notification.defaults |= Notification.DEFAULT_LIGHTS;
		notification.flags |= Notification.FLAG_AUTO_CANCEL;

		// Pass the Notification to the NotificationManager with the id=1
		nm.notify(1, notification);
	}
}
