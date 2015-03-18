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
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import pt.ulisboa.tecnico.cmov.airdesk.Application.AirDeskApp;
import pt.ulisboa.tecnico.cmov.airdesk.Exception.WorkspaceNotFoundException;
import pt.ulisboa.tecnico.cmov.airdesk.FileSystem.ADFile;
import pt.ulisboa.tecnico.cmov.airdesk.R;
import pt.ulisboa.tecnico.cmov.airdesk.Workspace.Workspace;


public class ListFiles extends ActionBarActivity {
    private ArrayAdapter filesAdapter;
    private ListView listView;
    private Workspace currWorkspace;
    private List<String> selectedFiles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_files);

        selectedFiles = new ArrayList<String>();
        AirDeskApp airDeskApp = (AirDeskApp) getApplicationContext();
        Intent intent = getIntent();
        String nameOfCurrWorkspace = intent.getStringExtra("nameOfWorkspace");
        try {
            currWorkspace = airDeskApp.getUser().getOwnedWorkspaceByName(nameOfCurrWorkspace);
            filesAdapter = new ArrayAdapter<ADFile>(this, android.R.layout.simple_list_item_1, currWorkspace.getFiles());
            listView = (ListView) findViewById(R.id.listFiles);
            listView.setAdapter(filesAdapter);
            listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
            listView.setLongClickable(true);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String selectedFromList =listView.getItemAtPosition(position).toString();
                    startViewFile(selectedFromList);
                }
            });
        }catch(WorkspaceNotFoundException e){}
        addContextToList(listView, airDeskApp);
    }

    public void addContextToList(final ListView listView, final AirDeskApp airDeskApp){
        listView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {

            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position,
                                                  long id, boolean checked) {
                String selectedFromList =listView.getItemAtPosition(position).toString();
                if(selectedFiles.contains(selectedFromList)) { //why doesnt if(checked) work? xD
                    selectedFiles.remove(selectedFromList);
                }else {
                    selectedFiles.add(selectedFromList);
                }
                mode.invalidate();
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                // Respond to clicks on the actions in the CAB
                switch (item.getItemId()) {
                    case R.id.deleteWorkspace:
                            for(int i=0; i<selectedFiles.size();i++){
                                try {
                                    currWorkspace.removeFile(selectedFiles.get(i));
                                }catch (Exception e){}
                            }
                        filesAdapter.notifyDataSetChanged();
                        selectedFiles.clear();
                        mode.finish(); // Action picked, so close the CAB
                        return true;
                    case R.id.editWorkspace:

                        return true;
                    default:
                        return false;
                }
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                // Inflate the menu for the CAB
                MenuInflater inflater = mode.getMenuInflater();
                inflater.inflate(R.menu.menu_list_files_selected, menu);
                return true;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                // Here you can make any necessary updates to the activity when
                // the CAB is removed. By default, selected items are deselected/unchecked.
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
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


    public void startViewFile(String selectedFile){
        Intent intent = new Intent(this, EditViewFile.class);
        intent.putExtra("nameOfWorkspace",currWorkspace.getName());
        intent.putExtra("nameOfWorkspace",selectedFile);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_list_files, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.createFile) {
            Intent intent = new Intent(this, NewFile.class);
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
