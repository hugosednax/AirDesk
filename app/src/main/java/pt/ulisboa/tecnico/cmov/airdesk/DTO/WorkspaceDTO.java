package pt.ulisboa.tecnico.cmov.airdesk.DTO;

/**
 * Created by Filipe Teixeira on 21/03/2015.
 */
public class WorkspaceDTO {
    private String name;
    private boolean isPublic;
    private int quota;

    public WorkspaceDTO(String name, boolean isPublic, int quota) {
        this.name = name;
        this.isPublic = isPublic;
        this.quota = quota;
    }

    public String getName() {
        return name;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public int getQuota() {
        return quota;
    }
}
