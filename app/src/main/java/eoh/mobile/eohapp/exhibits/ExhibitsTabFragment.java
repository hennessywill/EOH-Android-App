package eoh.mobile.eohapp.exhibits;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Collections;

import eoh.mobile.eohapp.MainActivity;
import eoh.mobile.eohapp.R;
import eoh.mobile.eohapp.models.Exhibit;

public class ExhibitsTabFragment extends ListFragment implements OnItemSelectedListener {
	
	private static final int REQUEST_CODE = 1;
	private static final int MAJOR_SPINNER_ID = 1;
	private static final int BUILDING_SPINNER_ID = 2;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View rootView = inflater.inflate(R.layout.tab_exhibits, container, false);
		return rootView;
	}
	
	
	@Override
	public void onStop() {
		super.onStop();
		ExhibitsListAdapter adapter = (ExhibitsListAdapter) this.getListAdapter();		// is this necessary?? trying to fix fragment crash bug
		if(adapter != null)
			adapter.notifyDataSetInvalidated();
	}
	
	
	@Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
		MainActivity activity = (MainActivity) getActivity();
		this.setListAdapter(new ExhibitsListAdapter(activity, activity.getFilteredExhibitsList()));

        initMajorSpinner(activity);
        initBuildingSpinner(activity);
	}

    /*
     * Build a list of unique major names and bind it to the spinner.
     */
    public void initMajorSpinner(MainActivity activity) {
        ArrayList<String> majors = new ArrayList<String>();
        ArrayList<Exhibit> allExhibits = activity.getAllExhibitsList();
        for (Exhibit e : allExhibits) {
            if (!majors.contains(e.getMajor()))
                majors.add(e.getMajor());
        }
        Collections.sort(majors);
        String majorSpinnerDefault = getResources().getString(R.string.major_spinner_default);
        majors.add(0, majorSpinnerDefault);

        Spinner majorSpinner = (Spinner) activity.findViewById(R.id.exhibits_header_major_spinner);
        majorSpinner.setOnItemSelectedListener(this);
        ArrayAdapter<String> majorSpinnerAdapter = new ArrayAdapter<String>(activity,
                android.R.layout.simple_spinner_item, majors);
        majorSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        majorSpinner.setId(MAJOR_SPINNER_ID);
        majorSpinner.setAdapter(majorSpinnerAdapter);
    }

    /*
     * Build a list of unique building names and bind it to the spinner.
     */
    public void initBuildingSpinner(MainActivity activity) {
        ArrayList<String> buildings = new ArrayList<String>();
        ArrayList<Exhibit> allExhibits = activity.getAllExhibitsList();
        for (Exhibit e : allExhibits) {
            if (!buildings.contains(e.getBuilding()))
                buildings.add(e.getBuilding());
        }
        Collections.sort(buildings);
        String buildingSpinnerDefault = getResources().getString(R.string.building_spinner_default);
        buildings.add(0, buildingSpinnerDefault);

        Spinner buildingSpinner = (Spinner) activity.findViewById(R.id.exhibits_header_building_spinner);
        buildingSpinner.setOnItemSelectedListener(this);
        ArrayAdapter<String> buildingSpinnerAdapter = new ArrayAdapter<String>(activity,
                android.R.layout.simple_spinner_item, buildings);
        buildingSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        buildingSpinner.setId(BUILDING_SPINNER_ID);
        buildingSpinner.setAdapter(buildingSpinnerAdapter);
    }

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Exhibit clicked = (Exhibit) l.getItemAtPosition(position);
		
		Bundle basket = new Bundle();
		basket.putString("name", clicked.getName());
		basket.putString("description", clicked.getDescription());
		basket.putString("major", clicked.getMajor());
		basket.putString("building", clicked.getBuilding());
		basket.putString("room", clicked.getRoom());
		basket.putString("address", clicked.getAddress());
		basket.putString("url", clicked.getUrl());
		basket.putBoolean("isFavorite", clicked.isFavorite());
		basket.putInt("id", clicked.getId());

		Intent launchInfo = new Intent(this.getActivity(), ExhibitInfoActivity.class);
		launchInfo.putExtras(basket);
		startActivityForResult(launchInfo, REQUEST_CODE);
	}

	


	/** Function called when a spinner item is selected from the filter list **/
	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
		ExhibitsListAdapter adapter = (ExhibitsListAdapter) getListAdapter();
		String constraint = parent.getId() + ":" + parent.getItemAtPosition(pos).toString();
    	this.getListView().smoothScrollToPosition(0);
    	adapter.getFilter().filter(constraint);
    }


	/**
     *  Function called when no spinner item is selected from the filter list.
     *  Just load all the exhibits.
     **/
	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		ExhibitsListAdapter adapter = (ExhibitsListAdapter) getListAdapter();
    	adapter.getFilter().filter(MAJOR_SPINNER_ID + ":" + "All Majors");	
	}
	
	/** Callback method after the Exhibit Information activity closes. **/
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (requestCode == REQUEST_CODE && intent != null) {
			int exhibitId = intent.getIntExtra("exhibitId", -1);
			boolean isFavorite = intent.getBooleanExtra("isFavorite", false);
			
			if( exhibitId != -1 ) {
				MainActivity activity = (MainActivity) getActivity();
				Exhibit exhibitToUpdate = activity.getExhibit(exhibitId);
				if(exhibitToUpdate != null) {
					exhibitToUpdate.setFavorite( isFavorite );
					ExhibitsListAdapter adapter = (ExhibitsListAdapter) getListAdapter();
					adapter.notifyDataSetChanged();
				}
			}
		}
	}
}