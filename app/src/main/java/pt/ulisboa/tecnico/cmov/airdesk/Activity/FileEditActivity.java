package pt.ulisboa.tecnico.cmov.airdesk.Activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import pt.ulisboa.tecnico.cmov.airdesk.Application.AirDeskApp;
import pt.ulisboa.tecnico.cmov.airdesk.Exception.FileNotFoundException;
import pt.ulisboa.tecnico.cmov.airdesk.Exception.WorkspaceNotFoundException;
import pt.ulisboa.tecnico.cmov.airdesk.R;
import pt.ulisboa.tecnico.cmov.airdesk.Workspace.Workspace;

public class FileEditActivity extends ActionBarActivity {

    TextView textView;
    String nameOfCurrFile;
    Workspace currWorkspace;
    boolean grabbedLock = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_edit);

        /*Logic and Backend:
         retrieve the app context and retrieve the name of the current workspace and current File, sent from the previous screen
        */
        AirDeskApp airDeskApp = (AirDeskApp) getApplicationContext();
        Intent intent = getIntent();
        String nameOfCurrWorkspace = intent.getStringExtra("nameOfWorkspace");
        nameOfCurrFile = intent.getStringExtra("nameOfFile");
        boolean isForeign = intent.getBooleanExtra("isForeign",false);

        String content = "";
        try {
             /*
            Logic and Backend:
            Retrieve the user from the context and then get the current workspace by searching with the name and get the current Name by the name
            */
            if(isForeign) {
                currWorkspace = airDeskApp.getUser().getForeignWorkspaceByName(nameOfCurrWorkspace);
            }else {
                currWorkspace = airDeskApp.getUser().getOwnedWorkspaceByName(nameOfCurrWorkspace);
            }
            content = currWorkspace.getFileContent(nameOfCurrFile);
            if(!currWorkspace.editable(nameOfCurrFile)) {
                Toast.makeText(getApplicationContext(), "File is being edited", Toast.LENGTH_SHORT).show();
                finish();
            }
            grabbedLock = true;

        } catch (WorkspaceNotFoundException e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        } catch (FileNotFoundException e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        //Find the view by its id
        textView = (TextView)findViewById(R.id.FileContent);

        //Set the text
        textView.setText(content);
        airDeskApp.getWifiHandler().setCurrentActivity(this);
    }

    public void SaveChanges(View v){
        new Thread(new Runnable() {
            public void run() {
                try {
                    currWorkspace.updateFile(nameOfCurrFile, textView.getText().toString());
                    grabbedLock = false;
                    finish();
                } catch (Exception e) {
                    final Context context = getApplicationContext();
                    final CharSequence text = e.getMessage();

                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }}).start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_file_edit, menu);
        return true;
    }

    @Override
    public void onDestroy(){
        if(grabbedLock)
            try {
                currWorkspace.setEditable(nameOfCurrFile);
            } catch (FileNotFoundException e) {
                //TODO
            } finally{
                super.onDestroy();
            }
    }

}
