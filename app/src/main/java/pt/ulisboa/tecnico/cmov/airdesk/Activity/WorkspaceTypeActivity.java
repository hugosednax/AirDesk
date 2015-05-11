package pt.ulisboa.tecnico.cmov.airdesk.Activity;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.content.Intent;

import pt.ulisboa.tecnico.cmov.airdesk.Application.AirDeskApp;
import pt.ulisboa.tecnico.cmov.airdesk.R;
import pt.ulisboa.tecnico.cmov.airdesk.User.User;


public class WorkspaceTypeActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workspaces);

        /*If it reached this activity either the player just signed up and was redirected OR
        * was directly redirected from StarterActivity since it has done sign up previously
        *
        * So we can know for sure that there is filled values for the nick and email, so
        * we create a User object with these informations
        * */
        AirDeskApp airDeskApp = (AirDeskApp) getApplicationContext();
        airDeskApp.setPrefs(getSharedPreferences("user_prefs", MODE_PRIVATE));
        String userEmail = airDeskApp.getPrefs().getString("email_pref","DEFAULT");
        String nick = airDeskApp.getPrefs().getString("nick_pref","DEFAULT");
        User user = new User(nick, userEmail, airDeskApp.getWifiHandler());
        airDeskApp.setUser(user);
        airDeskApp.getWifiHandler().setCurrentActivity(this);
        airDeskApp.getWifiHandler().setMyUser(user);
        airDeskApp.getWifiHandler().wifiOn();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_workspaces, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    //Pressed OwnedWorkspace button
    public void switchToOwnedActivity(View v){
            Intent intent = new Intent(this, OwnedWorkspacesActivity.class);
            startActivity(intent);
    }

    //Pressed ForeignWorkspace button
    public void switchToForeignActivity(View v){
        Intent intent = new Intent(this, ForeignWorkspacesActivity.class);
        startActivity(intent);
    }

}
