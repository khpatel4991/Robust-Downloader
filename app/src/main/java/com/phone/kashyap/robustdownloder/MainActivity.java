package com.phone.kashyap.robustdownloder;

import android.app.Activity;
import android.app.Fragment;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.NotificationCompat.WearableExtender;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


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
		public MainFragment(){}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
		{
			View rootView = inflater.inflate(R.layout.fragment_main, container, false);

			ConnectivityManager connManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
			final NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

			Button buttonDefaultDownload = (Button) rootView.findViewById(R.id.button_default_download);
			final ProgressBar progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);

			buttonDefaultDownload.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view)
				{
					Log.i(LOG_TAG, "Clicked Default Download Button");
					if(mWifi.isConnected())
						new DownloadTask(getActivity(), progressBar).execute();
					else
						Toast.makeText(getActivity(), "Wifi not connected", Toast.LENGTH_SHORT).show();
				}
			});
			return rootView;
		}
	}
}