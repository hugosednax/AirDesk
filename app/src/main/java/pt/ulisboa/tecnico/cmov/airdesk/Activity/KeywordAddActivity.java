package pt.ulisboa.tecnico.cmov.airdesk.Activity;

import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import pt.ulisboa.tecnico.cmov.airdesk.Application.AirDeskApp;
import pt.ulisboa.tecnico.cmov.airdesk.R;
import pt.ulisboa.tecnico.cmov.airdesk.User.User;
import pt.ulisboa.tecnico.cmov.airdesk.Workspace.OwnedWorkspace;

public class KeywordAddActivity extends ActionBarActivity {
    private User user;
    private ListView listView;
    private ArrayAdapter keywordsAdapter;
    EditText input;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_keyword_manager);

        AirDeskApp airDeskApp = (AirDeskApp) getApplicationContext();
        try {
            user = airDeskApp.getUser();
        } catch (Exception e) {
            Context context = getApplicationContext();
            CharSequence text = e.getMessage();
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }

        keywordsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,user.getInterestKeywords());
        listView = (ListView) findViewById(R.id.keywordList);
        listView.setAdapter(keywordsAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedFromList =(String) (listView.getItemAtPosition(position));
                user.removeInterestKeyword(selectedFromList);
                keywordsAdapter.notifyDataSetChanged();
            }
        });

        EditText keywordInput = (EditText)findViewById(R.id.keywordInput);
        keywordInput.setOnKeyListener(new View.OnKeyListener(){
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction()!=KeyEvent.ACTION_DOWN && keyCode != KeyEvent.KEYCODE_BACK)
                    return true;

                input = (EditText)v;
                if(keyCode == KeyEvent.KEYCODE_ENTER && input.getText().toString()!=null){
                    user.addInterestKeyword(input.getText().toString());
                    keywordsAdapter.notifyDataSetChanged();
                    input.getText().clear();
                    return true;
                }
                return false;
            }
        });

        keywordInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    user.addInterestKeyword(input.getText().toString());
                    keywordsAdapter.notifyDataSetChanged();
                    input.getText().clear();
                    return true;
                }
                return false;
            }
        });

        airDeskApp.getWifiHandler().setCurrentActivity(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_keyword_manager, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }
}
