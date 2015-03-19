package pt.ulisboa.tecnico.cmov.airdesk.Activity;

/**
 * Created by hugo__000 on 19/03/2015.
 */

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Scanner;

import pt.ulisboa.tecnico.cmov.airdesk.R;

public class InternalStorage extends Activity {

    private static final String LINE_SEP = System.getProperty("line.separator");


    @Override
    public void onCreate(final Bundle icicle) {
        super.onCreate(icicle);
        //TODO
    }

    private void write(String filename) {
        FileOutputStream fos = null;
        try {
            // note that there are many modes you can use
            fos = openFileOutput(filename, Context.MODE_PRIVATE);
            //fos.write(input.getText().toString().getBytes());
            Toast.makeText(this, "File written", Toast.LENGTH_SHORT).show();
        } catch (FileNotFoundException e) {
            Log.e("Internal Storage", "File not found", e);
        } /*catch (IOException e) {
            Log.e("Internal Storage", "IO problem", e);
        } */finally {
            try {
                fos.close();
            } catch (IOException e) {
                Log.d("AirDesk File Explorer", "Close error.");
            }
        }
    }

    private void read(String filename) {
        FileInputStream fis = null;
        Scanner scanner = null;
        StringBuilder sb = new StringBuilder();
        try {
            fis = openFileInput(filename);
            // scanner does mean one more object, but it's easier to work with
            scanner = new Scanner(fis);
            while (scanner.hasNextLine()) {
                sb.append(scanner.nextLine() + LINE_SEP);
            }
            Toast.makeText(this, "File read", Toast.LENGTH_SHORT).show();
        } catch (FileNotFoundException e) {
            Log.e("Internal Storage", "File not found", e);
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    Log.d("AirDesk File Explorer", "Close error.");
                }
            }
            if (scanner != null) {
                scanner.close();
            }
        }
        //output.setText(sb.toString());
    }
}
