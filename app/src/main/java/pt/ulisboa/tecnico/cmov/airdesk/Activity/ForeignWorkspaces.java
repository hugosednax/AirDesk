package pt.ulisboa.tecnico.cmov.airdesk.Activity;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import pt.ulisboa.tecnico.cmov.airdesk.Application.AirDeskApp;
import pt.ulisboa.tecnico.cmov.airdesk.R;


public class ForeignWorkspaces extends ActionBarActivity {

    private ListAdapter listOfWorkspaces;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foreign_workspaces);

        AirDeskApp airDeskApp = (AirDeskApp) getApplicationContext();
        listOfWorkspaces = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,airDeskApp.getUser().getForeignWorkspacesNames());
        ListView listView = (ListView) findViewById(R.id.lisWorkspaces);
        listView.setAdapter(listOfWorkspaces);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_foreign_workspaces, menu);
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
