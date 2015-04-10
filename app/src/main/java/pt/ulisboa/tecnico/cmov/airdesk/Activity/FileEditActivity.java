package pt.ulisboa.tecnico.cmov.airdesk.Activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileReader;

import pt.ulisboa.tecnico.cmov.airdesk.Application.AirDeskApp;
import pt.ulisboa.tecnico.cmov.airdesk.FileSystem.ADFile;
import pt.ulisboa.tecnico.cmov.airdesk.R;
import pt.ulisboa.tecnico.cmov.airdesk.Workspace.Workspace;

public class FileEditActivity extends ActionBarActivity {

    ADFile currFile;
    TextView textView;
    String nameOfCurrFile;
    Workspace currWorkspace;

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
        StringBuilder text = new StringBuilder();
        boolean isForeign = intent.getBooleanExtra("isForeign",false);
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
            currFile = currWorkspace.getFileByName(nameOfCurrFile);
            //Read text from file
            BufferedReader br = new BufferedReader(new FileReader(currFile.getFile()));
            String line;

            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            br.close();
        }catch (Exception e){
            Context context = getApplicationContext();
            CharSequence toastText = e.getMessage();
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }

        //Find the view by its id
        textView = (TextView)findViewById(R.id.FileContent);

        //Set the text
        textView.setText(text);
    }

    public void SaveChanges(View v){
        new Thread(new Runnable() {
            public void run() {
                EditText listView = (EditText) findViewById(R.id.FileName);
                try {
                    currWorkspace.updateFile(nameOfCurrFile, textView.getText().toString());
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

}
