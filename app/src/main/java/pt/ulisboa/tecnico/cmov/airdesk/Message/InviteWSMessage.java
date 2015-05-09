package pt.ulisboa.tecnico.cmov.airdesk.Message;

import org.json.JSONException;
import org.json.JSONObject;

import pt.ulisboa.tecnico.cmov.airdesk.DTO.WorkspaceDTO;

/**
 * Created by Filipe Teixeira on 09/05/2015.
 */
public class InviteWSMessage extends Message {
    private WorkspaceDTO workspace;

    public InviteWSMessage(WorkspaceDTO workspace) {
        super(Type.INVITE);
        this.workspace = workspace;
    }

    public WorkspaceDTO getWorkspaceDTO() {
        return this.workspace;
    }

    @Override
    public JSONObject toJSON() throws JSONException {
        JSONObject result = new JSONObject();
        result.put(MESSAGE_TYPE, this.getTypeOfMessage());
        result.put(MESSAGE_WORKSPACE, this.getWorkspaceDTO().toJSON());
        return result;
    }
}
