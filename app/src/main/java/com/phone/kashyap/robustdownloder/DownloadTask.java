package com.phone.kashyap.robustdownloder;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.webkit.URLUtil;
import android.widget.ProgressBar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class DownloadTask extends AsyncTask<String, Integer, Void>
{
	private final String LOG_TAG = DownloadTask.class.getSimpleName();
	private Context _context;
	private final int BUFFER_SIZE = 1024;
	private final ProgressBar _progressBar;
	private String _fileName;
	private int _fileSize;
	private double _fileSizeInMB;
	final static private String FOLDER_NAME = "Robust Downloader";
	private int NOTIFICATION_ID = 1;
	private Notification _notification;
	private NotificationManager _notificationManager;
	private NotificationCompat.Builder _builder;


	public DownloadTask(Context context, ProgressBar progressBar)
	{
		_context = context;
		_progressBar = progressBar;
		_builder = new NotificationCompat.Builder(_context);
		_notificationManager = (NotificationManager) _context.getSystemService(Context.NOTIFICATION_SERVICE);
	}

	@Override
	protected void onPreExecute()
	{
		super.onPreExecute();
		createNotification("File Downloading..", "");
	}

	@Override
	protected Void doInBackground(String... strings)
	{
		Log.i(LOG_TAG, "Connecting");
		try
		{
			//Connection Part
			URL defaultUrl = new URL(strings[0]);
			HttpURLConnection connection = (HttpURLConnection) defaultUrl.openConnection();
			connection.setDoOutput(true);
			connection.setRequestMethod("GET");
			connection.connect();
			_fileSize = connection.getContentLength();
			_fileSizeInMB = (double) _fileSize / BUFFER_SIZE / BUFFER_SIZE;
			_progressBar.setMax((int) Math.round(_fileSizeInMB));

			//Saving in directory
			File rootDirectory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), FOLDER_NAME);
			if(!rootDirectory.exists())
				rootDirectory.mkdirs();

			_fileName = URLUtil.guessFileName(strings[0], null, MimeTypeMap.getFileExtensionFromUrl(strings[0]));
			File file = new File(rootDirectory, _fileName);
			file.createNewFile();
			InputStream inputStream = connection.getInputStream();
			FileOutputStream output = new FileOutputStream(file);
			byte[] buffer = new byte[BUFFER_SIZE];
			int byteCount = 0;
			int i = 0;
			while ((byteCount = inputStream.read(buffer)) > 0)
			{
				if(i % BUFFER_SIZE == 0 || i > _fileSize - 3)
					publishProgress(i / BUFFER_SIZE);
				++i;
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
		Log.i(LOG_TAG, values[0].toString());
		_progressBar.setProgress(values[0]);
		_builder.setContentText(values[0].toString() + "/" + String.valueOf(_fileSizeInMB) + "MB");
		_notificationManager.notify(NOTIFICATION_ID, _builder.build());
	}

	@Override
	protected void onPostExecute(Void aVoid)
	{
		super.onPostExecute(aVoid);
		Log.i(LOG_TAG, "Download Complete");
		_builder.setOngoing(false);
		_builder.setContentTitle("Download Complete.");
		_builder.setContentText("Done.");
		_builder.setTicker("Download Finished.");
		_notificationManager.notify(NOTIFICATION_ID, _builder.build());
	}

	private void createNotification(String contentTitle, String contentText)
	{
		_builder.setOngoing(true);
		_builder.setContentTitle(contentTitle);
		_builder.setContentText(contentText);
		_builder.setSmallIcon(R.drawable.ic_launcher);

		_notification = _builder.build();
		_notificationManager = (NotificationManager) _context.getSystemService(_context.NOTIFICATION_SERVICE);
		_notificationManager.notify(NOTIFICATION_ID, _notification);
	}
}