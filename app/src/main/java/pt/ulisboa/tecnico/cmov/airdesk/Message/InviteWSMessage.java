package pt.ulisboa.tecnico.cmov.airdesk.Message;

import org.json.JSONException;
import org.json.JSONObject;

import pt.ulisboa.tecnico.cmov.airdesk.DTO.WorkspaceDTO;

/**
 * Created by Filipe Teixeira on 09/05/2015.
 */
public class InviteWSMessage extends Message {
    //region Class Variables
    private WorkspaceDTO workspace;
    //endregion

    //region Constructor
    public InviteWSMessage(String user, WorkspaceDTO workspace) {
        super(Type.INVITE, user);
        this.workspace = workspace;
    }
    //endregion

    //region Getters
    public WorkspaceDTO getWorkspaceDTO() {
        return this.workspace;
    }
    //endregion

    @Override
    public JSONObject toJSON() throws JSONException {
        JSONObject result = new JSONObject();
        result.put(MESSAGE_USER, this.getUser());
        result.put(MESSAGE_TYPE, this.getTypeOfMessage());
        result.put(MESSAGE_WORKSPACE, this.getWorkspaceDTO().toJSON());
        return result;
    }
}
