package pt.ulisboa.tecnico.cmov.airdesk.Message;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Filipe Teixeira on 09/05/2015.
 */
public class ImHereMessage extends Message {

    public ImHereMessage(String user) {
        super(Type.IMHERE, user);
    }

    @Override
    public JSONObject toJSON() throws JSONException {
        JSONObject result = new JSONObject();
        result.put(MESSAGE_TYPE, this.getTypeOfMessage());
        result.put(MESSAGE_USER, this.getUser());
        return result;
    }
}
