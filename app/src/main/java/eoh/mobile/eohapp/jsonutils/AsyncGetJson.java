package eoh.mobile.eohapp.jsonutils;

import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * @author willhennessy
 */
public class AsyncGetJson extends AsyncTask<String, Void, Integer> {
	
	private static final int JSON_SUCCESS = 0x0;
	private static final int JSON_FAIL = 0x1;
	
	private String json_url;
	private AsyncJsonListener callback;
	private ProgressBar progress;
	private JSONObject json;
	
	public AsyncGetJson(AsyncJsonListener callback, String json_url, ProgressBar progress) {
		this.callback = callback;
		this.json_url = json_url;
		this.progress = progress;
	}
	
	@Override
	protected void onPreExecute() {
		if(progress != null)
			progress.setVisibility(View.VISIBLE);
	}

    /*
     * Opens a new input stream and reads the JSON file at json_url.
     */
	@Override
	protected Integer doInBackground(String... arg0) {
		
		try {
			URL source = new URL(json_url);
			
			BufferedReader in = new BufferedReader( new InputStreamReader( source.openStream() ) );
			String json_str = "";
			String line;
			while( (line = in.readLine()) != null )
				json_str += line;
			in.close();

			json = new JSONObject( json_str );
		}
		catch(Exception e) {
			e.printStackTrace();
			return JSON_FAIL;
		}
		
		return JSON_SUCCESS;
	}
	

    /*
     * Checks that the AsyncTask did not fail and then passes the received JSON list
     * back to the AsyncJsonListener that requested it.
     */
	@Override
	protected void onPostExecute(Integer result) {
		super.onPostExecute(result);
		
		if(result == JSON_FAIL)
			json = null;
		
		if(progress != null)
			progress.setVisibility(View.GONE);
		
		callback.onJsonReceived(json);
	}
}
