package pt.ulisboa.tecnico.cmov.airdesk.FileSystem;

import android.provider.SyncStateContract;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import pt.ulisboa.tecnico.cmov.airdesk.Application.AirDeskApp;
import pt.ulisboa.tecnico.cmov.airdesk.Exception.CantCreateFileException;
import pt.ulisboa.tecnico.cmov.airdesk.Exception.WriteToFileException;
import pt.ulisboa.tecnico.cmov.airdesk.Workspace.Workspace;

/**
 * Created by Filipe Teixeira on 20/03/2015.
 */
public class SettingsHandler {
    //Foreign workspaces save not used
    private static final String DEFAULT_FILE_CONTENT ="OwnedWorkspaces" + "\n" + "NONE" + "\n" + "ForeignWorkspaces" + "\n"
            + "NONE";
    File settings;
    List<Workspace> ownedWorkspaces;
    List<Workspace> foreignWorkspaces;
    boolean hadSettings;


    public SettingsHandler() throws CantCreateFileException, WriteToFileException {

        File mainDir = AirDeskApp.getAppContext().getDir("data", AirDeskApp.getAppContext().MODE_PRIVATE);
        settings = new File(mainDir, ".settings.txt");

        if(settings.exists()){
            hadSettings = true;
            readSettingsFile();
        } else{
            try {
                settings.createNewFile();
            } catch (IOException e) {
                throw new CantCreateFileException(e.getMessage());
            }

            try {
                writeDefaultFile();
            } catch (IOException e) {
                throw new WriteToFileException(e.getMessage());
            }

            ownedWorkspaces = new ArrayList<Workspace>();
            foreignWorkspaces = new ArrayList<Workspace>();
            hadSettings = false;
        }
    }

    private void writeDefaultFile() throws IOException {
        FileWriter fw = new FileWriter(settings.getAbsoluteFile());
        BufferedWriter bw = new BufferedWriter(fw);
        try {
            bw.write(DEFAULT_FILE_CONTENT);
            bw.close();
        } catch (IOException e) {
            Log.d("[AirDesk]", "Error writing default string to Settings file" + "\n" + e.getMessage());
        }
    }

    private boolean readSettingsFile() {
        boolean result = false;
        if(settings.getTotalSpace() > 0){

            result = true;
        }
        return result;
    }

    public boolean hadSettings() {
        return hadSettings;
    }
}
