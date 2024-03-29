package pt.ulisboa.tecnico.cmov.airdesk.Activity;

import android.content.Context;
import android.content.Intent;
import android.nfc.Tag;
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
import android.widget.Toast;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import pt.ulisboa.tecnico.cmov.airdesk.Application.AirDeskApp;
import pt.ulisboa.tecnico.cmov.airdesk.Exception.RemoteMethodException;
import pt.ulisboa.tecnico.cmov.airdesk.Exception.ServiceNotBoundException;
import pt.ulisboa.tecnico.cmov.airdesk.Exception.WorkspaceNotFoundException;
import pt.ulisboa.tecnico.cmov.airdesk.Message.FuncCallMessage;
import pt.ulisboa.tecnico.cmov.airdesk.R;
import pt.ulisboa.tecnico.cmov.airdesk.Workspace.OwnedWorkspace;
import pt.ulisboa.tecnico.cmov.airdesk.Workspace.Workspace;


public class OwnedWorkspacesActivity extends ActionBarActivity {
    private ArrayAdapter workspacesAdapter;
    private List<OwnedWorkspace> workspaces;
    private ListView listView;
    private List<String> selectedWorkSpaces;
    AirDeskApp airDeskApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owned_workspaces);

        /*
            Logic and Backend:
            Retrieve the user from the context and then get the list of OwnedWorkspaces
            Link the ArrayAdapter to list of workspaces, this will only display the name of the Workspace thanks
                to the toString override on the Workspace class
            Get the ListView, link it to the ArrayAdapter, allow multiple choices, allow long clicks,
                set itemClick Listener
            */
        airDeskApp = (AirDeskApp) getApplicationContext();
        workspaces = airDeskApp.getUser().getOwnedWorkspaces();
        workspacesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, workspaces);
        listView = (ListView) findViewById(R.id.listWorkspaces);
        listView.setAdapter(workspacesAdapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        listView.setLongClickable(true);

        /*Logic: Find the name of the workspace on the list position of the click*/
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedFromList =listView.getItemAtPosition(position).toString();
                startListFiles(selectedFromList);
            }
        });

        selectedWorkSpaces = new ArrayList<>();
        addContextToList(listView, airDeskApp);
        airDeskApp.getWifiHandler().setCurrentActivity(this);
    }

    public void addContextToList(final ListView listView, final AirDeskApp airDeskApp){
        listView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {

            /*Logic: When a listView item is LONG clicked, adds it to a list of names*/
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position,
                                                  long id, boolean checked) {
                String selectedFromList =listView.getItemAtPosition(position).toString();
                mode.setSubtitle(listView.getCheckedItemCount()+" selected items");
                if(checked && !selectedWorkSpaces.contains(selectedFromList) ) { //why doesnt if(checked) work? xD
                    selectedWorkSpaces.add(selectedFromList);
                }else if(selectedWorkSpaces.contains(selectedFromList)){
                    selectedWorkSpaces.remove(selectedFromList);
                }
                mode.invalidate();
            }

            /*Logic: When a button from the Bar is clicked, detect which one was clicked and
            select correct behaviour depending if delete or edit*/
            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.deleteWorkspace:
                        try{
                            for(int i=0; i<selectedWorkSpaces.size();i++){
                                OwnedWorkspace w = airDeskApp.getUser().getOwnedWorkspaceByName(selectedWorkSpaces.get(i));
                                airDeskApp.getUser().deleteWorkspace(w);
                            }
                        } catch (WorkspaceNotFoundException e) {
                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        workspacesAdapter.notifyDataSetChanged(); //warn the adapter that the original array has changed
                        selectedWorkSpaces.clear();
                        mode.finish();
                        return true;
                    case R.id.editWorkspace:
                        startEditWorkspace();
                        return true;
                    case R.id.inviteUsers:
                        startInviteUsers();
                        return true;
                    case R.id.addKeyword:
                        startKeywordAdding();
                        return true;
                    default:
                        return false;
                }
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                /*Inflate the menu with other options (delete and edit) when something is selected*/
                MenuInflater inflater = mode.getMenuInflater();
                inflater.inflate(R.menu.menu_owned_workspaces_selected, menu);
                return true;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                selectedWorkSpaces.clear();
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                if (selectedWorkSpaces.size() == 1){
                    MenuItem itemShow = menu.findItem(R.id.editWorkspace);
                    itemShow.setVisible(true);
                    itemShow = menu.findItem(R.id.inviteUsers);
                    itemShow.setVisible(true);
                    itemShow = menu.findItem(R.id.addKeyword);
                    try {
                        if (airDeskApp.getUser().getOwnedWorkspaceByName(selectedWorkSpaces.get(0)).isPublic()) {
                            itemShow.setVisible(true);
                        }else{
                            itemShow.setVisible(false);
                        }
                    }catch (WorkspaceNotFoundException e){
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    MenuItem itemHide = menu.findItem(R.id.editWorkspace);
                    itemHide.setVisible(false);
                    itemHide = menu.findItem(R.id.inviteUsers);
                    itemHide.setVisible(false);
                    itemHide = menu.findItem(R.id.addKeyword);
                    itemHide.setVisible(false);
                }
                return true;
            }
        });
    }

    /*Logic: Start a FilesActivity, and send the name of the current workspace clicked on*/
    public void startListFiles(String nameOfWorkspace){
        Intent intent = new Intent(this, FilesActivity.class);
        intent.putExtra("nameOfWorkspace",nameOfWorkspace);
        startActivity(intent);
    }

    /*Logic: Start a WorkspaceEditActivity, and send the name of the current workspace clicked on*/
    public void startEditWorkspace(){
        Intent intent = new Intent(this, WorkspaceEditActivity.class);
        String nameOfWorkspace = selectedWorkSpaces.get(0);
        intent.putExtra("nameOfWorkspace",nameOfWorkspace);
        startActivity(intent);
    }

    public void startInviteUsers(){
        Intent intent = new Intent(this, InviteUsersActivity.class);
        String nameOfWorkspace = selectedWorkSpaces.get(0);
        intent.putExtra("nameOfWorkspace",nameOfWorkspace);
        startActivity(intent);
    }

    public void startKeywordAdding(){
        Intent intent = new Intent(this, KeywordAddActivity.class);
        String nameOfWorkspace = selectedWorkSpaces.get(0);
        intent.putExtra("nameOfWorkspace",nameOfWorkspace);
        startActivity(intent);
    }

    /*Normal inflate, only has the new Option*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_owned_workspaces, menu);
        return true;
    }

    /*Once clicked on the New Button, go to the WorkspaceCreateActivity*/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.createWorkspace) {
            Intent intent = new Intent(this, WorkspaceCreateActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (workspacesAdapter !=null){
            workspacesAdapter.notifyDataSetChanged();
        }
    }

}
