package pt.ulisboa.tecnico.cmov.airdesk.Message;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import pt.ulisboa.tecnico.cmov.airdesk.DTO.WorkspaceDTO;

/**
 * Created by Filipe Teixeira on 09/05/2015.
 */
public class InterestMessage extends Message {
    //region Class Variables
    private List<String> keywords;
    //endregion

    //region Constructor
    public InterestMessage(String user, List<String> keywords) {
        super(Type.INTEREST, user);
        this.keywords = keywords;
    }
    //endregion

    //region Getters
    public List<String> getKeywords() {
        return keywords;
    }
    //endregion

    @Override
    public JSONObject toJSON() throws JSONException {
        JSONObject result = new JSONObject();
        result.put(MESSAGE_USER, this.getUser());
        result.put(MESSAGE_TYPE, this.getTypeOfMessage());
        result.put(MESSAGE_KEYWORDS, new JSONArray(getKeywords()));
        return result;
    }
}
