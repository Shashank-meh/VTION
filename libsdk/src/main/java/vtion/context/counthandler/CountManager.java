package vtion.context.counthandler;

import android.content.Context;

/*
	CountManager - handler class to manage data sync of upload and download of data
 */
public class CountManager {

	private Context mContext = null;

	public static boolean DEBUG = false;

	private DataDownloadHandler downloadMgrObj = null;
	private DataUploadHandler uploadMgrObj = null;

	public void setDebugCount(boolean debug) {
		DEBUG = debug;
	}

	public CountManager(Context ctx) {
		this.mContext = ctx;
	}

	public void startCountManager() {
		try {
			uploadMgrObj = new DataUploadHandler(mContext);
			uploadMgrObj.startUploadProcess();

			downloadMgrObj = new DataDownloadHandler(mContext);
			downloadMgrObj.startDownloadProcess();
		} catch (Exception e) {}
	}

	public void stopCountManager() {
		try {
			uploadMgrObj.stopUploadProcess();
			uploadMgrObj = null;

			downloadMgrObj.stopDownloadProcess();
			downloadMgrObj = null;
		} catch (Exception e) {}
	}
}
