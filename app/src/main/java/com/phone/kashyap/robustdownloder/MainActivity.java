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
		final static private String URL_DEFAULT = "http://www.iso.org/iso/annual_report_2009.pdf";

		public MainFragment()
		{
		}

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
						new DownloadTask(progressBar).execute();
					else
						Toast.makeText(getActivity(), "Wifi not connected", Toast.LENGTH_SHORT).show();
				}
			});

			return rootView;
		}
		private class DownloadTask extends AsyncTask<Void, Integer, Void>
		{
			private final String LOG_TAG = DownloadTask.class.getSimpleName();
			private final int BUFFER_SIZE = 1024;
			private final ProgressBar _progressBar;
			private String _fileName;

			private int NOTIFICATION_ID = 1;
			private Notification _notification;
			private NotificationManager _notificationManager;

			public DownloadTask(ProgressBar progressBar)
			{
				_progressBar = progressBar;
				_notificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
			}

			@Override
			protected void onPreExecute()
			{
				super.onPreExecute();
				createNotification("File Downloading..", "");
			}

			@Override
			protected Void doInBackground(Void... voids)
			{
				Log.i(LOG_TAG, "Connecting");
				try
				{
					//Connection Part
					URL defaultUrl = new URL(URL_DEFAULT);
					HttpURLConnection connection = (HttpURLConnection) defaultUrl.openConnection();
					connection.setDoOutput(true);
					connection.setRequestMethod("GET");
					connection.connect();
					int fileSize = connection.getContentLength();
					_progressBar.setMax(fileSize / BUFFER_SIZE);

					//Saving in directory
					File rootDirectory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "Robust Downloader");
					if(!rootDirectory.exists())
						rootDirectory.mkdirs();

					_fileName = URLUtil.guessFileName(URL_DEFAULT, null, MimeTypeMap.getFileExtensionFromUrl(URL_DEFAULT));
					File file = new File(rootDirectory, _fileName);
					file.createNewFile();
					InputStream inputStream = connection.getInputStream();
					FileOutputStream output = new FileOutputStream(file);
					byte[] buffer = new byte[BUFFER_SIZE];
					int byteCount = 0;
					int i = 0;
					while ((byteCount = inputStream.read(buffer)) > 0)
					{
						publishProgress(++i);
						output.write(buffer, 0, byteCount);
					}
					output.close();
				}
				catch (MalformedURLException e)	{e.printStackTrace();}
				catch (IOException e) {e.printStackTrace();}
				return null;
			}

			@Override
			protected void onProgressUpdate(Integer... values)
			{
				super.onProgressUpdate(values);
				//Log.i(LOG_TAG, values[0].toString());
				_progressBar.setProgress(values[0]);
			}

			@Override
			protected void onPostExecute(Void aVoid)
			{
				super.onPostExecute(aVoid);
				Toast.makeText(getActivity(), _fileName + " downloaded!!", Toast.LENGTH_SHORT).show();
			}

			private void createNotification(String contentTitle, String contentText)
			{
				/*NotificationCompat.Builder mBuilder =
						new NotificationCompat.Builder(this)
								.setSmallIcon(R.drawable.ic_launcher)
								.setContentTitle(contentTitle)
								.setContentText(contentText);
				// Creates an explicit intent for an Activity in your app
				Intent resultIntent = new Intent(getActivity(), MainActivity.class);

				// The stack builder object will contain an artificial back stack for the
				// started Activity.
				// This ensures that navigating backward from the Activity leads out of
				// your application to the Home screen.
				TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
				// Adds the back stack for the Intent (but not the Intent itself)
				stackBuilder.addParentStack(ResultActivity.class);
				// Adds the Intent that starts the Activity to the top of the stack
				stackBuilder.addNextIntent(resultIntent);
				PendingIntent resultPendingIntent =
						stackBuilder.getPendingIntent(
								0,
								PendingIntent.FLAG_UPDATE_CURRENT
						);
				mBuilder.setContentIntent(resultPendingIntent);
				NotificationManager mNotificationManager =
						(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
				// mId allows you to update the notification later on.
				mNotificationManager.notify(mId, mBuilder.build());*/

			}
		}

	}

}
