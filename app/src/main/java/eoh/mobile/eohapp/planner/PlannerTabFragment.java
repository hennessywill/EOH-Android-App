package eoh.mobile.eohapp.planner;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

import eoh.mobile.eohapp.MainActivity;
import eoh.mobile.eohapp.R;
import eoh.mobile.eohapp.exhibits.ExhibitInfoActivity;
import eoh.mobile.eohapp.models.Exhibit;

public class PlannerTabFragment extends ListFragment {
	
	private static final int REQUEST_CODE = 2;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View rootView = inflater.inflate(R.layout.tab_planner, container, false);
		return rootView;
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		MainActivity activity = (MainActivity) getActivity();
		ArrayList<Exhibit> favoriteExhibits = activity.getFavoriteExhibitsList();
		this.setListAdapter( new PlannerListAdapter(activity, favoriteExhibits) );
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
					PlannerListAdapter adapter = (PlannerListAdapter) getListAdapter();
					adapter.reloadFavoritesList();
				}
			}
		}
	}
}