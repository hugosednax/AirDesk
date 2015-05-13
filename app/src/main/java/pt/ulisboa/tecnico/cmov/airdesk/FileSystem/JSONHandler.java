package pt.ulisboa.tecnico.cmov.airdesk.FileSystem;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import pt.ulisboa.tecnico.cmov.airdesk.Application.AirDeskApp;
import pt.ulisboa.tecnico.cmov.airdesk.DTO.WorkspaceDTO;
import pt.ulisboa.tecnico.cmov.airdesk.Exception.CantCreateFileException;
import pt.ulisboa.tecnico.cmov.airdesk.Exception.WriteToFileException;

/**
 * Created by Filipe Teixeira on 27/04/2015.
 */
public class JSONHandler {
    //region Static String
    public static final String TAG_OWNED_WORKSPACES = "OwnedWorkspaces";
    public static final String TAG_FOREIGN_WORKSPACES = "ForeignWorkspaces";
    public static final String TAG_NAME = "name";
    public static final String TAG_PUBLIC = "isPublic";
    public static final String TAG_QUOTA = "quota";
    public static final String TAG_KEYWORDS = "keywords";
    public static final String TAG_ALLOWED_USERS = "allowedUsers";
    public static final String TAG_FILE_NAMES = "fileNames";
    //endregion

    //region Class Variables
    private File settings;
    private List<WorkspaceDTO> ownedWorkspaces;
    private List<WorkspaceDTO> foreignWorkspaces;
    private boolean hadSettings;
    //endregion

    //region Constructor
    public JSONHandler() throws CantCreateFileException, WriteToFileException {
        File mainDir = AirDeskApp.getAppContext().getDir("data", AirDeskApp.getAppContext().MODE_PRIVATE);
        settings = new File(mainDir, "JSONSettings.txt");

        ownedWorkspaces = new ArrayList<>();
        foreignWorkspaces = new ArrayList<>();

        if(settings.isFile()){
            hadSettings = true;
            readSettingsFile();
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
    //endregion

    //region Private Methods
    private void writeDefaultFile() throws IOException {
        JSONObject JSONSettings = new JSONObject();
        JSONArray ownedWorkspaces = new JSONArray();
        JSONArray foreignWorkspaces = new JSONArray();

        try {
            JSONSettings.put(TAG_OWNED_WORKSPACES, ownedWorkspaces);
            JSONSettings.put(TAG_FOREIGN_WORKSPACES, foreignWorkspaces);
        } catch (JSONException e) {
            Log.d("[AirDesk]", "Error setting the JSON arrays" + "\n" + e.getMessage());
        }

        try {
            writeToFile(JSONSettings.toString() + "\n");
        } catch (IOException e) {
            Log.d("[AirDesk]", "Error writing default string to Settings file" + "\n" + e.getMessage());
        }
    }

    private void writeToFile(String content) throws IOException {
        //Log.d("[AirDesk]", "Trying to write " + content);
        PrintWriter writer = new PrintWriter(settings);
        writer.print(content);
        writer.close();
    }

    private String toJSONString() {
        String jsonStr = "";

        try {
            String line;
            BufferedReader br = new BufferedReader(new FileReader(settings.getPath()));
            line = br.readLine();
            while(line != null){
                jsonStr += line + "\n";
                line = br.readLine();
            }
        } catch (IOException e) {
            Log.d("[AirDesk]", "Error reading Settings file" + "\n" + e.getMessage());
        }
        return jsonStr;
    }
    //endregion

    //region Public API
    public List<WorkspaceDTO> getOwnedWorkspaces() {
        return ownedWorkspaces;
    }

    public List<WorkspaceDTO> getForeignWorkspaces() {
        return foreignWorkspaces;
    }

    public void saveOwnedWorkspace(WorkspaceDTO ws) throws WriteToFileException {

        try{
            JSONObject JSONsettings = new JSONObject(toJSONString());
            JSONArray ownedWorkspaces = JSONsettings.getJSONArray(TAG_OWNED_WORKSPACES);
            ownedWorkspaces.put(ws.toJSON());
            writeToFile(JSONsettings.toString()+"\n");
        } catch (JSONException e) {
            Log.d("[AirDesk]", "Error creating JSON object" + "\n" + e.getMessage());
        } catch (IOException e) {
            throw new WriteToFileException(e.getMessage());
        }
    }

    public void removeOwnedWorkspace(WorkspaceDTO ws) throws WriteToFileException {
        try{
            JSONObject JSONSettings = new JSONObject(this.toJSONString());
            JSONArray ownedWorkspaces = JSONSettings.getJSONArray(TAG_OWNED_WORKSPACES);
            ArrayList<String> list = new ArrayList<>();
            for(int i = 0; i < ownedWorkspaces.length(); i++){
                JSONObject workspace = (JSONObject)ownedWorkspaces.get(i);
                if(!workspace.get(TAG_NAME).equals(ws.getName())){
                    list.add(workspace.toString());
                }
            }
            JSONArray jsArray = new JSONArray(list);
            JSONSettings.put(TAG_OWNED_WORKSPACES, jsArray);
            writeToFile(JSONSettings.toString()+"\n");
        } catch (JSONException e) {
            Log.d("[AirDesk]", "Error creating JSON object" + "\n" + e.getMessage());
        } catch (IOException e) {
            throw new WriteToFileException(e.getMessage());
        }
    }

    public void updateOwnedWorkspace(WorkspaceDTO ws) throws WriteToFileException {
        removeOwnedWorkspace(ws);
        saveOwnedWorkspace(ws);
    }

    public void readSettingsFile(){
        try {
            JSONObject JSONSettings = new JSONObject(this.toJSONString());
            JSONArray ownedWorkspaces = JSONSettings.getJSONArray(TAG_OWNED_WORKSPACES);
            for (int i = 0; i < ownedWorkspaces.length(); i++) {
                JSONObject workspace = (JSONObject) ownedWorkspaces.get(i);
                WorkspaceDTO wsDTO = new WorkspaceDTO((String)workspace.get(TAG_NAME), (boolean)workspace.get(TAG_PUBLIC),
                        (int)workspace.get(TAG_QUOTA));
                JSONArray keywords = workspace.getJSONArray(TAG_KEYWORDS);
                JSONArray allowedUsers = workspace.getJSONArray(TAG_ALLOWED_USERS);
                for(int j = 0; j < keywords.length(); j++){
                    wsDTO.addKeyword((String)keywords.get(j));
                }
                for(int j = 0; j < allowedUsers.length(); j++){
                    wsDTO.addAllowedUser((String)allowedUsers.get(j));
                }
                this.ownedWorkspaces.add(wsDTO);
            }
            JSONArray foreignWorkspaces = JSONSettings.getJSONArray(TAG_FOREIGN_WORKSPACES);
            for (int i = 0; i < foreignWorkspaces.length(); i++) {
                JSONObject workspace = (JSONObject) ownedWorkspaces.get(i);
                WorkspaceDTO wsDTO = new WorkspaceDTO((String)workspace.get(TAG_NAME), (boolean)workspace.get(TAG_PUBLIC),
                        (int)workspace.get(TAG_QUOTA));
                this.foreignWorkspaces.add(wsDTO);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public boolean hadSettings() { return this.hadSettings; }
    //endregion
}
