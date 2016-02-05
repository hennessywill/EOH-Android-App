package eoh.mobile.eohapp;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import eoh.mobile.eohapp.exhibits.ExhibitsListAdapter;
import eoh.mobile.eohapp.exhibits.ExhibitsTabFragment;
import eoh.mobile.eohapp.food.RestaurantTabFragment;
import eoh.mobile.eohapp.newsfeed.NewsfeedWebViewFragment;
import eoh.mobile.eohapp.planner.PlannerListAdapter;
import eoh.mobile.eohapp.planner.PlannerTabFragment;

/**
 *  @author Will Hennessy
 *  
 * 	A FragmentPagerAdapter that returns a fragment corresponding to one of the four tabs.
 *  Based on the ViewPager example on the Android docs.
 */

public class AppSectionsPagerAdapter extends FragmentPagerAdapter {
	
	private static final String[] TAB_LABELS = {"Exhibits", "Planner", "Food", "News"};
	
	private ExhibitsTabFragment mExhibitsTabFragment;
	private PlannerTabFragment mPlannerTabFragment;
	//private FragmentManager mFragmentManager;
	
	public AppSectionsPagerAdapter(FragmentManager fm) {
		super(fm);
		//mFragmentManager = fm;
		
		//mExhibitsTabFragment = new ExhibitsTabFragment();
		//mPlannerTabFragment = new PlannerTabFragment();
		
		//this.reloadExhibitsList();
		//this.reloadFavoritesList();
	}
	
//	public void destroyFragments() {
//		if(mFragmentManager != null) {
//			Log.d("EOHAPP", "wooo calling destroy fragments");
//	        FragmentTransaction trans = mFragmentManager.beginTransaction();
//	        
//	        //Fragment exhibitsFragment = mFragmentManager.findFragmentByTag(TAB_LABELS[0]);
//	        //Fragment plannerFragment = mFragmentManager.findFragmentByTag(TAB_LABELS[1]);
//
//	        trans.remove( mExhibitsTabFragment );
//	        trans.remove( mPlannerTabFragment );
//	        trans.commit();
//		}
//	}
	
	
	@Override
	public Fragment getItem(int i) {
		switch (i) {
			case 0:
				mExhibitsTabFragment = new ExhibitsTabFragment();		// Tab 1:  Exhibits
				mExhibitsTabFragment.setRetainInstance(false);
				return mExhibitsTabFragment;
			case 1:
				mPlannerTabFragment = new PlannerTabFragment();			// Tab 2:  Planner
				mPlannerTabFragment.setRetainInstance(false);
				return mPlannerTabFragment;
			case 2:
				return new RestaurantTabFragment();							// Tab 3:  Food
			case 3:
				return new NewsfeedWebViewFragment();					// Tab 4:  Newsfeed

			default:
				return new ExhibitsTabFragment();
		}
	}
	
	@Override
	public int getCount() {
		return 4;
	}
	
	@Override
	public CharSequence getPageTitle(int position) {
		return TAB_LABELS[position];
	}
	
	@Override
	public void setPrimaryItem(ViewGroup container, int position, Object object) {
		if( position == 0 ) {
			reloadExhibitsList();
		}
		else if( position == 1 ) {
			reloadFavoritesList();
		}
	}
	
	public void reloadExhibitsList() {
		if(mExhibitsTabFragment != null) {
			ExhibitsListAdapter adapter = (ExhibitsListAdapter) mExhibitsTabFragment.getListAdapter();
			if(adapter != null)	// if adapter is null, the app is opening and the adapter hasn't been set yet. -> don't need to refresh
				adapter.reloadExhibitsList();	// calls notifyDataSetChanged()
		}
	}
	
	public void reloadFavoritesList() {
		if(mPlannerTabFragment != null) {
			PlannerListAdapter adapter = (PlannerListAdapter) mPlannerTabFragment.getListAdapter();
			if(adapter != null)
				adapter.reloadFavoritesList();	// re-examines the exhibit list for all favorites, then calls notifyDataSetChanged()
		}
	}
	
}