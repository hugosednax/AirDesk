package pt.ulisboa.tecnico.cmov.airdesk.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.TextView;
import android.widget.Toast;

import pt.ulisboa.tecnico.cmov.airdesk.Application.AirDeskApp;
import pt.ulisboa.tecnico.cmov.airdesk.Exception.WorkspaceNotFoundException;
import pt.ulisboa.tecnico.cmov.airdesk.R;
import pt.ulisboa.tecnico.cmov.airdesk.User.User;

public class UserActivity extends ActionBarActivity {

    private User user;
    private ListView listView;
    private ArrayAdapter keywordsAdapter;
    AirDeskApp airDeskApp;
    EditText input;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        airDeskApp = (AirDeskApp) getApplicationContext();
        user = airDeskApp.getUser();

        TextView nameView = (TextView) findViewById(R.id.name);
        nameView.setText(user.getNick());

        TextView emailView = (TextView) findViewById(R.id.email);
        emailView.setText(user.getEmail());

        keywordsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, user.getInterestKeywords());
        listView = (ListView) findViewById(R.id.kwrdList);
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
    }

    public void logOut(View v){
        SharedPreferences.Editor editor = getSharedPreferences("user_prefs", MODE_PRIVATE).edit();
        editor.remove("email_pref");
        editor.remove("nick_pref");
        editor.apply();
        Intent intent = new Intent(this, StarterActivity.class);
        startActivity(intent);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_user, menu);
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
