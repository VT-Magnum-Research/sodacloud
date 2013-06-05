package org.magnum.soda.example.controllers.homefragment;

import java.util.ArrayList;
import java.util.List;

import org.magnum.soda.example.controllers.followersfragment.FollowersFragment;
import org.magnum.soda.example.controllers.followingfragment.FollowingFragment;
import org.magnum.soda.example.controllers.newsfragment.NewsFragment;
import org.magnum.soda.example.controllerss.reportsfragment.ReportsFragment;
import org.magnum.soda.example.maint.R;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.app.ActionBar;

public class HomeFragment extends BasePagerFragment {
	
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = super.onCreateView(inflater, container, savedInstanceState);
		
		TabsAdapter tabsAdapter = createTabs();
		setAdapter(tabsAdapter);
		setupActionBar();
		
		return v;
	}
	private TabsAdapter createTabs() {
        TabsAdapter tabsAdapter = new TabsAdapter(this.getSherlockActivity(),mPager);
        final com.actionbarsherlock.app.ActionBar bar = getSherlockActivity().getSupportActionBar();
        bar.removeAllTabs();
        tabsAdapter.addTab(bar.newTab().setCustomView(R.layout.fragment_news_tab_icon),
                NewsFragment.class, null);
        tabsAdapter.addTab(bar.newTab().setCustomView(R.layout.fragment_reports_tab_icon),
                ReportsFragment.class, null);
        tabsAdapter.addTab(bar.newTab().setCustomView(R.layout.fragment_followers_tab_icon),
                FollowersFragment.class, null);
        tabsAdapter.addTab(bar.newTab().setCustomView(R.layout.fragment_following_tab_icon),
                FollowingFragment.class, null);

        return tabsAdapter;
    }
	private void setupActionBar() {
        final com.actionbarsherlock.app.ActionBar bar = getSherlockActivity().getSupportActionBar();
        bar.show();
        bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        bar.setDisplayOptions(0, ActionBar.DISPLAY_SHOW_TITLE);
        getSherlockActivity().getSupportActionBar().setDisplayShowTitleEnabled(false); 
        getSherlockActivity().getSupportActionBar().setDisplayShowHomeEnabled(false);
		
	}

}
