package eoh.mobile.eohapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import eoh.mobile.eohapp.jsonutils.AsyncGetJson;
import eoh.mobile.eohapp.jsonutils.AsyncJsonListener;
import eoh.mobile.eohapp.models.Exhibit;

/**
 *  @author Will Hennessy
 *  
 *  The main activity opened from the launcher icon.  Contains a view pager that
 *  contains the four tabs:  allExhibits, food, planner, newsfeed.
 *  ActionBarActivity also extends from FragmentActivity, so we can use fragments
 */

public class MainActivity extends ActionBarActivity implements ActionBar.TabListener, AsyncJsonListener {
	
	AppSectionsPagerAdapter mAppSectionsPagerAdapter;
	ViewPager mViewPager;
	private static final String EXHIBITS_JSON_URL = "http://eohmobile.appspot.com/api/exhibits.json";
	private SharedPreferences favorites;

	private ArrayList<Exhibit> allExhibits = new ArrayList<Exhibit>();
	private ArrayList<Exhibit> filteredExhibits = new ArrayList<Exhibit>();
	private ArrayList<Exhibit> favoriteExhibits = new ArrayList<Exhibit>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		favorites = this.getSharedPreferences("favoriteExhibits", Context.MODE_PRIVATE);
		loadExhibits();
	}

    /** Start the HTTP request for the Exhibits JSON in an AsyncTask **/
    protected void loadExhibits() {
		if(isOnline()) {
			ImageView ilogo = (ImageView) findViewById(R.id.main_activity_illinois_logo);
			ilogo.setVisibility(View.VISIBLE);

            ProgressBar progress = (ProgressBar) findViewById(R.id.main_activity_progress);
            AsyncGetJson task = new AsyncGetJson(this, EXHIBITS_JSON_URL, progress);
			task.execute();
		} else {
			showNoConnectionDialog(this);
		}
	}

    /** When the tab is pressed, switch to the corresponding page in the ViewPager **/
    @Override
	public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
		mViewPager.setCurrentItem( tab.getPosition() );
	}
	
	@Override
	public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
		// do nothing
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
		// do nothing
	}


	/** Callback method when exhibit data finished loading. **/
	@Override
	public void onJsonReceived(JSONObject json) {
		ImageView ilogo = (ImageView) findViewById(R.id.main_activity_illinois_logo);
		ilogo.setVisibility(View.GONE);
				
		try {
			if(json != null) {
				JSONArray jsonArray = (JSONArray) json.get("results");
				
				SharedPreferences favorites = this.getSharedPreferences("favoriteExhibits", Context.MODE_PRIVATE);

				for( int i = 0; i < jsonArray.length(); i++) {
					JSONObject curr = jsonArray.getJSONObject(i);
					String name = curr.getString("name");
					String description = curr.getString("description");
					String major = curr.getString("department");
					String building = curr.getString("building");
					String room = curr.getString("room");
					String address = curr.getString("address");
					String url = curr.getString("url");
					boolean isFavorite = favorites.getBoolean(name, false);
					int id = i;	 // the number of this exhibit in the sorted list

					Exhibit newExhibit = new Exhibit(name, description, major,
                                                    building, room, address, url, isFavorite, id);
					allExhibits.add( newExhibit );
					filteredExhibits.add( newExhibit );
					if(isFavorite)
						favoriteExhibits.add(newExhibit);
				}
			}
			else {
				showNoConnectionDialog(this);
				return;
			}
		} catch(JSONException e) {
			e.printStackTrace();
			showNoConnectionDialog(this);
			return;
		}
						
		final ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setHomeButtonEnabled(false);
		
		mAppSectionsPagerAdapter = new AppSectionsPagerAdapter( this.getSupportFragmentManager() );
		mAppSectionsPagerAdapter.notifyDataSetChanged();
		

		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mAppSectionsPagerAdapter);
		// Keep all four tab layouts in memory to eliminate repeated network calls for the data.
		mViewPager.setOffscreenPageLimit(3);
		mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				actionBar.setSelectedNavigationItem(position);
			}
		});
				
		// Create each tab and set a listener
		for(int i = 0; i < mAppSectionsPagerAdapter.getCount(); i++) {
			actionBar.addTab(
					actionBar.newTab()
						.setText(mAppSectionsPagerAdapter.getPageTitle(i))
						.setTabListener(this));
		}
	}
	
	
	/** Getter function that allows Exhibits tab to access all Exhibits list **/
	public ArrayList<Exhibit> getAllExhibitsList() {
		return this.allExhibits;
	}
	
	/** Getter function that allows Exhibits tab to access Exhibits list **/
	public ArrayList<Exhibit> getFilteredExhibitsList() {
		return this.filteredExhibits;
	}
	
	/** Getter function that allows the Planner tab to access favorite Exhibits list **/
	public ArrayList<Exhibit> getFavoriteExhibitsList() {
		return this.favoriteExhibits;
	}
	
	/** Getter function that returns the exhibit at index idx **/
	public Exhibit getExhibit(int idx) {
		if(idx >= 0 && idx < allExhibits.size())
			return allExhibits.get(idx);
		else
			return null;
	}
	
	/** Method that responds to a favorite's star click in either the Exhibits tab or Planner tab **/
	public void onStarClicked(View v) {
		CheckBox star = (CheckBox) v;
		int starId = star.getId();
		Exhibit clickedExhibit = allExhibits.get(starId);
		String exhibitName = clickedExhibit.getName();
		clickedExhibit.setFavorite( star.isChecked() );
		
		SharedPreferences.Editor favoritesEditor = favorites.edit();
		favoritesEditor.putBoolean(exhibitName, star.isChecked());
		favoritesEditor.commit();
	}
	
	/** When ExhibitInfoActivity finishes, it will send this intent with the updated status of its favorite star **/
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
	}
	
	
	
	/**
     * @return true if the device is online
     **/
	public boolean isOnline() {
	    ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo netInfo = cm.getActiveNetworkInfo();
	    return (netInfo != null && netInfo.isConnected());
	}
	
    public void showNoConnectionDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(true);
        builder.setMessage("No Internet connection found");
        builder.setTitle("Error");
        builder.setPositiveButton("Try Again", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            	loadExhibits();
            	return;
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                return;
            }
        });
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            public void onCancel(DialogInterface dialog) {
                return;
            }
        });

        builder.show();
    }

}
