package pt.ulisboa.tecnico.cmov.airdesk.Activity;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;

import pt.ulisboa.tecnico.cmov.airdesk.Application.AirDeskApp;
import pt.ulisboa.tecnico.cmov.airdesk.Exception.WorkspaceNotFoundException;
import pt.ulisboa.tecnico.cmov.airdesk.R;
import pt.ulisboa.tecnico.cmov.airdesk.Workspace.Workspace;


public class FileCreateActivity extends ActionBarActivity {

    AirDeskApp airDeskApp;
    String nameOfCurrWorkspace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_file);

        /*Logic and Backend:
         retrieve the app context and retrieve the name of the current workspace, sent from the previous screen
        */
        airDeskApp = (AirDeskApp) getApplicationContext();
        Intent intent = getIntent();
        nameOfCurrWorkspace = intent.getStringExtra("nameOfWorkspace");
    }

    /*
    Logic and Backend:
    Get the input of the ListView for the file name, create the file on the current Workspace and go back to
    the previous activity
    */
    public void createNewFile(View view){
        EditText listView = (EditText) findViewById(R.id.FileName);
        try {
            airDeskApp.getUser().createFile(nameOfCurrWorkspace,listView.toString());
        }catch(Exception e){
            Log.d("EXCEPTION",e.toString());
        }
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_new_file, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
