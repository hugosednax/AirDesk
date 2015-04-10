package pt.ulisboa.tecnico.cmov.airdesk.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import pt.ulisboa.tecnico.cmov.airdesk.Application.AirDeskApp;
import pt.ulisboa.tecnico.cmov.airdesk.Exception.CreateWorkspaceException;
import pt.ulisboa.tecnico.cmov.airdesk.R;

public class WorkspaceCreateActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workspace_create);
    }


    public void createWorkspace(View v){
        new Thread(new Runnable() {
            public void run() {
                EditText name = (EditText) findViewById(R.id.nameInput);
                EditText quota = (EditText) findViewById(R.id.quotaInput);
                CheckBox isPublic = (CheckBox) findViewById(R.id.isPublicCheckBox);

                //Call workspace constructor and use the View inputs to fill in its attributes
                AirDeskApp airDeskApp = (AirDeskApp) getApplicationContext();
                try {
                    airDeskApp.getUser().createWorkspace(name.getText().toString(), isPublic.isChecked(), Integer.parseInt(quota.getText().toString()));
                    finish();
                }catch (Exception e) {
                    final Context context = getApplicationContext();
                    final CharSequence text = e.getMessage();
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }}).start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_workspace_create, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }
}
