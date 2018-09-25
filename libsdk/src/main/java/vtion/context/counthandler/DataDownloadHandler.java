package vtion.context.counthandler;

import android.content.Context;

/*
    DataDownloadHandler - class to handle data fetch from backend services
 */
public class DataDownloadHandler {

    private Context mContext = null;

    public DataDownloadHandler(Context ctx) {
        this.mContext = ctx;
    }

    public void startDownloadProcess() {
        DataSyncReceiver.doCombineGetRequest(mContext);
    }

    public void stopDownloadProcess() {
    }
}