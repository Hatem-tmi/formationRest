package com.formation.formationrest.activity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;

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

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.formation.formationrest.R;
import com.formation.formationrest.data.User;
import com.formation.formationrest.tasks.DeleteUserTask;
import com.formation.formationrest.tasks.DeleteUserTask.DeleteUserCallback;

public class DetailsUser extends Activity implements OnClickListener,
		DeleteUserCallback {
	private static final String TAG = "DetailsUser";

	private static final String WS_URL = "http://10.0.2.2/projects/webservice-project/users.php";
	private static final int CONNECTION_TIMEOUT = 5000;

	private TextView userName;
	private TextView userLogin;
	private TextView userAge;
	private Button updateButton;
	private Button deleteButton;

	private ProgressDialog progressDialog;

	private User user;
	private int idUser = -1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.userdetails);

		userName = (TextView) findViewById(R.id.userName);
		userLogin = (TextView) findViewById(R.id.userLogin);
		userAge = (TextView) findViewById(R.id.userAge);
		updateButton = (Button) findViewById(R.id.updateButton);
		deleteButton = (Button) findViewById(R.id.deleteButton);

		updateButton.setOnClickListener(this);
		deleteButton.setOnClickListener(this);

		if (getIntent() != null && getIntent().hasExtra("idUser")) {
			idUser = getIntent().getIntExtra("idUser", -1);

			// Show Log of id of user
			Log.d(TAG, "idUser: " + idUser);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		new FetchWsUser().execute(WS_URL + "?id=" + idUser);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.updateButton:
			break;
		case R.id.deleteButton:
			if (user != null)
				showRemoveConfirmationDialog(user);
			break;
		default:
			break;
		}
	}

	/**
	 * Show Remove Dialog
	 * 
	 * @param user
	 */
	private void showRemoveConfirmationDialog(final User user) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder.setTitle("Suppression d'un Utilisateur")
				.setMessage("Voulez-vous vraiment supprimer?")
				.setCancelable(false);

		alertDialogBuilder.setPositiveButton("Oui",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {

						showLoadingProgressDialog();

						// invoke delete ws
						new DeleteUserTask(getApplicationContext(), user
								.getId(), DetailsUser.this).start();
					}
				});

		alertDialogBuilder.setNegativeButton("Non", null);

		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
	}

	/**
	 * Shows loading progress dialog
	 * 
	 * @param activity
	 */
	public void showLoadingProgressDialog() {
		progressDialog = new ProgressDialog(this);
		progressDialog.setTitle("Loading");
		progressDialog.setMessage("Veuillez patienter...");

		progressDialog.show();
	}

	/**
	 * Dismiss Loading Dialog
	 * 
	 * @param activity
	 */
	public void dismissLoadingProgressDialog() {
		if (progressDialog != null)
			progressDialog.dismiss();
	}

	/**
	 * Fetch data AsynckTask
	 * 
	 */
	private class FetchWsUser extends AsyncTask<String, Void, User> {

		@Override
		protected void onPreExecute() {
			showLoadingProgressDialog();
		}

		@Override
		protected User doInBackground(String... params) {
			User result = null;

			try {
				// Invoke ws to get data
				HttpParams httpParams = new BasicHttpParams();
				HttpConnectionParams.setConnectionTimeout(httpParams,
						CONNECTION_TIMEOUT);
				HttpConnectionParams.setSoTimeout(httpParams,
						CONNECTION_TIMEOUT);

				// Instantiate an HttpClient
				HttpClient httpClient = new DefaultHttpClient(httpParams);
				HttpGet httpGet = new HttpGet(params[0]);
				HttpResponse httpResponse = httpClient.execute(httpGet);

				// Get string data from response
				BufferedReader bufferedReader = new BufferedReader(
						new InputStreamReader(httpResponse.getEntity()
								.getContent()));
				StringBuilder sb = new StringBuilder();
				String line = null;
				while ((line = bufferedReader.readLine()) != null)
					sb.append(line);

				Log.d(TAG, "Result= " + sb.toString());

				// Parse received json data
				JSONObject jsonResult = new JSONObject(sb.toString());
				JSONArray usersArray = (JSONArray) jsonResult.get("users");

				JSONObject userObject = (JSONObject) usersArray.get(0);

				// Get user
				result = new User();
				result.setId(Integer.parseInt(userObject.getString("id")));
				result.setName(userObject.getString("name"));
				result.setLogin(userObject.getString("login"));
				result.setPwd(userObject.getString("password"));
				result.setAge(Integer.parseInt(userObject.getString("age")));

			} catch (MalformedURLException e) {
				Log.e(TAG, "", e);
			} catch (IOException e) {
				Log.e(TAG, "", e);
			} catch (JSONException e) {
				Log.e(TAG, "", e);
			} catch (Exception e) {
				Log.e(TAG, "", e);
			}

			return result;
		}

		@Override
		protected void onPostExecute(User result) {
			dismissLoadingProgressDialog();

			if (result == null)
				return;

			user = result;

			// set data on view
			userName.setText(user.getName());
			userLogin.setText(user.getLogin());
			userAge.setText("" + user.getAge());
		}
	}

	@Override
	public void onSuccessDeletingUser() {
		Log.d(TAG, "onSuccessDeletingUser");
		dismissLoadingProgressDialog();

		Toast.makeText(this, "Success deleting user", Toast.LENGTH_SHORT)
				.show();
		finish();
	}

	@Override
	public void onFailedDeletingUser() {
		Log.d(TAG, "onFailedDeletingUser");
		dismissLoadingProgressDialog();

		Toast.makeText(this, "Failure deleting user", Toast.LENGTH_SHORT)
				.show();

	}
}
