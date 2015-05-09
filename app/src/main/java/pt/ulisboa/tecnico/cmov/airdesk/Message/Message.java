package pt.ulisboa.tecnico.cmov.airdesk.Message;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Toninho on 5/2/2015.
 */
public abstract class Message {

    public static final String MESSAGE_TYPE = "TYPE";
    public static final String MESSAGE_USER = "USER";
    public static final String MESSAGE_WORKSPACE = "WORKSPACE";

    Type typeOfMessage;
    public enum Type{
        IMHERE, INVITE, INHERE, IGOTTHIS, INTEREST, UGOTTHIS, CONNECTO;
    }

    public Message(Type typeOfMessage){
        this.typeOfMessage = typeOfMessage;
    }

    public Type getTypeOfMessage() {
        return typeOfMessage;
    }

    public void setTypeOfMessage(Type typeOfMessage) {
        this.typeOfMessage = typeOfMessage;
    }

    public abstract JSONObject toJSON() throws JSONException;
}
