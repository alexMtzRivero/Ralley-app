package com.example.qrallye;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ConsoleMessage;
import android.webkit.GeolocationPermissions;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.google.android.gms.tasks.Task;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentCallback} interface
 * to handle interaction events.
 * Use the {@link MapFragment#} factory method to
 * create an instance of this fragment.
 */
public class MapFragment extends Fragment {

    private static final String TAG = "MapFragment";
    private WebView mWebView;

    private FragmentCallback mListener;

    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_map, container, false);
        mWebView = view.findViewById(R.id.mapWebView);
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                android.util.Log.d("WebView", consoleMessage.message());
                return true;
            }

            @Override
            public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
                // Geolocation permissions coming from this app's Manifest will only be valid for devices with
                // API_VERSION < 23. On API 23 and above, we must check for permissions, and possibly
                // ask for them.
                String perm = Manifest.permission.ACCESS_FINE_LOCATION;
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M || ContextCompat.checkSelfPermission(getContext(), perm) == PackageManager.PERMISSION_GRANTED) {
                    // we're on SDK < 23 OR user has already granted permission
                    callback.invoke(origin, true, false);
                } else {
                    if (!shouldShowRequestPermissionRationale(perm)) {
                        // ask the user for permission
                        requestPermissions(new String[] {perm}, 1);
                    }
                }
            }
        });
        mWebView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                new showQuizPositionsTask().execute();
                new getFinishedQuizListTask().execute();
                new getOpponentTeamsPositionTask().execute();
            }
        });

        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setGeolocationEnabled(true);
        webSettings.setAppCacheEnabled(true);
        webSettings.setDatabaseEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);


        mWebView.loadUrl("file:///android_asset/index.html");

        return view;
    }

    private class showQuizPositionsTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            while(QuizMGR.getInstance().isWaitingForListOfQuiz()){
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
        @Override
        protected void onPostExecute(String result) {
            if(QuizMGR.getInstance().getQuizList() == null){
                new showQuizPositionsTask().execute();
                return;
            }
            try{
                addQuizzMarkersToMap();
            }catch(Exception e){
                Log.e(TAG, "onPostExecute: ", e);
            }

        }
    }

    private void addQuizzMarkersToMap(){
        mWebView.loadUrl("javascript:quizzesGroup.clearLayers();");
        if(QuizMGR.getInstance().getFinishedQuizList() == null){
            mWebView.loadUrl("javascript:quizzesGroup.clearLayers();");
            for (Quiz quiz : QuizMGR.getInstance().getQuizList()) {
                String pos ="["+quiz.getPosition().getLatitude()+","+quiz.getPosition().getLongitude()+"]";
                String add = "quizzesGroup.addLayer( new L.Marker("+pos+", {icon: todoIcon}));";
                mWebView.loadUrl("javascript:"+add);
            }
        }
        else{
            ArrayList<Quiz> quizList = QuizMGR.getInstance().getQuizList();
            String script;
            for(Quiz quiz : quizList){
                String pos ="["+quiz.getPosition().getLatitude()+","+quiz.getPosition().getLongitude()+"]";
                if(finishedQuizListContainsGivenId(quiz.getId())){
                    script = "quizzesGroup.addLayer( new L.Marker("+pos+", {icon: doneIcon}).bindPopup(\""+quiz.getNomQuiz()+"\"));";
                }else{
                    script = "quizzesGroup.addLayer( new L.Marker("+pos+", {icon: todoIcon}).bindPopup(\""+quiz.getNomQuiz()+"\"));";
                }
                mWebView.loadUrl("javascript:"+script);
            }
        }
    }

    private void addOpponentMarkersToMap() {
        mWebView.loadUrl("javascript:opponentsGroup.clearLayers();");
        for (Team team : QuizMGR.getInstance().getListOfOpponentPosition()) {
            String pos ="["+team.getPosition().getLatitude()+","+team.getPosition().getLongitude()+"]";
            String add = "opponentsGroup.addLayer( new L.Marker("+pos+", {icon: opponentIcon}).bindPopup(\""+team.getName()+"\"));";
            mWebView.loadUrl("javascript:"+add);
        }
    }

    private boolean finishedQuizListContainsGivenId(String id){
        for (Quiz quiz : QuizMGR.getInstance().getFinishedQuizList()){
            if(quiz.getId().equals(id)) return true;
        }
        return false;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FragmentCallback) {
            mListener = (FragmentCallback) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private class getFinishedQuizListTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            QuizMGR.getInstance().retrieveFinishedQuizListFromDB();
        }

        @Override
        protected String doInBackground(String... strings) {
            while(QuizMGR.getInstance().isWaitingForListOfFinishedQuiz()){
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
        @Override
        protected void onPostExecute(String result) {
            if(QuizMGR.getInstance().getFinishedQuizList() == null){
                QuizMGR.getInstance().setWaitingForListOfFinishedQuizDone(true);
                new getFinishedQuizListTask().execute();
                return;
            }
            try{
                if(QuizMGR.getInstance().getQuizList() != null)
                    addQuizzMarkersToMap();
            }catch(Exception e){
                Log.e(TAG, "onPostExecute: ", e);
            }
        }
    }

    private class getOpponentTeamsPositionTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            QuizMGR.getInstance().retrieveOpponentTeamPositionListFromDB();
        }

        @Override
        protected String doInBackground(String... strings) {
            while(QuizMGR.getInstance().isWaitingForListOfOpponentPosition()){
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
        @Override
        protected void onPostExecute(String result) {
            if(QuizMGR.getInstance().getListOfOpponentPosition() != null){
                try{
                    addOpponentMarkersToMap();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            try{
                                new getOpponentTeamsPositionTask().execute();
                            }catch(Exception e){
                                Log.e(TAG, "asynchronous retrieve of opponent position: ", e);
                            }
                        }
                    }, 10000);
                }catch(Exception e){
                    Log.e(TAG, "onPostExecute: ", e);
                }
            }
        }
    }

}
