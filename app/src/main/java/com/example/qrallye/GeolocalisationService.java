package com.example.qrallye;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * helper methods.
 */
public class GeolocalisationService extends IntentService {

    private static final String TAG = "GeolocalisationService";
    private Handler handler = new Handler();

    public GeolocalisationService() {
        super("GeolocalisationService");
    }


    @SuppressLint("MissingPermission")
    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {

            handler.post(periodicLocalisation());

        }
    }

    private Runnable periodicLocalisation(){
        return new Runnable() {
            @Override
            public void run() {
                try{
                    if(SessionMGR.getInstance().getLogedTeam().getStartTimer() != null
                            && SessionMGR.getInstance().getLogedTeam().getEndTimer() == null){
                        SessionMGR.getInstance().sendGeopoint();
                    }
                }catch(Exception e){
                    Log.e(TAG, "run: ", e);
                }
                handler.postDelayed(this, 10000);
            }
        };
    }


}
