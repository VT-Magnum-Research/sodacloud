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
	
//    private ViewPager mViewPager;
//    private TabsAdapter mTabsAdapter;
//	public static final String TAG = HomeFragment.class.getName();
//	
//	//handlers
//	private final Handler handler = new Handler();
//	private Runnable runPager;
//	private boolean mCreated = false;
//    
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//    		Bundle savedInstanceState) {
//    	Log.d(TAG,"onCreateView");
//    	View v = inflater.inflate(R.layout.fragment_home, container, false);
//    	mViewPager = (ViewPager)v.findViewById(R.id.viewPagerHomeFragment);
//    	mViewPager.setOffscreenPageLimit(2);
////    	setupActionBar();
////    	createTabs();
//    	
//    	return v;
//    }
//    
//    @Override
//    public void onActivityCreated(Bundle savedInstanceState) {
//    	// TODO Auto-generated method stub
//    	super.onActivityCreated(savedInstanceState);
////    	createTabs();
//    	if(runPager != null) handler.post(runPager);
//    	mCreated = true;
//    	createTabs();
//    }
//    @Override
//    public void onPause() {
//    	// TODO Auto-generated method stub
//    	super.onPause();
//    	handler.removeCallbacks(runPager);
//    }
//    
	private TabsAdapter createTabs() {
        TabsAdapter tabsAdapter = new TabsAdapter(this.getSherlockActivity(),mPager);
        final com.actionbarsherlock.app.ActionBar bar = getSherlockActivity().getSupportActionBar();
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
//	
//	protected void setAdapter(TabsAdapter adapter) {
//		mTabsAdapter = adapter;
//		runPager = new Runnable() {
//
//			@Override
//			public void run() {
//				mViewPager.setAdapter(mTabsAdapter);
//				
//			}
//		};
//		if(mCreated)
//		{
//			handler.post(runPager);
//		}
//	}
//    
	private void setupActionBar() {
        final com.actionbarsherlock.app.ActionBar bar = getSherlockActivity().getSupportActionBar();
        bar.show();
        bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        bar.setDisplayOptions(0, ActionBar.DISPLAY_SHOW_TITLE);
        getSherlockActivity().getSupportActionBar().setDisplayShowTitleEnabled(false); 
        getSherlockActivity().getSupportActionBar().setDisplayShowHomeEnabled(false);
		
	}

//	@Override
//    protected void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//        outState.putInt("tab", getActionBar().getSelectedNavigationIndex());
//    }

}
