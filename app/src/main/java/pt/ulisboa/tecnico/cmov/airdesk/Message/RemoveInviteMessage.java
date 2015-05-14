package pt.ulisboa.tecnico.cmov.airdesk.Message;

import org.json.JSONException;
import org.json.JSONObject;

import pt.ulisboa.tecnico.cmov.airdesk.DTO.WorkspaceDTO;

/**
 * Created by Filipe Teixeira on 09/05/2015.
 */
public class RemoveInviteMessage extends Message {
    //region Class Variables
    private String workspace;
    //endregion

    //region Constructor
    public RemoveInviteMessage(String user, String workspace) {
        super(Type.REMOVE_INVITE, user);
        this.workspace = workspace;
    }
    //endregion

    //region Getters
    public String getWorkspaceName() {
        return this.workspace;
    }
    //endregion

    @Override
    public JSONObject toJSON() throws JSONException {
        JSONObject result = new JSONObject();
        result.put(MESSAGE_USER, this.getUser());
        result.put(MESSAGE_TYPE, this.getTypeOfMessage());
        result.put(MESSAGE_WORKSPACE, this.getWorkspaceName());
        return result;
    }
}
