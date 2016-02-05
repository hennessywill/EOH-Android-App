package eoh.mobile.eohapp.jsonutils;

import org.json.JSONObject;

/** 
 *  @author Will Hennessy
 *
 *  An interface that can be used to listen for the completion of a AsyncGetJson task.
 *  Use onJsonReceived(JSONObject json) in a list fragment to access the JSON and populate UI
 */

public interface AsyncJsonListener {

	public void onJsonReceived( JSONObject json );
	
}
