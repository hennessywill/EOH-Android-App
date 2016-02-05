package eoh.mobile.eohapp.food;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import eoh.mobile.eohapp.R;
import eoh.mobile.eohapp.jsonutils.AsyncGetJson;
import eoh.mobile.eohapp.jsonutils.AsyncJsonListener;
import eoh.mobile.eohapp.models.Restaurant;

/**
 * @author Will Hennessy
 *
 *  Displays a list of restaurants in the Champaign area by sending a request to the Google Places API.
 *  We are allowed 100,000 requests per 24-hour period.
 */

public class RestaurantTabFragment extends ListFragment implements AsyncJsonListener {
	
	private static final String RESTAURANTS_JSON_URL = 
			"https://maps.googleapis.com/maps/api/place/nearbysearch/json?" +	// receive a json
			"key=AIzaSyAp41-OkVUeovnfrR2V--OjnfyqLALReLs" +						// private key for EOH Android app
			"&location=40.110271,-88.230364" +									// coordinates of Green and 6th Street intersection
			"&radius=500" +														// radius in meters
			"&sensor=false" +													// does device have a GPS sensor
			"&types=bar|cafe|restaurant";										// types of Places to return
	
	private RestaurantListAdapter adapter;
	private String nextPageToken;	// token to get 20 more restaurants. may be null if none is returned in JSON
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View rootView = inflater.inflate(R.layout.tab_food, container, false);
		return rootView;
	}
	
	
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
				
		FragmentActivity activity = getActivity();
		View footer = activity.getLayoutInflater().inflate(R.layout.restaurant_list_footer, null);
		this.getListView().addFooterView(footer, null, false);
		
		Button loadmore = (Button) activity.findViewById(R.id.food_list_footer_loadmore);
		loadmore.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				loadRestaurants();
			}
		});
		
		// note: you must set the list adapter after the footer is added
		adapter = new RestaurantListAdapter(activity, new ArrayList<Restaurant>());
		this.setListAdapter(adapter);
		
		nextPageToken = ""; // make it not null to get past first loadRestaurants()
		loadRestaurants();
	}
	
	public void loadRestaurants() {
		FragmentActivity activity = this.getActivity();
		if(nextPageToken != null) {
			if(isOnline(activity)) {
				String url = RESTAURANTS_JSON_URL;
				if(adapter.getCount() > 0)
					url = url + "&pagetoken=" + nextPageToken;

				ProgressBar progress = (ProgressBar) activity.findViewById(R.id.tab_food_progress);
				AsyncGetJson task = new AsyncGetJson(this, url, progress);
				task.execute();
			} else {
				showNoConnectionDialog(activity);
			}
		}
		else {
			Toast.makeText(getActivity(), "There are no more restaurants to display", 
					   Toast.LENGTH_SHORT).show();
		}
	}
	
	

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Restaurant clicked = (Restaurant) l.getItemAtPosition(position);
		Bundle basket = new Bundle();
		basket.putString("reference", clicked.getReference());
		
		Intent launchInfo = new Intent(this.getActivity(), RestaurantInfoActivity.class);
		launchInfo.putExtras(basket);
		startActivity(launchInfo);
	}

	
	/** Determines if the device is online **/
	public boolean isOnline(Context context) {
	    ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo netInfo = cm.getActiveNetworkInfo();
	    if( netInfo != null && netInfo.isConnected() )
	        return true;
	    return false;
	}
	
    public void showNoConnectionDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(true);
        builder.setMessage("No Internet connection found");
        builder.setTitle("Error");
        builder.setPositiveButton("Try Again", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                //ctx.startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
            	loadRestaurants();
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

	@Override
	public void onJsonReceived(JSONObject json) {
		try {
			if(json != null) {
				/** Store the nextPageToken **/
				try {
					nextPageToken = json.getString("next_page_token");
				} catch(JSONException e) {
					nextPageToken = null;
				}
				
				/** Parse out all of the restaurants **/
				JSONArray jsonArray = (JSONArray) json.get("results");
				for( int i = 0; i < jsonArray.length(); i++) {
					JSONObject curr = jsonArray.getJSONObject(i);
					String name = curr.getString("name");
					String vicinity = curr.getString("vicinity");
					String reference = curr.getString("reference");
					
					// json may or may not have a rating
					double rating;
					try {
						rating = curr.getDouble("rating");
					} catch (JSONException e) { 
						continue;
					}
					
					// json may or may not have an open_now
					int openNow;
					try {
						boolean isOpen = curr.getJSONObject("opening_hours").getBoolean("open_now");
						openNow = isOpen ? 1 : 0;
					} catch(JSONException e) { 
						continue;
					}
					
					adapter.add(new Restaurant(name, vicinity, reference, rating, openNow));
				}
				adapter.notifyDataSetChanged();
			}
			
			/** Google Places will only return up to 60 places, but we omit a few that don't have rating/opennow.
			 *  If the list has over 40, we have made all three calls (20*3=60) so don't show the Load button. **/
			if(nextPageToken == null) //adapter.getCount() > 40)
				getActivity().findViewById(R.id.food_list_footer_loadmore).setVisibility(View.GONE);
						
		} catch(JSONException e) {
			e.printStackTrace();
		}
	}

}