package com.formation.formationrest.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.formation.formationrest.R;
import com.formation.formationrest.util.Utils;

public class MainActivity extends Activity {

	private EditText emailEditText;
	private EditText passwordEditText;
	private Button okButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		emailEditText = (EditText) findViewById(R.id.emailEditText);
		passwordEditText = (EditText) findViewById(R.id.passwordEditText);
		okButton = (Button) findViewById(R.id.okButton);

		okButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String email = emailEditText.getText().toString();
				String password = passwordEditText.getText().toString();

				if (Utils.isValidEmail(email) && password.equals("admin")) {

					// save user email in Shared-Preferences
					Utils.setUserEmail(getApplicationContext(), email);

					Intent intent = new Intent(MainActivity.this,
							UsersActivity.class);
					startActivity(intent);

					finish();
				} else {
					Toast.makeText(MainActivity.this,
							"Erreur d'authentification", Toast.LENGTH_LONG)
							.show();
				}
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();

		// check if user is logged-in from Shared-Preferences
		if (Utils.getUserEmail(getApplicationContext()) != null) {
			Intent intent = new Intent(MainActivity.this, UsersActivity.class);
			startActivity(intent);

			finish();
		}
	}
}
