package pt.ulisboa.tecnico.cmov.airdesk.Activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import pt.ulisboa.tecnico.cmov.airdesk.Application.AirDeskApp;
import pt.ulisboa.tecnico.cmov.airdesk.R;
import pt.ulisboa.tecnico.cmov.airdesk.Workspace.Workspace;


public class ForeignWorkspacesActivity extends ActionBarActivity {

    private ArrayAdapter workspacesAdapter;
    private List<Workspace> workspaces;
    private ListView listView;
    private List<String> selectedWorkSpaces;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foreign_workspaces);

        /*Logic and Backend:
         Retrieve the app context and retrieve the name of the current workspace, sent from the previous screen
        */
        AirDeskApp airDeskApp = (AirDeskApp) getApplicationContext();
        workspaces = airDeskApp.getUser().getForeignWorkspaces();
        workspacesAdapter = new ArrayAdapter<Workspace>(this, android.R.layout.simple_list_item_1,workspaces);
        listView = (ListView) findViewById(R.id.listWorkspaces);
        listView.setAdapter(workspacesAdapter);

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
                String selectedFromList = listView.getItemAtPosition(position).toString();
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
                                Workspace w = airDeskApp.getUser().getForeignWorkspaceByName(selectedWorkSpaces.get(i));
                                airDeskApp.getUser().deleteWorkspace(w);
                            }
                        }catch (Exception e){
                            Context context = getApplicationContext();
                            CharSequence text = e.getMessage();
                            int duration = Toast.LENGTH_SHORT;
                            Toast toast = Toast.makeText(context, text, duration);
                            toast.show();
                        }
                        workspacesAdapter.notifyDataSetChanged(); //warn the adapter that the original array has changed
                        selectedWorkSpaces.clear();
                        mode.finish();
                        return true;
                    default:
                        return false;
                }
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                /*Inflate the menu with other options (delete and edit) when something is selected*/
                MenuInflater inflater = mode.getMenuInflater();
                inflater.inflate(R.menu.menu_foreign_workspaces_selected, menu);
                return true;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                selectedWorkSpaces.clear();
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return true;
            }
        });
    }

    public void startListFiles(String nameOfWorkspace){
        Intent intent = new Intent(this, FilesActivity.class);
        intent.putExtra("nameOfWorkspace",nameOfWorkspace);
        intent.putExtra("isForeign",true);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_foreign_workspaces, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.addKeyword) {
            Intent intent = new Intent(this, KeywordAddActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
