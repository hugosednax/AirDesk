package pt.ulisboa.tecnico.cmov.airdesk.Message;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Filipe Teixeira on 10/05/2015.
 */
public class FuncResponseMessage extends Message{
    //region Class Variables
    private boolean exceptionThrown;
    private String result;
    private String exceptionMessage;
    private String exceptionName;
    //endregion

    //region Constructors
    public FuncResponseMessage(String user, boolean exceptionThrown, String result) {
        super(Type.FUNC_RESP, user);
        this.exceptionThrown = exceptionThrown;
        this.result = result;
    }

    public FuncResponseMessage(String user, boolean exceptionThrown, String exceptionMessage, String exceptionName) {
        super(Type.FUNC_RESP, user);
        this.exceptionThrown = exceptionThrown;
        this.exceptionMessage = exceptionMessage;
        this.exceptionName = exceptionName;
    }
    //endregion

    //region Getters
    public boolean isExceptionThrown() {
        return exceptionThrown;
    }

    public String getResult() {
        return result;
    }

    public String getExceptionMessage() {
        return exceptionMessage;
    }

    public String getExceptionName() {
        return exceptionName;
    }
    //endregion

    @Override
    public JSONObject toJSON() throws JSONException {
        JSONObject result = new JSONObject();
        result.put(MESSAGE_TYPE, Type.FUNC_RESP);
        result.put(MESSAGE_EXCEPTION_THROWN, isExceptionThrown());
        if(exceptionThrown) {
            result.put(MESSAGE_EXCEPTION_MESSAGE, this.getExceptionMessage());
            result.put(MESSAGE_EXCEPTION_NAME, this.exceptionName);
        }
        else result.put(MESSAGE_RESULT, getResult());
        return result;
    }
}
