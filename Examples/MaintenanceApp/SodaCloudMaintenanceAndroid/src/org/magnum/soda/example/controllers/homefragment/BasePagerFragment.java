package org.magnum.soda.example.controllers.homefragment;

/**
 * @project 
 * @author chris.jenkins
 * @created Dec 28, 2011
 */
 
import org.magnum.soda.example.maint.R;

import com.actionbarsherlock.app.SherlockFragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
 
 
/**
 * @author chris.jenkins
 */
public abstract class BasePagerFragment extends SherlockFragment
{
 
    // Data
    private FragmentStatePagerAdapter mAdapter = null;
 
    // Views
    private int layoutRes;
    private View mRootView;
    public ViewPager mPager;
 
    // Handlers
    private final Handler handler = new Handler();
    private Runnable runPager;
    private boolean mCreated = false;
 
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_home, container, false);
        mPager = (ViewPager) mRootView.findViewById(R.id.viewPagerHomeFragment);
        mPager.setOffscreenPageLimit(2);
        return mRootView;
    }
 
    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        if (runPager != null) handler.post(runPager);
        mCreated = true;
    }
 
    /**
     * @see android.support.v4.app.Fragment#onPause()
     */
    @Override
    public void onPause()
    {
        super.onPause();
        handler.removeCallbacks(runPager);
    }
 
    /**
     * Set the ViewPager adapter from this or from a subclass.
     * 
     * @author chris.jenkins
     * @param adapter This is a FragmentStatePagerAdapter due to the way it creates the TAG for the 
     * Fragment.
     */
    protected void setAdapter(FragmentStatePagerAdapter adapter)
    {
        mAdapter = adapter;
        runPager = new Runnable() {
 
            @Override
            public void run()
            {
                mPager.setAdapter(mAdapter);
            }
        };
        if (mCreated)
        {
            handler.post(runPager);
        }
    }
 
    /**
     * Has to be set before onCreateView
     * 
     * @author chris.jenkins
     * @param layoutRes
     */
    protected void setLayoutRes(int layoutRes)
    {
        this.layoutRes = layoutRes;
    }
 
}