package pt.ulisboa.tecnico.cmov.airdesk.Message;

import org.json.JSONException;
import org.json.JSONObject;

import pt.ulisboa.tecnico.cmov.airdesk.Exception.MessageParsingException;

/**
 * Created by Toninho on 5/2/2015.
 */
public abstract class Message {

    public static final String MESSAGE_TYPE = "TYPE";
    public static final String MESSAGE_USER = "USER";
    public static final String MESSAGE_WORKSPACE = "WORKSPACE";
    public static final String MESSAGE_FUNC_TYPE = "FUNC_TYPE";
    public static final String MESSAGE_ARG1 = "ARG1";
    public static final String MESSAGE_ARG2 = "ARG2";
    public static final String MESSAGE_EXCEPTION_THROWN = "EXCEPTION_THROWN";
    public static final String MESSAGE_EXCEPTION = "EXCEPTION";
    public static final String MESSAGE_RESULT = "RESULT";


    private Type typeOfMessage;
    private String user;

    public static Type stringToEnum(String type) throws MessageParsingException {
        if(type.equals("FUNC_CALL")){
            return Type.FUNC_CALL;
        } else if(type.equals("FUNC_RESP"))
            return Type.FUNC_RESP;
        else if(type.equals("INVITE"))
            return Type.INVITE;
        else throw new MessageParsingException("No known type = " + type);
    }

    public enum Type{
        FUNC_CALL, FUNC_RESP, INVITE;
    }

    public Message(Type typeOfMessage, String user){
        this.user = user;
        this.typeOfMessage = typeOfMessage;
    }

    public Type getTypeOfMessage() {
        return typeOfMessage;
    }


    public String getUser() {
        return user;
    }

    public void setTypeOfMessage(Type typeOfMessage) {
        this.typeOfMessage = typeOfMessage;
    }

    public abstract JSONObject toJSON() throws JSONException;
}
