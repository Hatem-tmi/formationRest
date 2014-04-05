package com.formation.formationrest.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.formation.formationrest.R;
import com.formation.formationrest.data.User;

public class UsersAdapter extends BaseAdapter {

	private LayoutInflater inflater;
	private List<User> data;

	public UsersAdapter(Context context, List<User> data) {
		this.data = data;
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		if (data != null)
			return data.size();
		else
			return 0;
	}

	@Override
	public Object getItem(int position) {
		if (data != null)
			return data.get(position);
		else
			return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		User user = data.get(position);

		if (convertView == null) {
			convertView = inflater.inflate(R.layout.user_item, parent, false);
		}

		TextView label = (TextView) convertView.findViewById(R.id.label);
		TextView studentName = (TextView) convertView
				.findViewById(R.id.userName);
		TextView studentLogin = (TextView) convertView
				.findViewById(R.id.userLogin);
		TextView studentAge = (TextView) convertView.findViewById(R.id.userAge);

		label.setText("Utilisateur d'id: " + user.getId());
		studentName.setText("Nom: " + user.getName());
		studentLogin.setText("Prénom: " + user.getLogin());
		studentAge.setText("Age: " + user.getAge());

		return convertView;
	}
}