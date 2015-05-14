package pt.ulisboa.tecnico.cmov.airdesk.Activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import pt.ulisboa.tecnico.cmov.airdesk.Application.AirDeskApp;
import pt.ulisboa.tecnico.cmov.airdesk.Exception.FileNotFoundException;
import pt.ulisboa.tecnico.cmov.airdesk.Exception.WorkspaceNotFoundException;
import pt.ulisboa.tecnico.cmov.airdesk.FileSystem.ADFile;
import pt.ulisboa.tecnico.cmov.airdesk.R;
import pt.ulisboa.tecnico.cmov.airdesk.User.User;


public class FileViewActivity extends ActionBarActivity {

    ADFile currFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_view);

        /*Logic and Backend:
         retrieve the app context and retrieve the name of the current workspace and current File, sent from the previous screen
        */
        AirDeskApp airDeskApp = (AirDeskApp) getApplicationContext();
        Intent intent = getIntent();
        String nameOfCurrWorkspace = intent.getStringExtra("nameOfWorkspace");
        String nameOfCurrFile = intent.getStringExtra("nameOfFile");
        String content = "";
        boolean isForeign = intent.getBooleanExtra("isForeign",false);
        try {
             /*
            Logic and Backend:
            Retrieve the user from the context and then get the current workspace by searching with the name and get the current Name by the name
            */
            if(isForeign)
                content = airDeskApp.getUser().getForeignWorkspaceByName(nameOfCurrWorkspace).getFileContent(nameOfCurrFile);
            else {
                content = airDeskApp.getUser().getOwnedWorkspaceByName(nameOfCurrWorkspace).getFileContent(nameOfCurrFile);
            }
            //Read text from file

        } catch (FileNotFoundException | WorkspaceNotFoundException e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        //Find the view by its id
        TextView tv = (TextView)findViewById(R.id.FileContent);
        //Set the text
        tv.setText(content);
        airDeskApp.getWifiHandler().setCurrentActivity(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_view_file, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}
