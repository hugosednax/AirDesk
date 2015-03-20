package pt.ulisboa.tecnico.cmov.airdesk.Activity;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.FileReader;

import pt.ulisboa.tecnico.cmov.airdesk.Application.AirDeskApp;
import pt.ulisboa.tecnico.cmov.airdesk.FileSystem.ADFile;
import pt.ulisboa.tecnico.cmov.airdesk.R;

public class FileEditActivity extends ActionBarActivity {

    ADFile currFile;
    TextView textView;

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
        String nameOfCurrFile = intent.getStringExtra("nameOfFile");
        StringBuilder text = new StringBuilder();
        try {
             /*
            Logic and Backend:
            Retrieve the user from the context and then get the current workspace by searching with the name and get the current Name by the name
            */
            currFile = airDeskApp.getUser().getOwnedWorkspaceByName(nameOfCurrWorkspace).getFileByName(nameOfCurrFile);
            //Read text from file
            BufferedReader br = new BufferedReader(new FileReader(currFile.getFile()));
            String line;

            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            br.close();
        }catch (Exception e){}

        //Find the view by its id
        textView = (TextView)findViewById(R.id.FileContent);

        //Set the text
        textView.setText(text);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_file_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //if confirm edit is clicked then actually save the file
        if (id == R.id.ConfirmEdit) {
            //currFile.save(textView.getText().toString());
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
