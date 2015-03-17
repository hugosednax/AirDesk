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
import pt.ulisboa.tecnico.cmov.airdesk.R;
import pt.ulisboa.tecnico.cmov.airdesk.Workspace.Workspace;


public class OwnedWorkspaces extends ActionBarActivity {
    private ArrayAdapter listWorkspacesAdapter;
    private List<Workspace> listWorkspaces;
    private ListView listView;
    private List<String> selectedWorkSpaces;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owned_workspaces);

        AirDeskApp airDeskApp = (AirDeskApp) getApplicationContext();
        listWorkspaces = airDeskApp.getUser().getOwnedWorkspaces();
        listWorkspacesAdapter = new ArrayAdapter<Workspace>(this, android.R.layout.simple_list_item_1, listWorkspaces);
        listView = (ListView) findViewById(R.id.listWorkspaces);
        listView.setAdapter(listWorkspacesAdapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        listView.setLongClickable(true);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedFromList =listView.getItemAtPosition(position).toString();
                startListFiles(selectedFromList);
            }
        });

        selectedWorkSpaces = new ArrayList<>();
        addContextToList(listView, airDeskApp);
    }

    public void addContextToList(final ListView listView, final AirDeskApp airDeskApp){
        listView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {

            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position,
                                                  long id, boolean checked) {
                String selectedFromList =listView.getItemAtPosition(position).toString();
                if(checked) {
                    selectedWorkSpaces.remove(selectedFromList);
                }else {
                    selectedWorkSpaces.add(selectedFromList);
                }
                mode.invalidate();
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                // Respond to clicks on the actions in the CAB
                switch (item.getItemId()) {
                    case R.id.deleteWorkspace:
                        try{
                            for(int i=0; i<selectedWorkSpaces.size();i++){
                                Workspace w = airDeskApp.getUser().getOwnedWorkspaceByName(selectedWorkSpaces.get(i));
                                airDeskApp.getUser().deleteWorkspace(w);
                            }
                        }catch(WorkspaceNotFoundException e){
                            Log.w("yap","exception this workspace does not exist");
                        }
                        listWorkspacesAdapter.notifyDataSetChanged();
                        selectedWorkSpaces.clear();
                        mode.finish(); // Action picked, so close the CAB
                        return true;
                    case R.id.editWorkspace:
                        startEditWorkspace();
                        return true;
                    default:
                        return false;
                }
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                // Inflate the menu for the CAB
                MenuInflater inflater = mode.getMenuInflater();
                inflater.inflate(R.menu.menu_owned_workspaces_selected, menu);
                return true;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                // Here you can make any necessary updates to the activity when
                // the CAB is removed. By default, selected items are deselected/unchecked.
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                if (selectedWorkSpaces.size() == 1){
                    MenuItem item = menu.findItem(R.id.editWorkspace);
                    item.setVisible(true);
                    return true;
                } else {
                    MenuItem item = menu.findItem(R.id.editWorkspace);
                    item.setVisible(false);
                    return true;
                }
            }
        });
    }

    public void startListFiles(String nameOfWorkspace){
        Intent intent = new Intent(this, ListFiles.class);
        intent.putExtra(nameOfWorkspace, "nameOfWorkspace");
        startActivity(intent);
    }

    public void startEditWorkspace(){ //TODO
        Intent intent = new Intent(this, EditWorkspace.class);
        //fetch from list of selections
        //intent.putExtra(nameOfWorkspace, "nameOfWorkspace");
        startActivity(intent);
    }

    public void startCreateWorkspace(){ //TODO
        Intent intent = new Intent(this, WorkspaceCreate.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_owned_workspaces, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.createWorkspace) {
            Intent intent = new Intent(this, WorkspaceCreate.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("resume", "list");
        if (listWorkspacesAdapter !=null){
            listWorkspacesAdapter.notifyDataSetChanged();
        }
    }

}
