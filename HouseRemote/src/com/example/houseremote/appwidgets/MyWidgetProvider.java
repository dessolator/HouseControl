package com.example.houseremote.appwidgets;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

import com.example.houseremote.R;
import com.example.houseremote.interfaces.SocketProvider;
import com.example.houseremote.network.NetworkSenderThread;
import com.example.houseremote.network.SwitchPacket;

public class MyWidgetProvider extends AppWidgetProvider implements SocketProvider {

	private static String SOME_ACTION = "SOME ACTION";
	Socket mSocket;
	@SuppressLint("NewApi")
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		super.onUpdate(context, appWidgetManager, appWidgetIds);
		for (int widg : appWidgetIds) {
			Log.d("MOPOOOOOO", "CALLING UPDATE");
			RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
			views.setTextViewText(R.id.widgetItemText, "lightswitch");
			views.setTextViewCompoundDrawables(R.id.widgetItemText, 0, context.getResources().getIdentifier(
					"drawable/" + "light_on", null, context.getPackageName()), 0, 0);

			Intent i = new Intent(context, MyWidgetProvider.class);
			i.setAction(SOME_ACTION);
			PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, i, 0);
			views.setOnClickPendingIntent(R.id.widgetItemText, pendingIntent);
			appWidgetManager.updateAppWidget(widg, views);

		}
	}

	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);
		if (intent.getAction().equals(SOME_ACTION)) {
			Log.d("MOOOO", "LOOOOLOL");
				NetworkSenderThread mSender = new NetworkSenderThread(this);
				mSender.start();
				mSender.addToQueue(new SwitchPacket(1, false));
//				mSender.registerKill();
		}
	}

	synchronized public Socket acquireSocket(int port) throws UnknownHostException, IOException {
		if ((mSocket == null) || mSocket.isClosed()) {

				mSocket = new Socket(InetAddress.getByName("192.168.1.101"), port);
			
		}
		return mSocket;
	}

}