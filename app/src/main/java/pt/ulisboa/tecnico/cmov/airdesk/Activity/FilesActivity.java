package pt.ulisboa.tecnico.cmov.airdesk.Activity;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import pt.ulisboa.tecnico.cmov.airdesk.Application.AirDeskApp;
import pt.ulisboa.tecnico.cmov.airdesk.Exception.WorkspaceNotFoundException;
import pt.ulisboa.tecnico.cmov.airdesk.FileSystem.ADFile;
import pt.ulisboa.tecnico.cmov.airdesk.R;
import pt.ulisboa.tecnico.cmov.airdesk.Workspace.Workspace;


public class FilesActivity extends ActionBarActivity {
    private ArrayAdapter filesAdapter;
    private ListView listView;
    private Workspace currWorkspace;
    private List<String> selectedFiles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_files);

        /*Logic and Backend:
         retrieve the app context and retrieve the name of the current workspace, sent from the previous screen
        */
        selectedFiles = new ArrayList<String>();
        AirDeskApp airDeskApp = (AirDeskApp) getApplicationContext();
        Intent intent = getIntent();
        String nameOfCurrWorkspace = intent.getStringExtra("nameOfWorkspace");
        try {
            /*
            Logic and Backend:
            Retrieve the user from the context and then get the current workspace by searching with the name
            Link the ArrayAdapter to list of files of the workspace, this will only display the name of the File thanks
                to the toString override on the ADFile class
            Get the ListView, link it to the ArrayAdapter, allow multiple choices, allow long clicks,
                set itemClick Listener
            */
            currWorkspace = airDeskApp.getUser().getOwnedWorkspaceByName(nameOfCurrWorkspace);
            filesAdapter = new ArrayAdapter<ADFile>(this, android.R.layout.simple_list_item_1, currWorkspace.getFiles());
            listView = (ListView) findViewById(R.id.listFiles);
            listView.setAdapter(filesAdapter);
            listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
            listView.setLongClickable(true);
            /*Logic: Find the name of the file on the list position of the click*/
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String selectedFromList = listView.getItemAtPosition(position).toString();
                    startViewFile(selectedFromList);
                }
            });
        }catch(WorkspaceNotFoundException e){}

        /*Links the listView to the context Bar*/
        addContextToList(listView, airDeskApp);
    }

    //Behaviour of the ContextBar
    public void addContextToList(final ListView listView, final AirDeskApp airDeskApp){
        listView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {

            /*Logic: When a listView item is LONG clicked, adds it to a list of names*/
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position,
                                                  long id, boolean checked) {
                String selectedFromList =listView.getItemAtPosition(position).toString();
                if(selectedFiles.contains(selectedFromList)) { //why doesnt if(checked) work? xD
                    selectedFiles.remove(selectedFromList);
                }else {
                    selectedFiles.add(selectedFromList);
                }
                mode.invalidate(); //automaticly calls onPrepareActionMode
            }

            /*Logic: When a button from the Bar is clicked, detect which one was clicked and
            select correct behaviour depending if delete or edit*/
            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch (item.getItemId()) {
                    /*Backend: iterate through the list of names of selectedFiles and remove the files from the current workspace */
                    case R.id.deleteFile:
                            for(int i=0; i<selectedFiles.size();i++){
                                try {
                                    currWorkspace.removeFile(selectedFiles.get(i));
                                }catch (Exception e){}
                            }
                        filesAdapter.notifyDataSetChanged(); //warn the adapter that the original array has changed
                        selectedFiles.clear();
                        mode.finish();
                        return true;
                    case R.id.editFile:
                        startEditFile(selectedFiles.get(0));
                        return true;
                    default:
                        return false;
                }
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
               /*Inflate the menu with other options (delete and edit) when something is selected*/
                MenuInflater inflater = mode.getMenuInflater();
                inflater.inflate(R.menu.menu_list_files_selected, menu);
                return true;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                //Shows or hides the editFile button depending on the number of selected files
                if (selectedFiles.size() <= 1){
                    MenuItem item = menu.findItem(R.id.editFile);
                    item.setVisible(true);
                    return true;
                } else {
                    MenuItem item = menu.findItem(R.id.editFile);
                    item.setVisible(false);
                    return true;
                }
            }
        });
    }

    /*Logic: Start a FileViewActivity, and send the name of the current workspace and file clicked on*/
    public void startViewFile(String selectedFile){
        Intent intent = new Intent(this, FileViewActivity.class);
        intent.putExtra("nameOfWorkspace",currWorkspace.getName());
        intent.putExtra("nameOfFile",selectedFile);
        startActivity(intent);
    }

    /*Logic: Start a FileEditActivity, and send the name of the current workspace and file clicked on*/
    public void startEditFile(String selectedFile){
        Intent intent = new Intent(this, FileEditActivity.class);
        intent.putExtra("nameOfWorkspace",currWorkspace.getName());
        intent.putExtra("nameOfFile",selectedFile);
        startActivity(intent);
    }

    /*Normal inflate, only has the new Option*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_list_files, menu);
        return true;
    }

    /*Once clicked on the New Button, go to the File Create Activity and send the name of the current Workspace*/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.createFile) {
            Intent intent = new Intent(this, FileCreateActivity.class);
            intent.putExtra("nameOfWorkspace",currWorkspace.getName());
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (filesAdapter !=null){
            filesAdapter.notifyDataSetChanged();
        }
    }
}
