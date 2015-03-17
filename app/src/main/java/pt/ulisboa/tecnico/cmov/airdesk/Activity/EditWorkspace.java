package pt.ulisboa.tecnico.cmov.airdesk.Activity;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import pt.ulisboa.tecnico.cmov.airdesk.Application.AirDeskApp;
import pt.ulisboa.tecnico.cmov.airdesk.Exception.WorkspaceNotFoundException;
import pt.ulisboa.tecnico.cmov.airdesk.R;
import pt.ulisboa.tecnico.cmov.airdesk.User.User;
import pt.ulisboa.tecnico.cmov.airdesk.Workspace.OwnedWorkspace;
import pt.ulisboa.tecnico.cmov.airdesk.Workspace.Workspace;


public class EditWorkspace extends ActionBarActivity {
    private OwnedWorkspace workspaceToEdit;
    private EditText quota;
    private ListView listView;
    private ArrayAdapter workspacesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_workspace);
        quota = (EditText)findViewById(R.id.newQuotaInput);
        String workspaceNameToEdit = getIntent().getExtras().getString("nameOfWorkspace");
        AirDeskApp airDeskApp = (AirDeskApp) getApplicationContext();
        try {
            workspaceToEdit = (OwnedWorkspace) airDeskApp.getUser().getOwnedWorkspaceByName(workspaceNameToEdit);
        }catch(WorkspaceNotFoundException e){
            //TODO
        }
        quota.setText(String.valueOf(workspaceToEdit.getQuota()));


        workspacesAdapter = new ArrayAdapter<User>(this, android.R.layout.simple_list_item_1, workspaceToEdit.getAllowedUsers());
        listView = (ListView) findViewById(R.id.privClientList);
        listView.setAdapter(workspacesAdapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
    }


    public void ConfirmChanges(View view){
        EditText quota = (EditText)findViewById(R.id.newQuotaInput);
        if(!quota.getText().toString().isEmpty())
            workspaceToEdit.setQuota(Integer.parseInt(quota.getText().toString()));
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_workspace, menu);
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
