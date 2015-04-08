package pt.ulisboa.tecnico.cmov.airdesk.Activity;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import pt.ulisboa.tecnico.cmov.airdesk.Application.AirDeskApp;
import pt.ulisboa.tecnico.cmov.airdesk.Exception.NotDirectoryException;
import pt.ulisboa.tecnico.cmov.airdesk.Exception.WorkspaceNotFoundException;
import pt.ulisboa.tecnico.cmov.airdesk.R;
import pt.ulisboa.tecnico.cmov.airdesk.User.User;
import pt.ulisboa.tecnico.cmov.airdesk.Workspace.OwnedWorkspace;


public class WorkspaceEditActivity extends ActionBarActivity {
    private OwnedWorkspace workspaceToEdit;
    private EditText quota;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_workspace);

        //Get the quota View, get the sent workspace Name from the previous screen
        quota = (EditText)findViewById(R.id.newQuotaInput);
        String workspaceNameToEdit = getIntent().getExtras().getString("nameOfWorkspace");
        AirDeskApp airDeskApp = (AirDeskApp) getApplicationContext();
        try {
            user = airDeskApp.getUser();
            //Get the workspace from its name
            workspaceToEdit = (OwnedWorkspace) user.getOwnedWorkspaceByName(workspaceNameToEdit);
        }catch(WorkspaceNotFoundException e){
            //TODO
        }
        //Fill the quotaView with the current Quota of the Workspace
        quota.setText(String.valueOf(workspaceToEdit.getQuota()));
    }

    //called when the button is pressed and changes the Quota of the workspace
    public void ConfirmChanges(View view) {
        EditText quota = (EditText) findViewById(R.id.newQuotaInput);
        if (!quota.getText().toString().isEmpty()) {
            try {
                workspaceToEdit.setQuota(Integer.parseInt(quota.getText().toString()));
            } catch (Exception e) {
                //TODO
            }
        }
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
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }
}
