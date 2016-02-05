package eoh.mobile.eohapp.exhibits;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.TextView;

import java.util.ArrayList;

import eoh.mobile.eohapp.MainActivity;
import eoh.mobile.eohapp.R;
import eoh.mobile.eohapp.models.Exhibit;

public class ExhibitsListAdapter extends ArrayAdapter<Exhibit> {
		
	private ArrayList<Exhibit> currentExhibits;
	private ExhibitsFilter mExhibitsFilter;
	private MainActivity mMainActivity;
	private LayoutInflater mLayoutInflater;
		
	public ExhibitsListAdapter(Activity activity, ArrayList<Exhibit> e) {
		super(activity, 0, e);
		mMainActivity = (MainActivity) activity;
		currentExhibits = mMainActivity.getFilteredExhibitsList();
		mLayoutInflater = mMainActivity.getLayoutInflater();
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
			rowView = mLayoutInflater.inflate(R.layout.exhibits_list_item, null);
			
			holder = new ViewHolder();
			holder.name = (TextView) rowView.findViewById(R.id.exhibit_list_item_name);
			holder.major = (TextView) rowView.findViewById(R.id.exhibit_list_item_major);
			holder.location = (TextView) rowView.findViewById(R.id.exhibit_list_item_location);
			holder.star = (CheckBox) rowView.findViewById(R.id.exhibit_list_item_star);
			rowView.setTag(holder);
		} else {
			holder = (ViewHolder) rowView.getTag();
		}

        Exhibit currExhibit = currentExhibits.get(position);
        holder.name.setText(currExhibit.getName());
        holder.major.setText(currExhibit.getMajor());
        holder.location.setText(currExhibit.readableLocation());
        holder.star.setChecked(currExhibit.isFavorite());
        holder.star.setId(currExhibit.getId());
		
		return rowView;
	}
	
	
	/** Reload the Exhibits list from the static copy in app (no network call).
	 *  Usually called because something's favorite status was changed **/
	public void reloadExhibitsList() {
		this.notifyDataSetChanged();
	}
	
	
	@Override
	public Filter getFilter() {
		if(mExhibitsFilter == null)
			mExhibitsFilter = new ExhibitsFilter();
		return mExhibitsFilter;
	}
	
	
	/** Private class that filters the Exhibits ArrayList and creates a new list
	 *  with the desired content.  Must be in this file to have access to the ArrayList */
	private class ExhibitsFilter extends Filter {
		
		private static final int MAJOR_SPINNER_ID = 1;
		private static final int BUILDING_SPINNER_ID = 2;

        private String defaultMajor;
        private String defaultBuilding;
		private String selectedMajor;
		private String selectedBuilding;

        public ExhibitsFilter() {
            defaultMajor = mMainActivity.getResources().getString(R.string.major_spinner_default);
            defaultBuilding = mMainActivity.getResources().getString(R.string.building_spinner_default);

            selectedMajor = defaultMajor;
            selectedBuilding = defaultBuilding;
        }

		/** Filter the list of exhibits by both major and building.
         * This is performed in a background thread.
         **/
		@Override
		protected FilterResults performFiltering(CharSequence constraint) {
			FilterResults results = new FilterResults();
			
			// Get the constant list of all allExhibits from the parent MainActivity
			ArrayList<Exhibit> allExhibits = mMainActivity.getAllExhibitsList();
            // make a temporary list to add to in this background thread
			ArrayList<Exhibit> tempFilteringList = new ArrayList<Exhibit>();
			
			String[] constraintInfo = constraint.toString().split(":");
			int id = Integer.parseInt(constraintInfo[0]);
			if(id == MAJOR_SPINNER_ID)
				selectedMajor = constraintInfo[1];
			else if(id == BUILDING_SPINNER_ID)
				selectedBuilding = constraintInfo[1];
			
			for(Exhibit currExhibit : allExhibits) {
                if (isExhibitInFilterParams(currExhibit, selectedMajor, selectedBuilding))
					tempFilteringList.add(currExhibit);
			}				
			
			results.values = tempFilteringList;
			results.count = tempFilteringList.size();
			return results;
		}

		@SuppressWarnings("unchecked")
		@Override
		protected void publishResults(CharSequence constraint, FilterResults results) {
			if(results != null) {
                // take the values out of the temp list and put it in the list bound to the adapter
				currentExhibits.clear();
				currentExhibits.addAll( (ArrayList<Exhibit>) results.values );
				notifyDataSetChanged();
			} else {
				notifyDataSetInvalidated();
			}
		}

        private boolean isExhibitInFilterParams(Exhibit exhibit, String selectedMajor, String selectedBuilding) {
            String major = exhibit.getMajor();
            String building = exhibit.getBuilding();
            return ((selectedMajor.equals(defaultMajor)
                        || selectedMajor.equals(major))
                    && (selectedBuilding.equals(defaultBuilding)
                        || selectedBuilding.equals(building)));
        }
		
	}
}