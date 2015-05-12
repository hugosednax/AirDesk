package pt.ulisboa.tecnico.cmov.airdesk.Message;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import pt.ulisboa.tecnico.cmov.airdesk.Exception.FileNotFoundException;
import pt.ulisboa.tecnico.cmov.airdesk.Exception.CreateFileException;
import pt.ulisboa.tecnico.cmov.airdesk.Exception.DeleteFileException;
import pt.ulisboa.tecnico.cmov.airdesk.Exception.MessageParsingException;
import pt.ulisboa.tecnico.cmov.airdesk.Exception.NotDirectoryException;
import pt.ulisboa.tecnico.cmov.airdesk.Exception.QuotaLimitExceededException;
import pt.ulisboa.tecnico.cmov.airdesk.Exception.WorkspaceNotFoundException;
import pt.ulisboa.tecnico.cmov.airdesk.User.User;

/**
 * Created by Filipe Teixeira on 10/05/2015.
 */
public class FuncCallMessage extends Message{
    private FuncType typeOfFunction;
    private String arg1;
    private String arg2;
    private String arg3;
    private int argNumber;

    public enum FuncType{
        CREATE_FILE, UPDATE_FILE, REMOVE_FILE, GET_FILE_NAMES;
    }

    public static FuncType stringToFuncEnum(String type) throws MessageParsingException {
        if(type.equals("CREATE_FILE")){
            return FuncType.CREATE_FILE;
        } else if(type.equals("UPDATE_FILE"))
            return FuncType.UPDATE_FILE;
        else if(type.equals("REMOVE_FILE"))
            return FuncType.REMOVE_FILE;
        else if(type.equals("GET_FILE_NAMES"))
            return FuncType.GET_FILE_NAMES;
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

    public FuncCallMessage(FuncType typeOfFunction, String user, String arg1, String arg2, String arg3) {
        super(Message.Type.FUNC_CALL, user);
        this.typeOfFunction = typeOfFunction;
        this.arg1 = arg1;
        this.arg2 = arg2;
        this.arg3 = arg3;
        this.argNumber = 3;
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

    public String getArg3() {
        return arg3;
    }

    public FuncResponseMessage execute(User user){
        FuncResponseMessage funcResponseMessage = null;
        if(this.getTypeOfFunction() == FuncType.CREATE_FILE){
            arg1 = parseWSName(arg1);
            Log.d("[AirDesk]", "Executing createFile with parameters: " + getArg1() + " " + getArg2());
            //arg1 = workspaceName, arg2 = filename
            try {
                user.createFile(getArg2(), getArg1());
                return new FuncResponseMessage(getUser(), false, "");
            } catch (WorkspaceNotFoundException e) {
                return new FuncResponseMessage(getUser(), true, "CreateFileException", e.getMessage());
            } catch (QuotaLimitExceededException e) {
                return new FuncResponseMessage(getUser(), true, "QuotaLimitExceededException", e.getMessage());
            } catch (CreateFileException e) {
                return new FuncResponseMessage(getUser(), true, "CreateFileException", e.getMessage());
            }

        } else if(this.getTypeOfFunction() == FuncType.UPDATE_FILE){
            //arg1 = workspaceName, arg2 = filename, arg3 = text
            arg1 = parseWSName(arg1);
            Log.d("[AirDesk]", "Executing createFile with parameters: " + getArg1() + " " + getArg2());
            try {
                user.getOwnedWorkspaceByName(getArg1()).updateFile(getArg2(), getArg3());
                return new FuncResponseMessage(getUser(), false, "");
            } catch (FileNotFoundException e) {
                return new FuncResponseMessage(getUser(), true, "FileNotFoundException", e.getMessage());
            } catch (QuotaLimitExceededException e) {
                return new FuncResponseMessage(getUser(), true, "QuotaLimitExceededException", e.getMessage());
            } catch (WorkspaceNotFoundException e) {
                return new FuncResponseMessage(getUser(), true, "FileNotFoundException", e.getMessage());
            }

        } else if(this.getTypeOfFunction() == FuncType.REMOVE_FILE) {
            //arg1 = workspaceName, arg2 = filename
            arg1 = parseWSName(arg1);
            Log.d("[AirDesk]", "Executing remove file with parameters: " + getArg1() + " " + getArg2());
            try {
                user.getOwnedWorkspaceByName(getArg1()).removeFile(getArg2());
                return new FuncResponseMessage(getUser(), false, "");
            } catch (FileNotFoundException e) {
                return new FuncResponseMessage(getUser(), true, "FileNotFoundException", e.getMessage());
            } catch (DeleteFileException e) {
                return new FuncResponseMessage(getUser(), true, "DeleteFileException", e.getMessage());
            } catch (WorkspaceNotFoundException e) {
                return new FuncResponseMessage(getUser(), true, "FileNotFoundException", e.getMessage());
            }
        } else if(this.getTypeOfFunction() == FuncType.GET_FILE_NAMES){
            Log.d("[AirDesk]", "Executing getFileNames");
            try {
                List<String> list = user.getOwnedWorkspaceByName(getArg1()).getFileNames();
                JSONArray jsonList = new JSONArray();
                for(String s : list)
                    jsonList.put(s);
                return new FuncResponseMessage(getUser(), false, (new JSONObject().put("LIST", jsonList)).toString());
            } catch (WorkspaceNotFoundException e) {
                return new FuncResponseMessage(getUser(), true, "FileNotFoundException", e.getMessage());

            } catch (JSONException e) {
                //TODO
            }

        }
        return funcResponseMessage;
    }

    private String parseWSName(String arg1) {
        String delimit = "[@]";
        String[] tokens = arg1.split(delimit);
        return tokens[0];
    }

    @Override
    public JSONObject toJSON() throws JSONException {
        JSONObject result = new JSONObject();
        result.put(MESSAGE_USER, this.getUser());
        result.put(MESSAGE_TYPE, Type.FUNC_CALL);
        result.put(MESSAGE_FUNC_TYPE, this.getTypeOfFunction());
        if(argNumber>0) {
            result.put(MESSAGE_ARG1, arg1);
            if (argNumber > 1) {
                result.put(MESSAGE_ARG2, arg2);
                if(argNumber == 3)
                    result.put(MESSAGE_ARG3, arg3);
            }
        }
        return result;
    }



}
