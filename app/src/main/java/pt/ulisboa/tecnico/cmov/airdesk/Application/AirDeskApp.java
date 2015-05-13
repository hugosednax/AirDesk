package pt.ulisboa.tecnico.cmov.airdesk.Application;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import pt.ulisboa.tecnico.cmov.airdesk.User.User;
import pt.ulisboa.tecnico.cmov.airdesk.WiFiDirect.WifiNotificationHandler;
/**
 * Created by Toninho
 */
public class AirDeskApp extends Application {
    //region Class Variables
    public static final String LOG_TAG = "[AirDesk]";

    private User user;
    SharedPreferences prefs;
    private static Context context;
    private WifiNotificationHandler wifiHandler;
    //endregion

    //region Constructors
    public AirDeskApp() {
        super();
    }
    //endregion

    //region Android Methods
    @Override
    public void onCreate() {
        super.onCreate();
        this.context = getApplicationContext();
        wifiHandler = new WifiNotificationHandler(getApplicationContext());
        //wifiHandler.wifiOn(); wifi is turned on in the WorkspaceTypeActivity
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
    //endregion

    //region Getters
    public User getUser() {
        return user;
    }

    public WifiNotificationHandler getWifiHandler() { return wifiHandler; }

    public SharedPreferences getPrefs() {
        return prefs;
    }

    public static Context getAppContext() {
        return AirDeskApp.context;
    }
    //endregion

    //region Setters
    public void setUser(User user) {
        this.user = user;
    }

    public void setPrefs(SharedPreferences prefs) {
        this.prefs = prefs;
    }
    //endregion
}
