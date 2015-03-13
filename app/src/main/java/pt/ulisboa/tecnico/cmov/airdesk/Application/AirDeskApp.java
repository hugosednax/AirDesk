package pt.ulisboa.tecnico.cmov.airdesk.Application;

import android.app.Application;
import android.content.SharedPreferences;

import pt.ulisboa.tecnico.cmov.airdesk.User;

/**
 * Created by Toninho on 3/12/2015.
 */
public class AirDeskApp extends Application {

    private User user;
    SharedPreferences prefs;


    public AirDeskApp() {
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public SharedPreferences getPrefs() {
        return prefs;
    }

    public void setPrefs(SharedPreferences prefs) {
        this.prefs = prefs;
    }


}
