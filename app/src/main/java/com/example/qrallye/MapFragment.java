package com.example.qrallye;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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
        mListener.mapFragmentInitialisation();
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
                try{
                    String perm = Manifest.permission.ACCESS_FINE_LOCATION;
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M || ContextCompat.checkSelfPermission(getContext(), perm) == PackageManager.PERMISSION_GRANTED) {
                        callback.invoke(origin, true, false);
                    } else {
                        if (!shouldShowRequestPermissionRationale(perm)) {
                            requestPermissions(new String[] {perm}, 1);
                        }
                    }
                }catch(Exception e){
                    Log.e(TAG, "onGeolocationPermissionsShowPrompt: ", e);
                }

            }
        });
        mWebView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                if(QuizMGR.getInstance().getQuizList().size() != 0){
                    addQuizzMarkersToMap();
                }

                if(QuizMGR.getInstance().getListOfOpponentPosition() != null)
                    addOpponentMarkersToMap();
            }
        });

        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setGeolocationEnabled(true);
        webSettings.setAppCacheEnabled(true);
        webSettings.setDatabaseEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);

        FloatingActionButton locateBtn = view.findViewById(R.id.locateBtn);
        locateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWebView.loadUrl("javascript:locateOnce();");
            }
        });


        mWebView.loadUrl("file:///android_asset/index.html");

        return view;
    }

    private void addQuizzMarkersToMap(){
        mWebView.loadUrl("javascript:quizzesGroup.clearLayers();");
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

    public void opponentsPositionRetrieved() {
        if(QuizMGR.getInstance().getListOfOpponentPosition().size() != 0){
            addOpponentMarkersToMap();
        }
    }

    public void quizzesRetrieved(){
        if(QuizMGR.getInstance().getQuizList().size() != 0){
            addQuizzMarkersToMap();
        }
    }

    public void finishedQuizzesRetrieved(){
        addQuizzMarkersToMap();
    }

}
