package eoh.mobile.eohapp.planner;

import java.util.ArrayList;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import eoh.mobile.eohapp.MainActivity;
import eoh.mobile.eohapp.R;
import eoh.mobile.eohapp.models.Exhibit;

public class PlannerListAdapter extends ArrayAdapter<Exhibit> {
	
	private ArrayList<Exhibit> favoriteExhibits;	// The allExhibits currently displayed in the list. Changes depending on filter.
	private MainActivity mMainActivity;
	
	/** Constructor **/
	public PlannerListAdapter(Activity activity, ArrayList<Exhibit> e) {
		super(activity, 0, e);
		this.favoriteExhibits = e;
		mMainActivity = (MainActivity) activity;
	}
	
	
	/** Custom class to hold rowViews in memory and re-use them
	 *  Decreases number of calls to rowView.findViewById() which is expensive. **/
	static class ViewHolder {
		public TextView name;
		public TextView major;
		public TextView location;
		public CheckBox star;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View rowView = convertView;
		ViewHolder holder;
		if(rowView == null) { // try to reuse a row view that is out of sight
			Activity activity = (Activity) getContext();
			LayoutInflater inflater = activity.getLayoutInflater();
			rowView = inflater.inflate(R.layout.exhibits_list_item, null);
			
			holder = new ViewHolder();
			holder.name = (TextView) rowView.findViewById(R.id.exhibit_list_item_name);
			holder.major = (TextView) rowView.findViewById(R.id.exhibit_list_item_major);
			holder.location = (TextView) rowView.findViewById(R.id.exhibit_list_item_location);
			holder.star = (CheckBox) rowView.findViewById(R.id.exhibit_list_item_star);
			rowView.setTag(holder);
		} else {
			holder = (ViewHolder) rowView.getTag();
		}

		Exhibit currExhibit = favoriteExhibits.get(position);
		holder.name.setText( currExhibit.getName() );
		holder.major.setText( currExhibit.getMajor() );
		holder.location.setText( currExhibit.getBuilding() + ", " + currExhibit.getRoom() );
		holder.star.setChecked( currExhibit.isFavorite() );
		holder.star.setId( currExhibit.getId() );
				
		return rowView;
	}
	
	/** Reload the data in the favorites list.  Usually called because something's favorite status was changed **/
	public void reloadFavoritesList() {
		ArrayList<Exhibit> allExhibits = mMainActivity.getAllExhibitsList();
		favoriteExhibits.clear();
		for(int i = 0; i < allExhibits.size(); i++) {
			Exhibit currExhibit = allExhibits.get(i);
			if(currExhibit.isFavorite())
				favoriteExhibits.add(currExhibit);
		}
		
		this.notifyDataSetChanged();
	}
	
}