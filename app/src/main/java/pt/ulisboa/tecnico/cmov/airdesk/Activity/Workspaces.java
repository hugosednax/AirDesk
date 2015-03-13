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


public class Workspaces extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workspaces);

        AirDeskApp airDeskApp = (AirDeskApp) getApplicationContext();
        airDeskApp.setPrefs(getSharedPreferences("user_prefs", MODE_PRIVATE));
        String userEmail = airDeskApp.getPrefs().getString("email_pref","DEFAULT");
        String nick = airDeskApp.getPrefs().getString("nick_pref","DEFAULT");
        airDeskApp.setUser(new User(nick,userEmail));
        Log.d("PREFERENCES WORKSPACES","user: "+nick+" email: "+userEmail);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_workspaces, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void switchToOwnedActivity(View v){
            Intent intent = new Intent(this, OwnedWorkspaces.class);
            startActivity(intent);
    }

    public void switchToForeignActivity(View v){
        Intent intent = new Intent(this, ForeignWorkspaces.class);
        startActivity(intent);
    }

}
