package com.formation.formationrest.activity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.formation.formationrest.R;
import com.formation.formationrest.data.User;
import com.formation.formationrest.util.Utils;

public class MainActivity extends Activity {
	private static final String TAG = "MainActivity";
	private static final String WS_URL = "http://10.0.2.2/projects/webservice-project/auth.php";
	private static final int CONNECTION_TIMEOUT = 5000;

	private EditText loginEditText;
	private EditText passwordEditText;
	private Button okButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		loginEditText = (EditText) findViewById(R.id.loginEditText);
		passwordEditText = (EditText) findViewById(R.id.passwordEditText);
		okButton = (Button) findViewById(R.id.okButton);

		okButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String login = loginEditText.getText().toString();
				String password = passwordEditText.getText().toString();

				if (login.length() > 0 && password.length() > 0) {

					// login
					new LoginAsynck().execute(WS_URL, login, password);
				} else {
					Toast.makeText(getApplicationContext(),
							"Formulaire invalide", Toast.LENGTH_LONG).show();
				}
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();

		// check if user is logged-in from Shared-Preferences
		if (Utils.isUserLoggedIn(getApplicationContext())) {
			Intent intent = new Intent(MainActivity.this, UsersActivity.class);
			startActivity(intent);

			finish();
		}
	}

	/**
	 * Login AsynckTask
	 * 
	 */
	private class LoginAsynck extends AsyncTask<String, Void, User> {

		@Override
		protected User doInBackground(String... params) {
			List<NameValuePair> nvps = new ArrayList<NameValuePair>();
			nvps.add(new BasicNameValuePair("login", params[1]));
			nvps.add(new BasicNameValuePair("password", params[2]));

			try {
				// Invoke ws to get data
				HttpParams httpParams = new BasicHttpParams();
				HttpConnectionParams.setConnectionTimeout(httpParams,
						CONNECTION_TIMEOUT);
				HttpConnectionParams.setSoTimeout(httpParams,
						CONNECTION_TIMEOUT);

				// Instantiate an HttpClient
				HttpClient httpClient = new DefaultHttpClient(httpParams);
				HttpPost httpPost = new HttpPost(params[0]);
				httpPost.setEntity(new UrlEncodedFormEntity(nvps));
				HttpResponse httpResponse = httpClient.execute(httpPost);

				// Get string data from response
				BufferedReader bufferedReader = new BufferedReader(
						new InputStreamReader(httpResponse.getEntity()
								.getContent()));
				StringBuilder sb = new StringBuilder();
				String line = null;
				while ((line = bufferedReader.readLine()) != null)
					sb.append(line);

				Log.d(TAG, sb.toString());

				// Parse received json data
				JSONObject jsonResult = new JSONObject(sb.toString());
				boolean success = jsonResult.getBoolean("success");

				if (success) {
					JSONObject userObject = jsonResult.getJSONObject("user");

					User user = new User();
					user.setId(Integer.parseInt(userObject.getString("id")));
					user.setName(userObject.getString("name"));
					user.setAge(Integer.parseInt(userObject.getString("age")));
					user.setLogin(jsonResult.getString("login"));
					user.setPwd(jsonResult.getString("password"));

					return user;
				}

			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(User result) {

			if (result != null) {
				// save user values in Shared-Preferences
				Utils.setUserLoggedIn(getApplicationContext(), true);
				Utils.setUserLoginValue(getApplicationContext(),
						result.getLogin());

				Intent intent = new Intent(MainActivity.this,
						UsersActivity.class);
				startActivity(intent);

				finish();
			} else {
				Toast.makeText(MainActivity.this, "Erreur d'authentification",
						Toast.LENGTH_LONG).show();
			}
		}
	}
}
