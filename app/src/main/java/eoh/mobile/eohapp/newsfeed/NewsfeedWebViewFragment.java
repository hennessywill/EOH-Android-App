package eoh.mobile.eohapp.newsfeed;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ProgressBar;

import eoh.mobile.eohapp.R;

public class NewsfeedWebViewFragment extends Fragment {
	
	private WebView mWebView;
	private ProgressBar progress;
	private Button backButton;

	@SuppressLint("SetJavaScriptEnabled")
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View mainView = inflater.inflate(R.layout.tab_newsfeed, container, false);
		mWebView = (WebView) mainView.findViewById(R.id.twitter_web_view);
		progress = (ProgressBar) mainView.findViewById(R.id.twitter_progress);
		backButton = (Button) mainView.findViewById(R.id.twitter_web_view_back_btn);
		
		backButton.setOnClickListener(new Button.OnClickListener() {
		    public void onClick(View v) {
	            mWebView.goBack();
		    }
		});
		
        String twitterBaseUrl = "https://twitter.com/IllinoisEOH/";

        mWebView.setWebViewClient(new TwitterWebViewClient());
	    mWebView.getSettings().setDomStorageEnabled(true);
        mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.loadUrl(twitterBaseUrl);
				
		return mainView;
	}
	
	
	/**
     * Class to handle progress bar and back button of our twitter webview
     **/
    private class TwitterWebViewClient extends WebViewClient {
		@Override
		public void onPageStarted(WebView webView, String url, Bitmap favicon) {
			progress.setVisibility(View.VISIBLE);
		}
		@Override
		public void onPageFinished(WebView webView, String url) {
			progress.setVisibility(View.GONE);
			
			if(webView.canGoBack())
				backButton.setVisibility(View.VISIBLE);
			else
				backButton.setVisibility(View.GONE);
		}
    }
    
}