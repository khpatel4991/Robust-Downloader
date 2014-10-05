package com.phone.kashyap.robustdownloder;

import android.app.Activity;
import android.app.DownloadManager;
import android.app.Fragment;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity
{

	final static private String LOG_TAG = MainActivity.class.getSimpleName();
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		if (savedInstanceState == null)
		{
			getFragmentManager().beginTransaction().add(R.id.container, new MainFragment()).commit();
		}
	}

	public static class MainFragment extends Fragment
	{
		final static private String LOG_TAG = MainFragment.class.getSimpleName();
		final static private String FOLDER_NAME = "Robust Downloader";
		final static private String URL_DEFAULT = "http://www.iso.org/iso/annual_report_2009.pdf";
		public MainFragment(){}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
		{
			View rootView = inflater.inflate(R.layout.fragment_main, container, false);

			TextView defaultURL = (TextView) rootView.findViewById(R.id.textView_default);
			defaultURL.setText(URL_DEFAULT);

			Button buttonDefaultDownload = (Button) rootView.findViewById(R.id.button_default_download);
			Button buttonCustomDownload = (Button) rootView.findViewById(R.id.button_custom_download);
			Button buttonDefaultDownloadManager = (Button) rootView.findViewById(R.id.button_default_download_manager);
			Button buttonCustomDownloadManager = (Button) rootView.findViewById(R.id.button_custom_download_manager);

			final EditText editText = (EditText) rootView.findViewById(R.id.editText_URL);
			final ProgressBar progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);

			buttonDefaultDownload.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view)
				{
					Log.i(LOG_TAG, "Clicked Default Download1 Button");
					ConnectivityManager connManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
					NetworkInfo wifiStatus = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
					if(wifiStatus.isConnected())
						new DownloadTask(getActivity(), progressBar).execute(URL_DEFAULT);
					else
						Toast.makeText(getActivity(), "Wifi not connected", Toast.LENGTH_SHORT).show();
				}
			});

			buttonCustomDownload.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view)
				{
					Log.i(LOG_TAG, "Clicked Custom Download1 Button");
					ConnectivityManager connManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
					NetworkInfo wifiStatus = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
					if(wifiStatus.isConnected())
					{
						final String editTextVal = editText.getText().toString();
						String URL_Custom = getProperUrl(editTextVal);
						if(!URL_Custom.equals(null))
							new DownloadTask(getActivity(), progressBar).execute(URL_Custom);
						else
							Toast.makeText(getActivity(), "Invalid URL", Toast.LENGTH_SHORT).show();
					}
					else
						Toast.makeText(getActivity(), "Wifi not connected", Toast.LENGTH_SHORT).show();
				}
			});

			//Button onCLickListeners
			buttonDefaultDownloadManager.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view)
				{
					Log.i(LOG_TAG, "Clicked Default Download2 Button");
					ConnectivityManager connManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
					NetworkInfo wifiStatus = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
					if(wifiStatus.isConnected())
						downloadFile(URL_DEFAULT);
					else
						Toast.makeText(getActivity(), "Wifi not connected", Toast.LENGTH_SHORT).show();

				}
			});

			buttonCustomDownloadManager.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view)
				{
					Log.i(LOG_TAG, "Clicked Custom Download2 Button");
					final String editTextVal = editText.getText().toString();
					ConnectivityManager connManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
					NetworkInfo wifiStatus = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
					if(wifiStatus.isConnected())
						downloadFile(editTextVal);
					else
						Toast.makeText(getActivity(), "Wifi not connected", Toast.LENGTH_SHORT).show();
				}
			});

			return rootView;
		}

		private String getProperUrl(String editTextVal)
		{
			String URL_Custom;
			if(!editTextVal.isEmpty())
			{
				if (!editTextVal.startsWith("http://") && !editTextVal.startsWith("https://"))
					URL_Custom = "http://" + editTextVal;
				else URL_Custom = editTextVal;

				URLUtil urlUtil = new URLUtil();
				if (urlUtil.isHttpUrl(URL_Custom) || urlUtil.isHttpsUrl(URL_Custom)) {return URL_Custom;}
			}
			else
				Toast.makeText(getActivity(), "URL field is Empty.", Toast.LENGTH_SHORT).show();
			return "";
		}

		private void downloadFile(String URL)
		{
			URL = getProperUrl(URL);
			if(URL.isEmpty())
				return;
			DownloadManager.Request request = new DownloadManager.Request(Uri.parse(URL));
			request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
			request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
			String fileName = URLUtil.guessFileName(URL_DEFAULT, null, MimeTypeMap.getFileExtensionFromUrl(URL_DEFAULT));
			request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS + "/" + FOLDER_NAME, fileName);
			DownloadManager downloadManager = (DownloadManager) getActivity().getSystemService(Context.DOWNLOAD_SERVICE);
			downloadManager.enqueue(request);
			Toast.makeText(getActivity(), "Download Started. Check Notification.", Toast.LENGTH_SHORT).show();
		}
	}
}