package org.magnum.soda.example.controllers.homeactivity;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.magnum.soda.example.controllers.createreportfragment.CreateReportFragment;
import org.magnum.soda.example.controllers.homefragment.HomeFragment;
import org.magnum.soda.example.controllers.searchlocationfragment.SearchLocationFragment;
import org.magnum.soda.example.controllers.searchqrfragment.SearchQrFragment;
import org.magnum.soda.example.maint.R;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.slidingmenu.lib.SlidingMenu;
import com.slidingmenu.lib.app.SlidingFragmentActivity;
//import org.magnum.soda.example.views.followersfragment.FollowersFragment;
//import org.magnum.soda.example.views.followingfragment.FollowingFragment;
//import org.magnum.soda.example.views.newsfragment.NewsFragment;
//import org.magnum.soda.example.views.projectsfragment.ProjectsFragment;

public class HomeActivity extends SlidingFragmentActivity {
    private Fragment mMenuFragment;
    private SherlockFragment mCurrentFragment;
    
    private Map<String,SherlockFragment> mMenuControlledFragments;
    
    public static final String FRAGMENT_HOME = "home";
	public static final String FRAGMENT_SEARCH_LOCATION = "search_location";
	public static final String FRAGMENT_SEARCH_QR = "search_qr";
	public static final String FRAGMENT_CREATE_REPORT = "create_report";
    
    public static final String TAG = HomeActivity.class.getName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      
        setContentView(R.layout.activity_base);
        setBehindContentView(R.layout.menu_frame);
        
        FragmentTransaction t = this.getSupportFragmentManager()
				.beginTransaction();
		// init MenuFragment
		mMenuFragment = new SlidingMenuFragment();
		// set MenuFragment
		t.replace(R.id.menu_frame, mMenuFragment);
		t.commit();
		
		setupSlidingMenu();
        
//        instantiateMemberVariables();
//        setupActionBar();
//        createTabs(savedInstanceState);
//   
		initMenuControlledFragments();
    }
    
	private void initMenuControlledFragments() {
		mMenuControlledFragments = new HashMap<String, SherlockFragment>();

		mMenuControlledFragments.put(FRAGMENT_HOME, new HomeFragment());

		mMenuControlledFragments.put(FRAGMENT_SEARCH_LOCATION, new SearchLocationFragment());

		mMenuControlledFragments.put(FRAGMENT_SEARCH_QR, new SearchQrFragment());
//
		mMenuControlledFragments.put(FRAGMENT_CREATE_REPORT, new CreateReportFragment());

		switchContent(FRAGMENT_HOME);
	}
	
	public void switchContent(final String fragmentName) {
		Log.d(TAG,"SwitchContent");
		SherlockFragment newFragment = mMenuControlledFragments
				.get(fragmentName);
		if (newFragment != null) {
			if (newFragment != mCurrentFragment) {
				getSupportFragmentManager().beginTransaction()
						.replace(R.id.content_frame, newFragment).commit();
				mCurrentFragment = newFragment;
			}
			// close menu and show the content fragment
			Handler h = new Handler();
			h.postDelayed(new Runnable() {
				public void run() {
					getSlidingMenu().showContent();
				}
			}, 50);
			// update ActionBar title

//			TextView titleTextView = (TextView) this.getSupportActionBar()
//					.getCustomView().findViewById(R.id.barTitle);
//			titleTextView.setText(fragmentName);
		}
	}
    
    private void setupSlidingMenu() {
		SlidingMenu sm = getSlidingMenu();
		sm.setShadowWidthRes(R.dimen.shadow_width);
		sm.setShadowDrawable(R.drawable.shadow);
		sm.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		sm.setFadeDegree(0.35f);
		sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
		
	}
//    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {  	
//    	initMenu(menu);
     	
    	return true;
    }
    
    @Override
    protected void onStop() {
    	// TODO Auto-generated method stub
    	super.onStop();
    }

//	private void initMenu(Menu menu) {
//		
//	}
//
//
//	private void createTabs(Bundle savedInstanceState) {
//        mTabsAdapter = new TabsAdapter(this, mViewPager);
//        final com.actionbarsherlock.app.ActionBar bar = getSupportActionBar();
//        mTabsAdapter.addTab(bar.newTab().setCustomView(R.layout.fragment_news_tab_icon),
//                NewsFragment.class, null);
//        mTabsAdapter.addTab(bar.newTab().setCustomView(R.layout.fragment_reports_tab_icon),
//                ReportsFragment.class, null);
//        mTabsAdapter.addTab(bar.newTab().setCustomView(R.layout.fragment_followers_tab_icon),
//                FollowersFragment.class, null);
//        mTabsAdapter.addTab(bar.newTab().setCustomView(R.layout.fragment_following_tab_icon),
//                FollowingFragment.class, null);
//
//
//
//        if (savedInstanceState != null) {
//        	//Use saved instance state to set currentTabe
////	            bar.setSelectedNavigationItem(savedInstanceState.getInt("tab", 0));
//        }
//    }
//
//	private void setupActionBar() {
//        final com.actionbarsherlock.app.ActionBar bar = getSupportActionBar();
//        bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
//        bar.setDisplayOptions(0, ActionBar.DISPLAY_SHOW_TITLE);
//        getSupportActionBar().setDisplayShowTitleEnabled(false); 
//        getSupportActionBar().setDisplayShowHomeEnabled(false);
//		
//	}

//	private void instantiateMemberVariables() {
//		mViewPager = (ViewPager) findViewById(R.id.viewPager1);
//		
//	}

//	@Override
//    protected void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//        outState.putInt("tab", getActionBar().getSelectedNavigationIndex());
//    }
	
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub		
		Log.i(TAG, "Deleting all fragments");
		FragmentTransaction ft = this.getSupportFragmentManager().beginTransaction();		
		if (ft != null && mMenuControlledFragments != null && mMenuControlledFragments.size() > 0) {
			for(Entry<String, SherlockFragment> entry : mMenuControlledFragments.entrySet()) {
				ft.remove(entry.getValue());
			}
			ft.commitAllowingStateLoss();
			this.getSupportFragmentManager().executePendingTransactions();
		}
		super.onDestroy();		
	}
}
