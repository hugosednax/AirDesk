package pt.ulisboa.tecnico.cmov.airdesk.Activity;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import pt.ulisboa.tecnico.cmov.airdesk.Application.AirDeskApp;
import pt.ulisboa.tecnico.cmov.airdesk.R;


public class StarterActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_starter);

        AirDeskApp airDeskApp = (AirDeskApp) getApplicationContext();
        //@FT add
        AirDeskApp.getAppContext().getDir("data", AirDeskApp.getAppContext().MODE_PRIVATE);
        //end

        /*Get the prefs, get the user and nick (with the default value of DEFAULT)
        * Check if the user is DEFAULT which means the user has not signed up once yet
        * so redirect do SignUpActivity
        * ELSE
        * go to WorkspaceTypeActivity since the user already signed up
        * */
        airDeskApp.setPrefs(getSharedPreferences("user_prefs", MODE_PRIVATE));
        String userEmail = airDeskApp.getPrefs().getString("email_pref","DEFAULT");
        String nick = airDeskApp.getPrefs().getString("nick_pref","DEFAULT");
        Intent intent;
        if(userEmail.equals("DEFAULT")) {
            intent = new Intent(this, SignUpActivity.class);
            Log.d("PREFERENCES MISSING","user: "+nick+" email: "+userEmail);
        }else{
            intent = new Intent(this, WorkspaceTypeActivity.class);
            Log.d("PREFERENCES HERE","user: "+nick+" email: "+userEmail);
        }
        startActivity(intent);
        finish(); //finish to close this activity and not allow to use the back button to travel to it
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_starter, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }
}
