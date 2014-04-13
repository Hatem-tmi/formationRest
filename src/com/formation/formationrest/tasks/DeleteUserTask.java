package com.formation.formationrest.tasks;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONObject;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

public class DeleteUserTask extends Thread {

	private static final String TAG = "DeleteUserTask";
	private static final String WS_URL = "http://10.0.2.2/projects/webservice-project/delete_user.php";
	private static final int CONNECTION_TIMEOUT = 5000;

	private Context context;
	private int id;
	private DeleteUserCallback callback;

	public DeleteUserTask(Context context, int id, DeleteUserCallback callback) {
		this.context = context;
		this.id = id;
		this.callback = callback;
	}

	@Override
	public void run() {
		boolean success = false;

		try {
			// Invoke ws to get data
			HttpParams httpParams = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParams,
					CONNECTION_TIMEOUT);
			HttpConnectionParams.setSoTimeout(httpParams, CONNECTION_TIMEOUT);

			// Instantiate an HttpClient
			HttpClient httpClient = new DefaultHttpClient(httpParams);
			HttpGet httpPost = new HttpGet(WS_URL + "?id=" + this.id);
			HttpResponse httpResponse = httpClient.execute(httpPost);

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
			success = (Boolean) jsonResult.get("success");
		} catch (UnsupportedEncodingException e) {
			Log.e(TAG, "", e);
		} catch (ClientProtocolException e) {
			Log.e(TAG, "", e);
		} catch (IOException e) {
			Log.e(TAG, "", e);
		} catch (Exception e) {
			Log.e(TAG, "", e);
		}

		sendCallback(success);
	}

	/**
	 * Send callback on main-thread
	 * 
	 * @param success
	 */
	private void sendCallback(final boolean success) {
		if (callback == null)
			return;

		new Handler(context.getMainLooper()).post(new Runnable() {
			@Override
			public void run() {
				if (success)
					callback.onSuccessDeletingUser();
				else
					callback.onFailedDeletingUser();
			}
		});
	}

	/**
	 * Delete User Task callbacks
	 */
	public interface DeleteUserCallback {
		public void onSuccessDeletingUser();

		public void onFailedDeletingUser();
	}
}
