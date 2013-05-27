package org.magnum.soda.example.views.homeactivity;

import org.magnum.soda.example.maint.R;
import org.magnum.soda.example.views.followersfragment.FollowersFragment;
import org.magnum.soda.example.views.followingfragment.FollowingFragment;
import org.magnum.soda.example.views.newsfragment.NewsFragment;
import org.magnum.soda.example.views.projectsfragment.ProjectsFragment;

import android.os.Bundle;
import android.support.v4.view.ViewPager;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuItem.OnMenuItemClickListener;

public class HomeActivity extends SherlockFragmentActivity {
    private ViewPager mViewPager;
    private TabsAdapter mTabsAdapter;
    
    public static final String TAG = HomeActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        
        instantiateMemberVariables();
        setupActionBar();
        createTabs(savedInstanceState);
   

    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {  	
    	initMenu(menu);
     	
    	return true;
    }
    
    @Override
    protected void onStop() {
    	// TODO Auto-generated method stub
    	super.onStop();
    }

	private void initMenu(Menu menu) {
		
	}


	private void createTabs(Bundle savedInstanceState) {
        mTabsAdapter = new TabsAdapter(this, mViewPager);
        final com.actionbarsherlock.app.ActionBar bar = getSupportActionBar();
        mTabsAdapter.addTab(bar.newTab().setCustomView(R.layout.fragment_news_tab_icon),
                NewsFragment.class, null);
        mTabsAdapter.addTab(bar.newTab().setCustomView(R.layout.fragment_projects_tab_icon),
                ProjectsFragment.class, null);
        mTabsAdapter.addTab(bar.newTab().setCustomView(R.layout.fragment_followers_tab_icon),
                FollowersFragment.class, null);
        mTabsAdapter.addTab(bar.newTab().setCustomView(R.layout.fragment_following_tab_icon),
                FollowingFragment.class, null);



        if (savedInstanceState != null) {
        	//Use saved instance state to set currentTabe
//	            bar.setSelectedNavigationItem(savedInstanceState.getInt("tab", 0));
        }
    }

	private void setupActionBar() {
        final com.actionbarsherlock.app.ActionBar bar = getSupportActionBar();
        bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        bar.setDisplayOptions(0, ActionBar.DISPLAY_SHOW_TITLE);
        getSupportActionBar().setDisplayShowTitleEnabled(false); 
        getSupportActionBar().setDisplayShowHomeEnabled(false);
		
	}

	private void instantiateMemberVariables() {
		mViewPager = (ViewPager) findViewById(R.id.viewPager1);
		
	}

	@Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("tab", getActionBar().getSelectedNavigationIndex());
    }
}
