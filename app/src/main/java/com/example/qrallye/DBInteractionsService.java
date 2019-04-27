package com.example.qrallye;

import android.app.IntentService;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * helper methods.
 */
public class DBInteractionsService extends IntentService {
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    public static final String ACTION_getOpponentsPosition = "com.example.qrallye.action.getOpponentsPosition";
    public static final String ACTION_getQuizzes = "com.example.qrallye.action.getQuizzes";
    public static final String ACTION_getFinishedQuizzes = "com.example.qrallye.action.getFinishedQuizzes";
    private static final String TAG = "DBInteractionsService";
    private ServiceCallbacks callbacks;
    private final IBinder mBinder = new LocalBinder();
    private final Handler handler = new Handler();
    private Runnable getOpponentsPositionRunnable;
    private Runnable getQuizzesRunnable;
    private Runnable getFinishedQuizzesRunnable;

    public DBInteractionsService() {
        super("DBInteractionsService");
    }

    public class LocalBinder extends Binder {
        DBInteractionsService getService() {
            return DBInteractionsService.this;
        }
    }

    @Override
    protected void onHandleIntent(Intent intent) {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            final String action = intent.getAction();
            if (action != null) {
                switch (action){
                    case ACTION_getOpponentsPosition:
                        handleActionGetOpponentsPosition();
                        break;
                    case ACTION_getQuizzes:
                        handleActionGetQuizzes();
                        break;
                    case ACTION_getFinishedQuizzes:
                        handleActionGetFinishedQuizzes();
                        break;
                }
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        handler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    public void setCallbacks(ServiceCallbacks callbacks) {
        this.callbacks = callbacks;
    }

    private void handleActionGetOpponentsPosition() {
        QuizMGR.getInstance().retrieveOpponentTeamPositionListFromDB();

        // Obligatoire pour la requete au bout de 10 secondes
        final Runnable dbRequestRunnable = new Runnable() {
            @Override
            public void run() {
                handleActionGetOpponentsPosition();
            }
        };

        getOpponentsPositionRunnable = new Runnable() {
            @Override
            public void run() {
                if(QuizMGR.getInstance().isWaitingForListOfOpponentPosition()){
                    handler.postDelayed(getOpponentsPositionRunnable, 100);
                }else{
                    try{
                        Log.d(TAG, "handleActionGetOpponentsPosition: retrieved");
                        callbacks.opponentsPositionsRetrieved();
                    }catch(Exception e){
                        Log.e(TAG, "getOpponentsPositionRunnable: ", e);
                    }
                    handler.postDelayed(dbRequestRunnable, 10000);
                }

            }
        };
        handler.post(getOpponentsPositionRunnable);
    }

    private void handleActionGetQuizzes() {
        QuizMGR.getInstance().retrieveQuizList();

        getQuizzesRunnable = new Runnable() {
            @Override
            public void run() {
                if(QuizMGR.getInstance().isWaitingForListOfQuiz()){
                    handler.postDelayed(getQuizzesRunnable, 100);
                }else{
                    try{
                        callbacks.quizzesRetrieved();
                    }catch(Exception e){
                        Log.e(TAG, "getQuizzesRunnable: ", e);
                    }
                }

            }
        };
        handler.post(getQuizzesRunnable);
    }


    private void handleActionGetFinishedQuizzes() {
        QuizMGR.getInstance().retrieveFinishedQuizListFromDB();

        getFinishedQuizzesRunnable = new Runnable() {
            @Override
            public void run() {
                if(QuizMGR.getInstance().isWaitingForListOfFinishedQuiz()){
                    handler.postDelayed(getFinishedQuizzesRunnable, 100);
                }else{
                    try{
                        callbacks.finishedQuizzesRetrieved();
                    }catch(Exception e){
                        Log.e(TAG, "getFinishedQuizzesRunnable: ", e);
                    }

                }

            }
        };
        handler.post(getFinishedQuizzesRunnable);
    }
}
