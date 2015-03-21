package pt.ulisboa.tecnico.cmov.airdesk.Application;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import pt.ulisboa.tecnico.cmov.airdesk.User.User;

/**
 * Created by Toninho on 3/12/2015.
 */
public class AirDeskApp extends Application {

    public static final String LOG_TAG = "[AirDesk]";

    private User user;
    SharedPreferences prefs;
    private static Context context;


    public AirDeskApp() {
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        AirDeskApp.context = getApplicationContext();
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

    public static Context getAppContext() {
        return AirDeskApp.context;
    }


}
