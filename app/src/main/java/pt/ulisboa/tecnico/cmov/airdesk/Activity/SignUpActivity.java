package pt.ulisboa.tecnico.cmov.airdesk.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import pt.ulisboa.tecnico.cmov.airdesk.Application.AirDeskApp;
import pt.ulisboa.tecnico.cmov.airdesk.R;


public class SignUpActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ((AirDeskApp)getApplicationContext()).getWifiHandler().setCurrentActivity(this);
    }

    //called when the button is pressed
    public void saveSettings(View v){
        EditText email = (EditText)findViewById(R.id.inputEmail);
        EditText nick = (EditText)findViewById(R.id.inputName);

        /*gets the App context, gets the sharedPreferences, gets the content inside the editText Views and save them inside the Preferences
        redirect to the WorkspaceTypeActivity*/
        AirDeskApp airDeskApp = (AirDeskApp) getApplicationContext();
        SharedPreferences.Editor prefEditor = airDeskApp.getPrefs().edit();
        prefEditor.putString("email_pref",email.getText().toString());
        prefEditor.putString("nick_pref",nick.getText().toString());
        prefEditor.apply();

        //Parse signUp
        ParseUser user = new ParseUser();
        user.setUsername(nick.getText().toString());
        user.setPassword("DEFAULT");
        user.setEmail(email.getText().toString());

        try {
            user.signUp();
        } catch (ParseException e) {
            Log.d("[AirDesk]", "SignUp didn't succeed: " + e.getMessage());
        }

        Intent intent = new Intent(this, WorkspaceTypeActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sign_up, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return super.onOptionsItemSelected(item);
    }
}
