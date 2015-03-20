package pt.ulisboa.tecnico.cmov.airdesk.Activity;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import pt.ulisboa.tecnico.cmov.airdesk.Application.AirDeskApp;
import pt.ulisboa.tecnico.cmov.airdesk.R;


public class ForeignWorkspacesActivity extends ActionBarActivity {

    private ListAdapter listOfWorkspaces;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foreign_workspaces);

        /*Logic and Backend:
         Retrieve the app context and retrieve the name of the current workspace, sent from the previous screen
        */
        AirDeskApp airDeskApp = (AirDeskApp) getApplicationContext();
        listOfWorkspaces = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,airDeskApp.getUser().getForeignWorkspacesNames());
        listView = (ListView) findViewById(R.id.listWorkspaces);
        listView.setAdapter(listOfWorkspaces);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedFromList =(String) (listView.getItemAtPosition(position));
                startListFiles(selectedFromList);
            }
        });
    }

    public void startListFiles(String nameOfWorkspace){
        Intent intent = new Intent(this, FilesActivity.class);
        intent.putExtra(nameOfWorkspace, "nameOfWorkspace");
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
        return super.onOptionsItemSelected(item);
    }
}
