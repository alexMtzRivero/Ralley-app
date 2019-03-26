package com.example.qrallye;

import android.util.Log;
import com.example.qrallye.Team;

public class SessionMGR {
    private final String TAG = "SessionMGR";
    private Team team;
    private static final SessionMGR ourInstance = new SessionMGR();
    public static SessionMGR getInstance() {
        return ourInstance;
    }

    public CallbackOnTeamFound callbackTeam = new CallbackOnTeamFound() {
        @Override
        public void onTeamFound(Team teamRetrieve) {
            team = teamRetrieve;
            Log.d(TAG, "callbackCall: sessionTeam color"+team.getColor());
        }
    };
    private SessionMGR() {
    }

    public boolean login(String teamName , String password){
        team = null;
        DatabaseMGR.getInstance().getTeam("Catsu");

        if(team.getPassword() == Integer.parseInt(password)){
            return true;
        }
        else{
            return false;
        }


    }
    public interface CallbackOnTeamFound {
        void onTeamFound(Team teamRetrieve);
    }
}
