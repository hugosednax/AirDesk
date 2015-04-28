package pt.ulisboa.tecnico.cmov.airdesk.FileSystem;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import pt.ulisboa.tecnico.cmov.airdesk.Application.AirDeskApp;
import pt.ulisboa.tecnico.cmov.airdesk.DTO.WorkspaceDTO;
import pt.ulisboa.tecnico.cmov.airdesk.Exception.CantCreateFileException;
import pt.ulisboa.tecnico.cmov.airdesk.Exception.WriteToFileException;
import pt.ulisboa.tecnico.cmov.airdesk.Workspace.Workspace;

/**
 * Created by Filipe Teixeira on 27/04/2015.
 */
public class JSONHandler {

    private static final String TAG_OWNEDWORKSPACES = "OwnedWorkspaces";
    private static final String TAG_FOREIGNWORKSPACES = "ForeignWorkspaces";

    private File settings;
    private List<WorkspaceDTO> ownedWorkspaces;
    private List<WorkspaceDTO> foreignWorkspaces;
    private boolean hadSettings;

    public JSONHandler() throws CantCreateFileException, WriteToFileException {
        File mainDir = AirDeskApp.getAppContext().getDir("data", AirDeskApp.getAppContext().MODE_PRIVATE);
        settings = new File(mainDir, "JSONsettings.txt");

        ownedWorkspaces = new ArrayList<WorkspaceDTO>();
        foreignWorkspaces = new ArrayList<WorkspaceDTO>();

        if(settings.isFile()){
            hadSettings = true;
            //readSettingsFile();
        } else{
            hadSettings = false;
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
        }
    }

    private void writeDefaultFile() throws IOException {
        JSONObject JSONSettings = new JSONObject();
        JSONArray ownedWorkspaces = new JSONArray();
        JSONArray foreignWorkspaces = new JSONArray();

        try {
            JSONSettings.put(TAG_OWNEDWORKSPACES, ownedWorkspaces);
            JSONSettings.put(TAG_FOREIGNWORKSPACES, foreignWorkspaces);
        } catch (JSONException e) {
            e.printStackTrace();
            //TODO
        }

        try {
            writeToFile(JSONSettings.toString() + "\n");
        } catch (IOException e) {
            Log.d("[AirDesk]", "Error writing default string to Settings file" + "\n" + e.getMessage());
        }
    }

    private void writeToFile(String content) throws IOException {
        Log.d("[AirDesk]", "Trying to write " + content);
        PrintWriter writer = new PrintWriter(settings);
        writer.print(content);
        writer.close();
    }

    public void saveOwnedWorkspace(WorkspaceDTO ws) throws WriteToFileException {
        BufferedReader br = null;
        String jsonStr = "";
        String line = "";

        try {
            br = new BufferedReader(new FileReader(settings.getPath()));
        } catch (FileNotFoundException e) {
            // TODO
            e.printStackTrace();
        }

        try {
            line = br.readLine();
            while(line != null)
                jsonStr += line + "\n";
        } catch (IOException e) {
            //TODO
            e.printStackTrace();
        }

        try{
            JSONObject JSONsettings = new JSONObject(jsonStr);
            JSONArray ownedWorkspaces = JSONsettings.getJSONArray(TAG_OWNEDWORKSPACES);
            ownedWorkspaces.put(ws.toJSON());
            JSONsettings.remove(TAG_OWNEDWORKSPACES);
            JSONsettings.put(TAG_OWNEDWORKSPACES, ownedWorkspaces);
            Log.d("[AirDesk]", JSONsettings.toString());
            writeToFile(JSONsettings.toString());
        } catch (JSONException e) {
            //TODO
            e.printStackTrace();
        } catch (IOException e) {
            throw new WriteToFileException(e.getMessage());
        }
    }




}
