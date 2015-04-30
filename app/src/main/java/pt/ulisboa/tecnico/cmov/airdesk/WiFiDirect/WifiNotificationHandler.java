package pt.ulisboa.tecnico.cmov.airdesk.WiFiDirect;

import android.app.Activity;

/**
 * Created by Filipe Teixeira on 30/04/2015.
 */
public class WifiNotificationHandler {
    Activity currentActivity;

    public WifiNotificationHandler(Activity currentActivity){
        this.currentActivity = currentActivity;
    }

    public void setCurrentActivity(Activity currentActivity){
        this.currentActivity = currentActivity;
    }

    protected void notifyWifi(boolean isOn){

    }
}
