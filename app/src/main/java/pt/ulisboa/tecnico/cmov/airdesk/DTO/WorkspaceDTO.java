package pt.ulisboa.tecnico.cmov.airdesk.DTO;

import pt.ulisboa.tecnico.cmov.airdesk.Workspace.Workspace;

/**
 * Created by Filipe Teixeira on 21/03/2015.
 */
public class WorkspaceDTO {

    //region Class Variables
    private String name;
    private boolean isPublic;
    private int quota;
    //endregion

    //region Constructors
    public WorkspaceDTO(String name, boolean isPublic, int quota) {
        this.name = name;
        this.isPublic = isPublic;
        this.quota = quota;
    }

    public WorkspaceDTO(Workspace ws){
        this.name = ws.getName();
        this.quota = ws.getQuota();
    }
    //endregion

    //region Getters
    public String getName() {
        return name;
    }

    public int getQuota() {
        return quota;
    }
    //endregion

    //region Setters
    public boolean isPublic() {
        return isPublic;
    }
    //endregion
}
