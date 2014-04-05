package com.formation.formationrest.activity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONObject;

import android.app.Activity;
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

public class CreateUserActivity extends Activity
{
	private static final String TAG = CreateUserActivity.class.getSimpleName();

	private static final String WS_URL = "http://10.0.2.2/projects/webservice-project/create_user.php";
	private static final int CONNECTION_TIMEOUT = 5000;

	private EditText nameEditText;
	private EditText ageEditText;
	private EditText loginEditText;
	private EditText passwordEditText;
	private Button submitButton;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.create_user);

		nameEditText = (EditText) findViewById(R.id.nameEditText);
		ageEditText = (EditText) findViewById(R.id.ageEditText);
		loginEditText = (EditText) findViewById(R.id.loginEditText);
		passwordEditText = (EditText) findViewById(R.id.passwordEditText);
		submitButton = (Button) findViewById(R.id.submitButton);

		submitButton.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				String name = nameEditText.getText().toString();
				String login = loginEditText.getText().toString();
				String password = loginEditText.getText().toString();

				int age = 0;
				try
				{
					age = Integer.parseInt(ageEditText.getText().toString());
				}
				catch (NumberFormatException e)
				{
				}

				if (name.length() > 0 && age > 0 && login.length() > 0 && password.length() > 0)
				{
					User user = new User();
					user.setName(name);
					user.setAge(age);
					user.setLogin(login);
					user.setPwd(password);

					// post infos user
					new CreateUserAsynck().execute(user);
				}
				else
				{
					Toast.makeText(CreateUserActivity.this, "Erreur lors de la cr�ation d'un utilisateur",
							Toast.LENGTH_LONG).show();
				}
			}
		});
	}

	/**
	 * Create User AsynckTask
	 * 
	 */
	private class CreateUserAsynck extends AsyncTask<User, Void, Boolean>
	{

		@Override
		protected void onPreExecute()
		{
		}

		@Override
		protected Boolean doInBackground(User... params)
		{
			boolean success = false;
			if (params.length == 0 || params[0] == null)
				return false;

			List<NameValuePair> nvps = new ArrayList<NameValuePair>();
			nvps.add(new BasicNameValuePair("name", params[0].getName()));
			nvps.add(new BasicNameValuePair("age", "" + params[0].getAge()));
			nvps.add(new BasicNameValuePair("login", params[0].getLogin()));
			nvps.add(new BasicNameValuePair("password", params[0].getPwd()));

			try
			{
				// Invoke ws to get data
				HttpParams httpParams = new BasicHttpParams();
				HttpConnectionParams.setConnectionTimeout(httpParams, CONNECTION_TIMEOUT);
				HttpConnectionParams.setSoTimeout(httpParams, CONNECTION_TIMEOUT);

				// Instantiate an HttpClient
				HttpClient httpClient = new DefaultHttpClient(httpParams);
				HttpPost httpPost = new HttpPost(WS_URL);
				httpPost.setEntity(new UrlEncodedFormEntity(nvps));
				HttpResponse httpResponse = httpClient.execute(httpPost);
				HttpEntity entity = httpResponse.getEntity();

				// Get string data from response
				BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpResponse.getEntity()
						.getContent()));
				StringBuilder sb = new StringBuilder();
				String line = null;
				while ((line = bufferedReader.readLine()) != null)
					sb.append(line);

				Log.d(TAG, "Result= " + sb.toString());

				// Parse received json data
				JSONObject jsonResult = new JSONObject(sb.toString());
				success = (Boolean) jsonResult.get("success");
			}
			catch (UnsupportedEncodingException e)
			{
				Log.e(TAG, "", e);
			}
			catch (ClientProtocolException e)
			{
				Log.e(TAG, "", e);
			}
			catch (IOException e)
			{
				Log.e(TAG, "", e);
			}
			catch (Exception e)
			{
				Log.e(TAG, "", e);
			}

			return success;
		}

		@Override
		protected void onPostExecute(Boolean result)
		{
			if (result == false)
			{
				Toast.makeText(CreateUserActivity.this, "Erreur d'ajout d'un utilisateur: ", Toast.LENGTH_LONG).show();
			}
			else
			{
				finish();
			}
		}
	}
}
