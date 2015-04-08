package pt.ulisboa.tecnico.cmov.airdesk.Activity;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import java.util.List;

import pt.ulisboa.tecnico.cmov.airdesk.Application.AirDeskApp;
import pt.ulisboa.tecnico.cmov.airdesk.Exception.WorkspaceNotFoundException;
import pt.ulisboa.tecnico.cmov.airdesk.R;
import pt.ulisboa.tecnico.cmov.airdesk.User.User;
import pt.ulisboa.tecnico.cmov.airdesk.Workspace.OwnedWorkspace;

public class InviteUsersActivity extends ActionBarActivity {
    private OwnedWorkspace workspaceToEdit;
    private User user;
    private ListView listView;
    private ArrayAdapter usersAdapter;
    EditText input;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_users);


        String workspaceNameToEdit = getIntent().getExtras().getString("nameOfWorkspace");
        AirDeskApp airDeskApp = (AirDeskApp) getApplicationContext();
        try {
            user = airDeskApp.getUser();
            //Get the workspace from its name
            workspaceToEdit = (OwnedWorkspace) user.getOwnedWorkspaceByName(workspaceNameToEdit);
        }catch(WorkspaceNotFoundException e){
            //TODO
        }

        // Link the array of previliged users to an adapter, user has a toString overriden so it will display its nick and not the object
        usersAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, workspaceToEdit.getAllowedUsers());
        listView = (ListView) findViewById(R.id.privClientList);
        listView.setAdapter(usersAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedFromList =(String) (listView.getItemAtPosition(position));
                user.uninvite(workspaceToEdit,selectedFromList);
                usersAdapter.notifyDataSetChanged();
            }
        });

        EditText prevlgdUserInput = (EditText)findViewById(R.id.previllgedUser);
        prevlgdUserInput.setOnKeyListener(new View.OnKeyListener(){
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                //This is the filter
                boolean filter = false;
                if (event.getAction()!=KeyEvent.ACTION_DOWN)
                    return true;

                input = (EditText)v;
                if(keyCode == KeyEvent.KEYCODE_ENTER && input.getText().toString()!=null){
                    user.invite(workspaceToEdit, input.getText().toString());
                    usersAdapter.notifyDataSetChanged();
                    input.getText().clear();
                    filter = true;
                }
                return filter;
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_invite_users, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }
}
