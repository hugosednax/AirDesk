package pt.ulisboa.tecnico.cmov.airdesk.DTO;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import pt.ulisboa.tecnico.cmov.airdesk.Workspace.OwnedWorkspace;
import pt.ulisboa.tecnico.cmov.airdesk.Workspace.Workspace;

/**
 * Created by Filipe Teixeira on 21/03/2015.
 */
public class WorkspaceDTO {

    //region Class Variables
    private String name;
    private boolean isPublic;
    private int quota;
    private List<String> allowedUsers;
    private List<String> keywords;
    //endregion

    //region Constructors
    public WorkspaceDTO(String name, boolean isPublic, int quota) {
        this.name = name;
        this.isPublic = isPublic;
        this.quota = quota;
        this.allowedUsers = new ArrayList<>();
        this.keywords = new ArrayList<>();
    }

    public WorkspaceDTO(OwnedWorkspace ws){
        this.name = ws.getName();
        this.quota = ws.getQuota();
        this.allowedUsers = ws.getAllowedUsers();
        this.keywords = new ArrayList<>();
    }
    //endregion

    //region Getters
    public String getName() {
        return name;
    }

    public int getQuota() {
        return quota;
    }

    public List<String> getAllowedUsers() {
        return allowedUsers;
    }

    public List<String> getKeywords() {
        return keywords;
    }
    //endregion

    //region Setters
    public boolean isPublic() {
        return isPublic;
    }

    public void addAllowedUser(String user){
        this.getAllowedUsers().add(user);
    }

    public void addKeyword(String keyword) { this.getKeywords().add(keyword); }

    public JSONObject toJSON() {
        JSONObject jsonWS = new JSONObject();
        JSONArray allowedUsers = new JSONArray();
        JSONArray keywords = new JSONArray();
        try {
            jsonWS.put("name", this.getName());
            jsonWS.put("quota", this.getQuota());
            jsonWS.put("isPublic", this.isPublic());
            for (String user : this.getAllowedUsers())
                allowedUsers.put(user);
            for (String keyword : this.getKeywords())
                keywords.put(keyword);
            jsonWS.put("allowedUsers", allowedUsers);
            jsonWS.put("keywords", keywords);
        } catch (JSONException e) {
            Log.d("[AirDesk]", "JSON exception at the parsing an owned WS to JSON string\n" + e.getMessage());
        }
        return jsonWS;
    }
    //endregion
}
