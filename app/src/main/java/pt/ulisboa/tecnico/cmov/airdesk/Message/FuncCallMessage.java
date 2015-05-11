package pt.ulisboa.tecnico.cmov.airdesk.Message;

import org.json.JSONException;
import org.json.JSONObject;

import pt.ulisboa.tecnico.cmov.airdesk.Exception.MessageParsingException;

/**
 * Created by Filipe Teixeira on 10/05/2015.
 */
public class FuncCallMessage extends Message{
    private FuncType typeOfFunction;
    private String arg1;
    private String arg2;
    private int argNumber;

    public enum FuncType{
        CREATE_FILE, UPDATE_FILE, REMOVE_FILE;
    }

    public static FuncType stringToFuncEnum(String type) throws MessageParsingException {
        if(type.equals("CREATE_FILE")){
            return FuncType.CREATE_FILE;
        } else if(type.equals("UPDATE_FILE"))
            return FuncType.UPDATE_FILE;
        else if(type.equals("REMOVE_FILE"))
            return FuncType.REMOVE_FILE;
        else throw new MessageParsingException("No known type = " + type);
    }

    public FuncCallMessage(FuncType typeOfFunction, String user) {
        super(Message.Type.FUNC_CALL, user);
        this.typeOfFunction = typeOfFunction;
        this.argNumber = 0;
    }

    public FuncCallMessage(FuncType typeOfFunction, String user, String arg1) {
        super(Message.Type.FUNC_CALL, user);
        this.typeOfFunction = typeOfFunction;
        this.arg1 = arg1;
        this.argNumber = 1;
    }

    public FuncCallMessage(FuncType typeOfFunction, String user, String arg1, String arg2) {
        super(Message.Type.FUNC_CALL, user);
        this.typeOfFunction = typeOfFunction;
        this.arg1 = arg1;
        this.arg2 = arg2;
        this.argNumber = 2;
    }

    public FuncType getTypeOfFunction() {
        return typeOfFunction;
    }

    public String getArg1() {
        return arg1;
    }

    public String getArg2() {
        return arg2;
    }

    public FuncResponseMessage execute(){
        FuncResponseMessage funcResponseMessage = new FuncResponseMessage("error", false, "2");
        return funcResponseMessage;
    }

    @Override
    public JSONObject toJSON() throws JSONException {
        JSONObject result = new JSONObject();
        result.put(MESSAGE_TYPE, Type.FUNC_CALL);
        result.put(MESSAGE_FUNC_TYPE, this.getTypeOfFunction());
        if(argNumber>0){
            result.put(MESSAGE_ARG1, arg1);
            if(argNumber == 2)
                result.put(MESSAGE_ARG2, arg2);
        }
        return result;
    }



}
