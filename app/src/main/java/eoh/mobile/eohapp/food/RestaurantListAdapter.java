package eoh.mobile.eohapp.food;

import android.app.Activity;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import eoh.mobile.eohapp.R;
import eoh.mobile.eohapp.models.Restaurant;

public class RestaurantListAdapter extends ArrayAdapter<Restaurant> {

	private List<Restaurant> restaurants;
	private Resources resources;
	
	/** Constructor **/
	public RestaurantListAdapter(Activity activity, List<Restaurant> r) {
		super(activity, 0, r);
		this.restaurants = r;
		this.resources = activity.getResources();
	}
	
	
	/** Custom class to hold rowViews in memory and re-use them
	 *  Decreases number of calls to rowView.findViewById() which is expensive. **/
	static class ViewHolder {
		public TextView name;
		public TextView open;
		public TextView location;
		public TextView rating;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View rowView = convertView;
		ViewHolder holder;
		if(rowView == null) { // try to reuse a row view that is out of sight
			Activity activity = (Activity) getContext();
			LayoutInflater inflater = activity.getLayoutInflater();
			rowView = inflater.inflate(R.layout.restaurant_list_item, null);
			
			holder = new ViewHolder();
			holder.name = (TextView) rowView.findViewById(R.id.food_list_item_name);
			holder.open = (TextView) rowView.findViewById(R.id.food_list_item_open);
			holder.location = (TextView) rowView.findViewById(R.id.food_list_item_location);
			holder.rating = (TextView) rowView.findViewById(R.id.food_list_item_rating);
			rowView.setTag(holder);
		} else {
			holder = (ViewHolder) rowView.getTag();
		}
		
		Restaurant r = restaurants.get(position);
		holder.name.setText( r.getName() );
		holder.open.setText( r.getOpenNowString() );
		holder.open.setTextColor( r.getOpenNowString().equals("open") ? resources.getColor(R.color.eoh_blue) : resources.getColor(R.color.restaurant_closed) );
		holder.location.setText( r.getVicinity() );
		holder.rating.setText( r.getRatingString() );
		
		return rowView;
	}
}