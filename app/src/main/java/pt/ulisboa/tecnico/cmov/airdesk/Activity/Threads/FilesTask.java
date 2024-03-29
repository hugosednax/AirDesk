package pt.ulisboa.tecnico.cmov.airdesk.Activity.Threads;

import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import pt.ulisboa.tecnico.cmov.airdesk.Activity.FilesActivity;
import pt.ulisboa.tecnico.cmov.airdesk.Application.AirDeskApp;
import pt.ulisboa.tecnico.cmov.airdesk.Exception.WorkspaceNotFoundException;
import pt.ulisboa.tecnico.cmov.airdesk.FileSystem.ADFile;
import pt.ulisboa.tecnico.cmov.airdesk.R;
import pt.ulisboa.tecnico.cmov.airdesk.Workspace.Workspace;

/**
 * Created by hugo__000 on 25/03/2015.
 */
public class FilesTask extends AsyncTask<FilesActivity, Void, List<String>> {

    private FilesActivity activity;
    @Override
    protected List<String> doInBackground(FilesActivity... params) {
        activity = params[0];
        Intent intent = activity.getIntent();
        AirDeskApp airDeskApp = (AirDeskApp) activity.getApplicationContext();
        String nameOfCurrWorkspace = intent.getStringExtra("nameOfWorkspace");
        activity.setTitle("Files of " + nameOfCurrWorkspace);
        boolean isForeign = intent.getBooleanExtra("isForeign",false); //default value is false
        try {
            /*
            Logic and Backend:
            Retrieve the user from the context and then get the current workspace by searching with the name
            Link the ArrayAdapter to list of files of the workspace, this will only display the name of the File thanks
                to the toString override on the ADFile class
            Get the ListView, link it to the ArrayAdapter, allow multiple choices, allow long clicks,
                set itemClick Listener
            */
            if(isForeign)
                activity.setCurrentWorkspace(airDeskApp.getUser().getForeignWorkspaceByName(nameOfCurrWorkspace));
            else{
                activity.setCurrentWorkspace(airDeskApp.getUser().getOwnedWorkspaceByName(nameOfCurrWorkspace));
                airDeskApp.getUser().getOwnedWorkspaceByName(nameOfCurrWorkspace).accessCloudFiles();
            }

        }catch(WorkspaceNotFoundException e){
            Log.d("[AirDesk]", "Workspace not found at FilesTask: " + e.getMessage());
        }
        return null;//activity.getCurrentWorkspace().getFileNames();
    }

    @Override
    protected void onPostExecute(List<String> files) {
        activity.assignArrayAdapter(files);
    }
}
