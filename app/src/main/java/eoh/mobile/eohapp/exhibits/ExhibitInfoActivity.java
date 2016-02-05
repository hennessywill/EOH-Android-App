package eoh.mobile.eohapp.exhibits;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import eoh.mobile.eohapp.R;

/**
 *  @author Will Hennessy
 *  
 *  This activity displays more information on one specific exhibit including
 *  name, location, description, time, etc.
 */

public class ExhibitInfoActivity extends ActionBarActivity {
	
	private String exhibitName;
	private String address;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_exhibit_info);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		Bundle basket = getIntent().getExtras();
		exhibitName = basket.getString("name");
		String description = basket.getString("description");
		String major = basket.getString("major");
		String building = basket.getString("building");
		String room = basket.getString("room");
		address = basket.getString("address");
		boolean isFavorite = basket.getBoolean("isFavorite");
		int id = basket.getInt("id");
		
		final String url = basket.getString("url");

		TextView nameView = (TextView) findViewById(R.id.exhibit_info_name);
		nameView.setText( exhibitName );
		
		TextView descriptionView = (TextView) findViewById(R.id.exhibit_info_description);
		descriptionView.setText( description );
		
		TextView departmentView = (TextView) findViewById(R.id.exhibit_info_major);
		departmentView.setText( major );
		
		TextView locationView = (TextView) findViewById(R.id.exhibit_info_location);
		String readableLocation = building;
		if(room != null && room.length() > 0)
			readableLocation += (", " + room);
		locationView.setText( readableLocation );
		
		CheckBox star = (CheckBox) findViewById(R.id.exhibit_info_star);
		star.setChecked(isFavorite);
		star.setId(id);
		
		
		TextView urlView = (TextView) findViewById(R.id.exhibit_info_url);
		if( url != null && !url.equals("") )
		{
			urlView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Uri address = Uri.parse(url);
					Intent browser = new Intent(Intent.ACTION_VIEW, address);
					startActivity(browser);
				}
			});
		}
		else
			urlView.setVisibility(View.GONE);
	}
	
	
	public void getDirections(View view) {
		if(isAppInstalled("com.google.android.apps.maps")) {
			
			String uri = "http://maps.google.com/maps?daddr=" + address;
			
			Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri));
			intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
			startActivity(intent);
		} else {
			Toast.makeText(getApplicationContext(),
					"Please download Google Maps from the Google Play Store to utilize this feature.",
					Toast.LENGTH_LONG).show();
		}
	}
	
	
	// helper function to check if Maps is installed
	private boolean isAppInstalled(String uri) {
	    PackageManager pm = getApplicationContext().getPackageManager();
	    boolean app_installed;
	    try {
	        pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
	        app_installed = true;
	    } catch (PackageManager.NameNotFoundException e) {
	        app_installed = false;
	    }
	    return app_installed;
	}
	
	
	/** 
	 *  Define the behavior of the home/up button in action bar
	 *  Note:  Technically, the home button should not be the same as the back button.
	 *   However, since MainActivity contains a ViewPager with four fragments, the
	 *   typical "home/up" behavior does not restore the state of these fragments.
	 *   Thus, in order to avoid reloading all the data over the network, I use
	 *   onBackPressed() here.
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
            case android.R.id.home:
                this.onBackPressed();
                return true;
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	public void onStarClicked(View v) {
		CheckBox star = (CheckBox) v;
		int starId = star.getId();
		
		Intent returnIntent = new Intent();
		returnIntent.putExtra("exhibitId", starId);
		returnIntent.putExtra("isFavorite", star.isChecked());
		setResult(RESULT_OK, returnIntent);

		SharedPreferences favorites = getSharedPreferences("favoriteExhibits", Context.MODE_PRIVATE);
		SharedPreferences.Editor favoritesEditor = favorites.edit();
		favoritesEditor.putBoolean(exhibitName, star.isChecked());
		favoritesEditor.commit();
	}

}
