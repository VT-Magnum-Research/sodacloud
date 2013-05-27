package org.magnum.soda.example.views.projectsfragment;

import org.magnum.soda.example.maint.R;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.app.SherlockFragment;


public class ProjectsFragment extends SherlockFragment{
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_projects, container,false);
	}

}
