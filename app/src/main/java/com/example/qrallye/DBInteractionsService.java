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
    Handler handler = new Handler();
    Runnable getOpponentsPositionRunnable;
    Runnable getQuizzesRunnable;
    Runnable getFinishedQuizzesRunnable;

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
        Log.d(TAG, "onStartCommand: ");
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
        handler.removeCallbacksAndMessages(getOpponentsPositionRunnable);
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
        Log.d(TAG, "handleActionGetOpponentsPosition: startRetrieveFromDB");
        QuizMGR.getInstance().retrieveOpponentTeamPositionListFromDB();

        getOpponentsPositionRunnable = new Runnable() {
            @Override
            public void run() {
                if(QuizMGR.getInstance().isWaitingForListOfOpponentPosition()){
                    handler.postDelayed(this, 200);
                }else{
                    try{
                        if(QuizMGR.getInstance().getListOfOpponentPosition() == null){
                            handleActionGetOpponentsPosition();
                        }else{
                            Log.d(TAG, "handleActionGetOpponentsPosition: retrieved");
                            callbacks.opponentsPositionsRetrieved();
                        }
                    }catch(Exception e){
                        Log.e(TAG, "getOpponentsPositionRunnable: ", e);
                    }

                }

            }
        };
        handler.post(getOpponentsPositionRunnable);
    }

    private void handleActionGetQuizzes() {
        Log.d(TAG, "handleActionGetOpponentsPosition: startRetrieveFromDB");
        QuizMGR.getInstance().retrieveQuizList();

        getQuizzesRunnable = new Runnable() {
            @Override
            public void run() {
                if(QuizMGR.getInstance().isWaitingForListOfQuiz()){
                    handler.postDelayed(this, 200);
                }else{
                    try{
                        if(QuizMGR.getInstance().getQuizList() == null){
                            handleActionGetQuizzes();
                        }else{
                            Log.d(TAG, "handleActionGetOpponentsPosition: retrieved");
                            callbacks.quizzesRetrieved();
                        }
                    }catch(Exception e){
                        Log.e(TAG, "getQuizzesRunnable: ", e);
                    }

                }

            }
        };
        handler.post(getOpponentsPositionRunnable);
    }


    private void handleActionGetFinishedQuizzes() {
        Log.d(TAG, "handleActionGetOpponentsPosition: startRetrieveFromDB");
        QuizMGR.getInstance().retrieveFinishedQuizListFromDB();

        getQuizzesRunnable = new Runnable() {
            @Override
            public void run() {
                if(QuizMGR.getInstance().isWaitingForListOfFinishedQuiz()){
                    handler.postDelayed(this, 200);
                }else{
                    try{
                        if(QuizMGR.getInstance().getFinishedQuizList() == null){
                            handleActionGetFinishedQuizzes();
                        }else{
                            Log.d(TAG, "handleActionGetFinishedQuizzes: retrieved");
                            callbacks.finishedQuizzesRetrieved();
                        }
                    }catch(Exception e){
                        Log.e(TAG, "getFinishedQuizzesRunnable: ", e);
                    }

                }

            }
        };
        handler.post(getFinishedQuizzesRunnable);
    }
}
