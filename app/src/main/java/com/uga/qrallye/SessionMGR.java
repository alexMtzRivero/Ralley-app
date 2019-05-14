package com.uga.qrallye;

import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class SessionMGR {
    private final String TAG = "SessionMGR";
    private Team team;
    private ConnectionActivity toSend;
    private AdminActivity adminActivity;
    private LaunchScreenActivity launchScreenActivity;
    private String waitingPassword;
    private ArrayList<Administrators> adminList;
    private boolean isWaitingForAdminList;
    private static final SessionMGR ourInstance = new SessionMGR();
    private ConnectionType connectionType;
    public static SessionMGR getInstance() {
        return ourInstance;
    }

    private enum ConnectionType{
        ALLREADY_LOGGED, TO_CONNECT
    }


    private SessionMGR() {
    }
    public Team getLogedTeam (){
        return team;
    }
    public void login(String teamName , String password,ConnectionActivity connectionActivity){
        team = null;
        waitingPassword  = password;
        toSend = connectionActivity;
        connectionType = ConnectionType.TO_CONNECT;
        DatabaseMGR.getInstance().getTeam(teamName);
    }

    public void login(String teamName, LaunchScreenActivity activity){
        team = null;
        connectionType = ConnectionType.ALLREADY_LOGGED;
        this.launchScreenActivity = activity;
        DatabaseMGR.getInstance().getTeam(teamName);
    }

    public void  updateCurrentTeam(){
        if(team!=null)
            DatabaseMGR.getInstance().getTeam(team.getName());
    }

    public void onTeamFound(Team teamRetrieve) {
        team = teamRetrieve;

        if(connectionType == ConnectionType.TO_CONNECT && toSend!=null){
            if(team!=null) {
                if (waitingPassword != null)
                    if (team.getPassword() == Long.parseLong(waitingPassword)) {
                        String token = DatabaseMGR.getInstance().newToken();
                        SharedPreferences sp = toSend.getSharedPreferences(toSend.getString(R.string.sharedPreferencesFile), MODE_PRIVATE);
                        SharedPreferences.Editor mEditor =  sp.edit();
                        mEditor.putString("token", token);
                        mEditor.putString(toSend.getString(R.string.teamNamePref), team.getName());
                        mEditor.apply();
                        toSend.goToNext();
                    }
                    else toSend.notifyWrongPasword();
            }
            else toSend.noTeamFound();
            toSend = null;
        }else
            if (connectionType == ConnectionType.ALLREADY_LOGGED && launchScreenActivity != null){
                if(team!=null) {
                    SharedPreferences sp = launchScreenActivity.getSharedPreferences(launchScreenActivity.getString(R.string.sharedPreferencesFile), MODE_PRIVATE);
                    if (team.getToken().equals(sp.getString("token", ""))) {
                            launchScreenActivity.goToNext();
                    }
                    else launchScreenActivity.goToConnectionActivity();
                }
                else launchScreenActivity.goToConnectionActivity();
                launchScreenActivity = null;
            }
    }

    public void requestAdminList(AdminActivity adminActivity){
        this.adminActivity = adminActivity;
        DatabaseMGR.getInstance().getAdmin();
    }

    public void onAdminListFound(ArrayList<Administrators> adminList){
        this.adminList = adminList;
        adminActivity.refreshList(adminList);
    }

    //------------------Team Position MGR---------------------

    public void updatePosition(GeoPoint geoPoint){
        try{
            this.team.setPosition(geoPoint);
        }catch(Exception e){
            Log.e(TAG, "updatePosition: ", e);
        }
    }

    public void sendGeopoint(){
        if(SessionMGR.getInstance().team.getPosition() != null)
            DatabaseMGR.getInstance().pushTeamPosition();
    }


}
