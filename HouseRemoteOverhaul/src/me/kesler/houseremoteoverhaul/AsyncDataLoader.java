package me.kesler.houseremoteoverhaul;

import java.lang.ref.WeakReference;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;

public class AsyncDataLoader extends AsyncQueryHandler {
	
	
	
	private Uri tableURIs[];

	private static final String[] controllerProjection = {
			DBContract.CONTROLLER_ID, DBContract.CONTROLLER_NAME,
			DBContract.CONTROLLER_IMAGE_NAME, DBContract.CONTROLLER_TYPE,
			DBContract.CONTROLLER_IP, DBContract.CONTROLLER_PORT,
			DBContract.CONTROL_PIN_NUMBER };
	private static final String controllerSelection = DBContract.SERVER_IP
			+ "=?";

	private WeakReference<HeadlessFragment> mListener;

	public AsyncDataLoader(ContentResolver cr, HeadlessFragment mListener) {
		super(cr);
		this.mListener = new WeakReference<HeadlessFragment>(mListener);
		tableURIs = new Uri[FragmentType.values().length];
		tableURIs[0] = DBProvider.LIGHTS_URI;
		tableURIs[1] = DBProvider.SWITCHES_URI;
		tableURIs[2] = DBProvider.MEDIA_URI;
		tableURIs[3] = DBProvider.APPLIANCES_URI;
		tableURIs[4] = DBProvider.ROOMS_URI;
	}

	@Override
	protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
		super.onQueryComplete(token, cookie, cursor);
		if (mListener.get() != null)
			mListener.get().onDataLoaded((FragmentType) cookie, cursor);
	}

	public void startLoadingData(FragmentType toFragment, String[] serverIp) {
		startQuery(0, toFragment, tableURIs[toFragment.getValue()],
				controllerProjection, controllerSelection,
				serverIp, null);

	}

}
