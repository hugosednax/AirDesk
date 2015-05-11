package pt.ulisboa.tecnico.cmov.airdesk.Message;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Filipe Teixeira on 10/05/2015.
 */
public class FuncResponseMessage extends Message{
    private boolean exceptionThrown;
    private String result;
    private Exception exception;


    public FuncResponseMessage(String user, boolean exceptionThrown, String result) {
        super(Type.FUNC_RESP, user);
        this.exceptionThrown = exceptionThrown;
        this.result = result;
    }

    public FuncResponseMessage(String user, boolean exceptionThrown, Exception exception) {
        super(Type.FUNC_RESP, user);
        this.exceptionThrown = exceptionThrown;
        this.exception = exception;
    }

    public boolean isExceptionThrown() {
        return exceptionThrown;
    }

    public String getResult() {
        return result;
    }

    public Exception getException() {
        return exception;
    }

    @Override
    public JSONObject toJSON() throws JSONException {
        JSONObject result = new JSONObject();
        result.put(MESSAGE_TYPE, Type.FUNC_RESP);
        result.put(MESSAGE_EXCEPTION_THROWN, isExceptionThrown());
        if(exceptionThrown)
            result.put(MESSAGE_EXCEPTION, this.getException().getMessage());
        else result.put(MESSAGE_RESULT, getResult());
        return result;
    }
}
