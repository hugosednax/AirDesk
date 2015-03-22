package pt.ulisboa.tecnico.cmov.airdesk.Activity;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
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


public class WorkspaceEditActivity extends ActionBarActivity {
    private OwnedWorkspace workspaceToEdit;
    private EditText quota;
    private ListView listView;
    private ArrayAdapter usersAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_workspace);

        //Get the quota View, get the sent workspace Name from the previous screen
        quota = (EditText)findViewById(R.id.newQuotaInput);
        String workspaceNameToEdit = getIntent().getExtras().getString("nameOfWorkspace");
        AirDeskApp airDeskApp = (AirDeskApp) getApplicationContext();
        try {
            //Get the workspace from its name
            workspaceToEdit = (OwnedWorkspace) airDeskApp.getUser().getOwnedWorkspaceByName(workspaceNameToEdit);
        }catch(WorkspaceNotFoundException e){
            //TODO
        }
        //Fill the quotaView with the current Quota of the Workspace
        quota.setText(String.valueOf(workspaceToEdit.getQuota()));

        // Link the array of previliged users to an adapter, user has a toString overriden so it will display its nick and not the object
        usersAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, workspaceToEdit.getAllowedUsers());
        listView = (ListView) findViewById(R.id.privClientList);
        listView.setAdapter(usersAdapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);

        EditText prevlgdUserInput = (EditText)findViewById(R.id.previllgedUser);
        prevlgdUserInput.setOnKeyListener(new View.OnKeyListener(){
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                EditText input = (EditText)v;
                if(keyCode == KeyEvent.KEYCODE_ENTER && !input.getText().toString().equals("")){
                    Log.d("PERSON",input.getText().toString());
                    workspaceToEdit.invite(input.getText().toString());
                    usersAdapter.notifyDataSetChanged();
                    input.setText("");
                }
                return false;
            }

        });
    }

    //called when the button is pressed and changes the Quota of the workspace
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
