package eoh.mobile.eohapp.food;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import eoh.mobile.eohapp.R;
import eoh.mobile.eohapp.jsonutils.AsyncGetJson;
import eoh.mobile.eohapp.jsonutils.AsyncJsonListener;

public class RestaurantInfoActivity extends ActionBarActivity implements AsyncJsonListener {
	
	private static String RESTAURANT_DETAIL_JSON_URL_BASE = 
			"https://maps.googleapis.com/maps/api/place/details/json?" +
			"key=AIzaSyAp41-OkVUeovnfrR2V--OjnfyqLALReLs" +						// Google Places API key for our application
			"&sensor=false" +													// this device does not have a sensor (use static coordinates)
			"&language=en" +
			"&reference=";														// append the reference of the restaurant when you get it
			
	
	private double latitude;
	private double longitude;
	private String reference;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_restaurant_info);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		Bundle basket = getIntent().getExtras();
		reference = basket.getString("reference");		// get the reference key for this restaurant
		
		loadRestaurantInfo();
	}
	
	public void loadRestaurantInfo() {
		if(isOnline()) {
			/** Start the HTTP request for the Exhibits JSON off the main thread **/
			ProgressBar progress = (ProgressBar) findViewById(R.id.restaurant_info_progress);
			AsyncGetJson task = new AsyncGetJson(this, RESTAURANT_DETAIL_JSON_URL_BASE + reference, progress);
			task.execute();
		} else {
			showNoConnectionDialog(this);
		}
	}

	
	@Override
	public void onJsonReceived(JSONObject json) {
		try {
			if(json != null) {
				JSONObject restaurant = (JSONObject) json.get("result");

				String name = restaurant.getString("name");
				String vicinity = restaurant.getString("vicinity");
				final String url = restaurant.getString("url");			// NOTE: Google requires that we link to this page when we display Place details
				double rating;
				try {
					rating = restaurant.getDouble("rating");
				} catch(JSONException e) {
					rating = -1;
				}
				
				/** Get the price -- not all Places have this though. **/
				int price;
				try {
					price = restaurant.getInt("price_level");
				} catch(JSONException e) {
					price = -1;
				}

				/** Store the lat and long as private variables for the getDirections() function **/
				JSONObject geometry = restaurant.getJSONObject("geometry");
				JSONObject location = geometry.getJSONObject("location");
				latitude = location.getDouble("lat");
				longitude = location.getDouble("lng");
				
				
				/** Get the user reviews **/
				TextView authorNameView1 = (TextView) findViewById(R.id.restaurant_info_review1_name);
				TextView authorRatingView1 = (TextView) findViewById(R.id.restaurant_info_review1_rating);
				TextView authorReviewView1 = (TextView) findViewById(R.id.restaurant_info_review1_review);

				JSONArray reviews = restaurant.getJSONArray("reviews");
				/** Review number one **/
				if(reviews.length() > 0) {
					JSONObject review1 = reviews.getJSONObject(0);
					String authorName1 = review1.getString("author_name");
					String authorRating1 = review1.getInt("rating") + "";
					String authorReview1 = review1.getString("text");
					authorReview1 = authorReview1.replace("&#39;", "'");
					
					authorNameView1.setText(authorName1);
					authorRatingView1.setText(authorRating1 + " / 5.0");
					authorReviewView1.setText(authorReview1);
				} else {
					authorNameView1.setVisibility(View.GONE);
					authorRatingView1.setVisibility(View.GONE);
					authorReviewView1.setVisibility(View.GONE);
				}
				
				/** Review number two **/
				TextView authorNameView2 = (TextView) findViewById(R.id.restaurant_info_review2_name);
				TextView authorRatingView2 = (TextView) findViewById(R.id.restaurant_info_review2_rating);
				TextView authorReviewView2 = (TextView) findViewById(R.id.restaurant_info_review2_review);

				if(reviews.length() > 1) {
					JSONObject review2 = reviews.getJSONObject(1);
					String authorName2 = review2.getString("author_name");
					String authorRating2 = review2.getInt("rating") + "";
					String authorReview2 = review2.getString("text");
					authorReview2 = authorReview2.replace("&#39;", "'");
					
					authorNameView2.setText(authorName2);
					authorRatingView2.setText(authorRating2 + " / 5.0");
					authorReviewView2.setText(authorReview2);
				} else {
					authorNameView2.setVisibility(View.GONE);
					authorRatingView2.setVisibility(View.GONE);
					authorReviewView2.setVisibility(View.GONE);
				}
				
				
				
				TextView nameView = (TextView) findViewById(R.id.restaurant_info_name);
				nameView.setText( name );
				
				TextView vicinityView = (TextView) findViewById(R.id.restaurant_info_vicinity);
				vicinityView.setText(vicinity);
				
				TextView ratingView = (TextView) findViewById(R.id.restaurant_info_rating);
				if(rating > -1)
					ratingView.setText(rating + " / 5.0");
				else
					ratingView.setText("No rating available");
				
				TextView priceView = (TextView) findViewById(R.id.restaurant_info_price);
                switch (price) {
                    case 0:
                        priceView.setText("Free");
                        break;
                    case 1:
                        priceView.setText("$");
                        break;
                    case 2:
                        priceView.setText("$$");
                        break;
                    case 3:
                        priceView.setText("$$$");
                        break;
                    case 4:
                        priceView.setText("$$$$");
                        break;
                    default:
                        priceView.setText("-");
                        break;
                }
				
				
				TextView urlView = (TextView) findViewById(R.id.restaurant_info_url);
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
		} catch(JSONException e) {
			e.printStackTrace();
		}
		
	}
	
	
	/** Opens Google Maps and loads directions to the Exhibit **/
	public void getDirections(View view) {
		if( isAppInstalled("com.google.android.apps.maps") ) {
			
			/** Request URL: saddr=""->use current location. daddr=destination coordinates **/
			String uri = "http://maps.google.com/maps?"
					+ "daddr=" + latitude + "," + longitude;
			
			Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri));
			intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
			startActivity(intent);
		} else {
			Toast.makeText(getApplicationContext(),
					"Please download Google Maps from the Google Play Store to utilize this feature.",
					Toast.LENGTH_LONG).show();
		}
	}
	
	
	/** helper function to check if Maps is installed **/
	private boolean isAppInstalled(String uri) {
	    PackageManager pm = getApplicationContext().getPackageManager();
	    boolean app_installed = false;
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
	
	
	
	/** Determines if the device is online **/
	public boolean isOnline() {
	    ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
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
            	loadRestaurantInfo();
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
