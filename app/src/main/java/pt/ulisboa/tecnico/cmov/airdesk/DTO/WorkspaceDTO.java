package pt.ulisboa.tecnico.cmov.airdesk.DTO;

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
    //endregion

    //region Constructors
    public WorkspaceDTO(String name, boolean isPublic, int quota) {
        this.name = name;
        this.isPublic = isPublic;
        this.quota = quota;
        this.allowedUsers = new ArrayList<>();
    }

    public WorkspaceDTO(OwnedWorkspace ws){
        this.name = ws.getName();
        this.quota = ws.getQuota();
        this.allowedUsers = ws.getAllowedUsers();
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
    //endregion

    //region Setters
    public boolean isPublic() {
        return isPublic;
    }

    public void setAllowedUsers(List<String> allowedUsers) {
        this.allowedUsers = allowedUsers;
    }

    public void addAllowedUser(String user){
        this.getAllowedUsers().add(user);
    }

    //endregion
}
