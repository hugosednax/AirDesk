package pt.ulisboa.tecnico.cmov.airdesk.Activity;

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

public class WorkspaceCreate extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workspace_create);
    }


    public void createWorkspace(View v){
        EditText name = (EditText)findViewById(R.id.nameInput);
        EditText quota = (EditText)findViewById(R.id.quotaInput);
        CheckBox isPublic = (CheckBox)findViewById(R.id.isPublicCheckBox);

        AirDeskApp airDeskApp = (AirDeskApp) getApplicationContext();
        try {
            airDeskApp.getUser().createWorkspace(name.getText().toString(), isPublic.isChecked(), Integer.parseInt(quota.getText().toString()));
        }catch(CreateWorkspaceException e){
            Toast.makeText(this.getApplicationContext(),"Can't create it (need proper handle)", Toast.LENGTH_SHORT);
        }
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_workspace_create, menu);
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
}
