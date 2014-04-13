package com.formation.formationrest.activity;

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

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.formation.formationrest.R;
import com.formation.formationrest.adapter.UsersAdapter;
import com.formation.formationrest.data.User;
import com.formation.formationrest.service.UpdaterService;
import com.formation.formationrest.tasks.DeleteUserTask;
import com.formation.formationrest.tasks.DeleteUserTask.DeleteUserCallback;

public class UsersActivity extends Activity implements OnItemClickListener,
		OnClickListener, OnItemLongClickListener, DeleteUserCallback {
	private static final String TAG = UsersActivity.class.getSimpleName();

	private static final String WS_URL = "http://10.0.2.2/projects/webservice-project/users.php";
	private static final int CONNECTION_TIMEOUT = 5000;

	private ListView listView;
	private ProgressBar progressBar;
	private Button addUserButton;
	private Button backButton;
	private Button refreshButton;

	private ProgressDialog progressDialog;

	private UsersAdapter adapter;
	private List<User> data = new ArrayList<User>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.users_layout);

		listView = (ListView) findViewById(R.id.list_view);
		progressBar = (ProgressBar) findViewById(R.id.progressBar);
		addUserButton = (Button) findViewById(R.id.addUserButton);
		backButton = (Button) findViewById(R.id.backButton);
		refreshButton = (Button) findViewById(R.id.refreshButton);

		listView.setOnItemClickListener(this);
		listView.setOnItemLongClickListener(this);
		addUserButton.setOnClickListener(this);
		backButton.setOnClickListener(this);
		refreshButton.setOnClickListener(this);

		// Create Adapter to adapt data on listView
		adapter = new UsersAdapter(getApplicationContext(), data);
		listView.setAdapter(adapter);
	}

	@Override
	protected void onResume() {
		super.onResume();

		// fetch data
		new FetchDataAsynck().execute(WS_URL);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.startService:
			// Start UpdaterService
			startService(new Intent(getApplicationContext(),
					UpdaterService.class));
			break;
		case R.id.stopService:
			// Stop UpdaterService
			stopService(new Intent(getApplicationContext(),
					UpdaterService.class));
			break;
		}
		return true;
	}

	/**
	 * Add User to data and refresh listview
	 */
	private void addUser() {
		// User user = new User();
		// user.setId(data.size());
		// user.setName("Name-" + data.size());
		// user.setLogin("Login-" + data.size());
		// user.setAge(20);
		//
		// // add user and refresh listview
		// data.add(0, user);
		// adapter.notifyDataSetChanged();

		startActivity(new Intent(getApplicationContext(),
				CreateUserActivity.class));
	}

	/**
	 * Remove User from data and refresh listview
	 * 
	 * @param user
	 */
	private void removeUser(User user) {
		data.remove(user);
		adapter.notifyDataSetChanged();
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
								.getId(), UsersActivity.this).start();
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

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Toast.makeText(this,
				String.format("User item at position %s is clicked", position),
				Toast.LENGTH_SHORT).show();

		Intent intent = new Intent(getApplicationContext(), DetailsUser.class);
		intent.putExtra("idUser", data.get(position).getId());
		startActivity(intent);
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
		showRemoveConfirmationDialog(data.get(position));
		return true;
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.addUserButton:
			addUser();
			break;
		case R.id.backButton:
			finish();
		case R.id.refreshButton:
			// fetch data
			new FetchDataAsynck().execute(WS_URL);
			break;
		default:
			break;
		}
	}

	/**
	 * Fetch data AsynckTask
	 * 
	 */
	private class FetchDataAsynck extends AsyncTask<String, Void, List<User>> {

		@Override
		protected void onPreExecute() {
			progressBar.setVisibility(View.VISIBLE);
			listView.setVisibility(View.GONE);
		}

		@Override
		protected List<User> doInBackground(String... params) {
			List<User> result = new ArrayList<User>();

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

			return result;
		}

		@Override
		protected void onPostExecute(List<User> result) {
			progressBar.setVisibility(View.GONE);
			listView.setVisibility(View.VISIBLE);

			data.clear();
			data.addAll(result);
			adapter.notifyDataSetChanged();
		}
	}

	@Override
	public void onSuccessDeletingUser() {
		Log.d(TAG, "onSuccessDeletingUser");
		dismissLoadingProgressDialog();

		Toast.makeText(this, "Success deleting user", Toast.LENGTH_SHORT)
				.show();
		adapter.notifyDataSetChanged();
	}

	@Override
	public void onFailedDeletingUser() {
		Log.d(TAG, "onFailedDeletingUser");
		dismissLoadingProgressDialog();

		Toast.makeText(this, "Failure deleting user", Toast.LENGTH_SHORT)
				.show();

	}
}
