package com.phone.kashyap.robustdownloder;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.widget.Toast;

public class WifiReceiver extends BroadcastReceiver
{
	final static private String LOG_TAG = WifiReceiver.class.getSimpleName();

	public WifiReceiver(){}

	@Override
	public void onReceive(Context context, Intent intent)
	{
		if(intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION))
		{
			NetworkInfo networkInfo =
					intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
			if(networkInfo.isConnected())
			{
				Toast.makeText(context, "Connected to " + String.valueOf(networkInfo.getExtraInfo()) + ". Download will start if any.", Toast.LENGTH_SHORT).show();
			}
		}
		else if(intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION))
		{
			NetworkInfo networkInfo =
					intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
			if(networkInfo.getType() == ConnectivityManager.TYPE_WIFI && !networkInfo.isConnected())
			{
				Toast.makeText(context, "Disconnected from Wifi. Download will pause if any!", Toast.LENGTH_SHORT).show();
			}
		}

	}
}