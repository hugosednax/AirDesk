package pt.ulisboa.tecnico.cmov.airdesk.Message;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Filipe Teixeira on 10/05/2015.
 */
public class FuncResponseMessage extends Message{
    private boolean exceptionThrown;
    // If exception is thrown, then result is the name of the exception
    private String result;


    public FuncResponseMessage(String user, boolean exceptionThrown, String result) {
        super(Type.FUNC_RESP, user);
        this.exceptionThrown = exceptionThrown;
        this.result = result;
    }

    public boolean isExceptionThrown() {
        return exceptionThrown;
    }

    public String getResult() {
        return result;
    }

    @Override
    public JSONObject toJSON() throws JSONException {
        JSONObject result = new JSONObject();
        result.put(MESSAGE_EXCEPTION, isExceptionThrown());
        result.put(MESSAGE_RESULT, getResult());
        return result;
    }
}
