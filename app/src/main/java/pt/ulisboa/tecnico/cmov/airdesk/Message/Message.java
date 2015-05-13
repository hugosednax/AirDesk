package pt.ulisboa.tecnico.cmov.airdesk.Message;

import org.json.JSONException;
import org.json.JSONObject;

import pt.ulisboa.tecnico.cmov.airdesk.Exception.MessageParsingException;

/**
 * Created by Toninho on 5/2/2015.
 */
public abstract class Message {
    //region Message TAG Declaration
    public static final String MESSAGE_TYPE = "TYPE";
    public static final String MESSAGE_USER = "USER";
    public static final String MESSAGE_WORKSPACE = "WORKSPACE";
    public static final String MESSAGE_FUNC_TYPE = "FUNC_TYPE";
    public static final String MESSAGE_ARG1 = "ARG1";
    public static final String MESSAGE_ARG2 = "ARG2";
    public static final String MESSAGE_ARG3 = "ARG3";
    public static final String MESSAGE_EXCEPTION_THROWN = "EXCEPTION_THROWN";
    public static final String MESSAGE_EXCEPTION_MESSAGE = "EXCEPTION";
    public static final String MESSAGE_EXCEPTION_NAME = "EXCEPTION_NAME";
    public static final String MESSAGE_RESULT = "RESULT";
    public static final String MESSAGE_KEYWORDS = "KEYWORDS";
    //endregion

    //region Class Variables
    private Type typeOfMessage;
    private String user;
    //endregion

    //region Type Declaration and Methods
    public static Type stringToEnum(String type) throws MessageParsingException {
        if(type.equals("FUNC_CALL")){
            return Type.FUNC_CALL;
        } else if(type.equals("FUNC_RESP"))
            return Type.FUNC_RESP;
        else if(type.equals("INVITE"))
            return Type.INVITE;
        else if(type.equals("INTEREST"))
            return Type.INTEREST;
        else throw new MessageParsingException("No known type = " + type);
    }

    public enum Type{
        FUNC_CALL, FUNC_RESP, INVITE, INTEREST;
    }
    //endregion

    //region Constructor
    public Message(Type typeOfMessage, String user){
        this.user = user;
        this.typeOfMessage = typeOfMessage;
    }
    //endregion

    //region Getters
    public Type getTypeOfMessage() {
        return typeOfMessage;
    }

    public String getUser() {
        return user;
    }
    //endregion

    //region Abstract Methods
    public abstract JSONObject toJSON() throws JSONException;
    //endregion
}
