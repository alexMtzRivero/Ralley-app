package com.example.qrallye;

import android.util.Log;
import com.example.qrallye.Team;

import static com.example.qrallye.SessionMGR.*;

public class SessionMGR {
    private final String TAG = "SessionMGR";
    private Team team;
    private ConnectionActivity toSend;
    private String waitingPassword;
    private static final SessionMGR ourInstance = new SessionMGR();
    public static SessionMGR getInstance() {
        return ourInstance;
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
        DatabaseMGR.getInstance().getTeam(teamName);
    }
    public void  updateCurrentTeam(){
        if(team!=null)
            DatabaseMGR.getInstance().getTeam(team.getName());
    }

    public void onTeamFound(Team teamRetrieve) {
        team = teamRetrieve;

        if(toSend!=null){
            if(team!=null) {
                if (waitingPassword != null)
                    if (team.getPassword() == Long.parseLong(waitingPassword))
                        toSend.goToNext();
                    else toSend.notifyWrongPasword();
            }
            else toSend.notifyWrongUserName();

            toSend = null;
        }
    }


}
