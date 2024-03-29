package pt.ulisboa.tecnico.cmov.airdesk.Workspace;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import pt.ulisboa.tecnico.cmov.airdesk.DTO.WorkspaceDTO;
import pt.ulisboa.tecnico.cmov.airdesk.Exception.FileNotFoundException;
import pt.ulisboa.tecnico.cmov.airdesk.Exception.CreateFileException;
import pt.ulisboa.tecnico.cmov.airdesk.Exception.DeleteFileException;
import pt.ulisboa.tecnico.cmov.airdesk.Exception.NotDirectoryException;
import pt.ulisboa.tecnico.cmov.airdesk.Exception.QuotaLimitExceededException;
import pt.ulisboa.tecnico.cmov.airdesk.Exception.RemoteMethodException;
import pt.ulisboa.tecnico.cmov.airdesk.FileSystem.ADFile;
import pt.ulisboa.tecnico.cmov.airdesk.Message.FuncCallMessage;
import pt.ulisboa.tecnico.cmov.airdesk.Message.FuncResponseMessage;
import pt.ulisboa.tecnico.cmov.airdesk.WiFiDirect.WifiNotificationHandler;
/**
 * Created by Filipe Teixeira
 */
// 2nd version of ForeignWorkspace, Remote
public class ForeignRemoteWorkspace extends Workspace{
    //region Class Const
    private static final String TAG ="[AirDesk]";
    //endregion

    //region Class Variables
    String owner;
    WifiNotificationHandler wifiHandler;
    String myUser;
    //endregion

    //region Constructors
    public ForeignRemoteWorkspace(WifiNotificationHandler wifiHandler, WorkspaceDTO workspaceDTO, String owner){
        super(workspaceDTO.getName() + "@" + owner);
        this.owner = owner;
        this.wifiHandler = wifiHandler;
        this.myUser = wifiHandler.getMyUserEmail();
    }
    //endregion


    public String getOwner() {
        return owner;
    }

    //region File Methods
    public String getFileContent(String filename) throws FileNotFoundException {
        Log.d(TAG, "Calling Remote getFileContent");

        FuncCallMessage newFuncCallMessage = new FuncCallMessage(FuncCallMessage.FuncType.GET_FILE_CONTENT, myUser, this.getName(), filename);

        try {
            FuncResponseMessage response = wifiHandler.remoteMethodInvoke(owner, newFuncCallMessage);
            if(response.isExceptionThrown()){
                if(response.getExceptionName().equals("FileNotFoundException"))
                    throw new FileNotFoundException(response.getExceptionMessage());
            } else
                return response.getResult();
        }catch (JSONException | RemoteMethodException e) {
            Log.d(TAG, "In remote getFileNames " + e.getMessage());
        }
        return "Error accessing the File Owner";
    }

    @Override
    public List<String> getFileNames() {
        Log.d(TAG, "Calling Remote getFileNames");

        FuncCallMessage newFuncCallMessage = new FuncCallMessage(FuncCallMessage.FuncType.GET_FILE_NAMES, myUser, this.getName());
        List<String> result = new ArrayList<>();
        try {
            FuncResponseMessage response = wifiHandler.remoteMethodInvoke(owner, newFuncCallMessage);
            String list = response.getResult();
            JSONArray jsonList = (new JSONObject(list)).getJSONArray("LIST");
            for(int i = 0; i<jsonList.length(); i++){
                result.add(jsonList.getString(i));
            }
        }catch (JSONException | RemoteMethodException e) {
            Log.d(TAG, "In remote getFileNames " + e.getMessage());
        }
        return result;
    }

    public void createFile(String fileName) throws QuotaLimitExceededException, CreateFileException {
        Log.d(TAG, "Calling Remote createFile " + fileName);
        FuncCallMessage newFuncCallMessage = new FuncCallMessage(FuncCallMessage.FuncType.CREATE_FILE, myUser, this.getName(), fileName);

        try {
            FuncResponseMessage response = wifiHandler.remoteMethodInvoke(owner, newFuncCallMessage);
            if(response.isExceptionThrown()){
                if(response.getExceptionName().equals("QuotaLimitExceededException"))
                    throw new QuotaLimitExceededException(response.getExceptionMessage());
                if(response.getExceptionName().equals("CreateFileException"))
                    throw new CreateFileException(response.getExceptionMessage());
            }

        }catch (JSONException | RemoteMethodException e) {
            Log.d(TAG, "In remote getFileNames " + e.getMessage());
        }
    }

    public void removeFile(String filename) throws FileNotFoundException, DeleteFileException {
        Log.d(TAG, "Calling Remote removeFile");

        FuncCallMessage newFuncCallMessage = new FuncCallMessage(FuncCallMessage.FuncType.REMOVE_FILE, myUser, this.getName(), filename);

        try {
            FuncResponseMessage response = wifiHandler.remoteMethodInvoke(owner, newFuncCallMessage);
            if(response.isExceptionThrown()){
                if(response.getExceptionName().equals("FileNotFoundException"))
                    throw new FileNotFoundException(response.getExceptionMessage());
                if(response.getExceptionName().equals("DeleteFileException"));
                    throw new DeleteFileException(response.getExceptionMessage());
            }
        }catch (JSONException | RemoteMethodException e) {
            Log.d(TAG, "In remote getFileNames " + e.getMessage());
        }
    }

    public void updateFile(String filename, String text) throws QuotaLimitExceededException, FileNotFoundException {
        Log.d(TAG, "Calling Remote updateFile");

        FuncCallMessage newFuncCallMessage = new FuncCallMessage(FuncCallMessage.FuncType.UPDATE_FILE, myUser, this.getName(), filename, text);

        try {
            FuncResponseMessage response = wifiHandler.remoteMethodInvoke(owner, newFuncCallMessage);
            if(response.isExceptionThrown()){
                if(response.getExceptionName().equals("QuotaLimitExceededException"))
                    throw new QuotaLimitExceededException(response.getExceptionMessage());
                if(response.getExceptionName().equals("FileNotFoundException"))
                    throw new FileNotFoundException(response.getExceptionMessage());
            }
        }catch (JSONException | RemoteMethodException e) {
            Log.d(TAG, "In remote getFileNames " + e.getMessage());
        }
    }

    public boolean editable(String filename) throws FileNotFoundException {
        Log.d(TAG, "Calling Remote editable");
        boolean result = false;
        FuncCallMessage newFuncCallMessage = new FuncCallMessage(FuncCallMessage.FuncType.EDITABLE, myUser, this.getName(), filename);
        try {
            FuncResponseMessage response = wifiHandler.remoteMethodInvoke(owner, newFuncCallMessage);
            if(response.isExceptionThrown()){
                if(response.getExceptionName().equals("FileNotFoundException"))
                    throw new FileNotFoundException(response.getExceptionMessage());
            } else {
                if(response.getResult().equals("true"))
                    result = true;
            }
        }catch (JSONException | RemoteMethodException e) {
            Log.d(TAG, "In remote getFileNames " + e.getMessage());
        }
        return result;
    }

    public void setEditable(String filename) throws FileNotFoundException {
        Log.d(TAG, "Calling Remote setEditable");
        FuncCallMessage newFuncCallMessage = new FuncCallMessage(FuncCallMessage.FuncType.CANCEL_EDIT, myUser, this.getName(), filename);
        try {
            FuncResponseMessage response = wifiHandler.remoteMethodInvoke(owner, newFuncCallMessage);
            if(response.isExceptionThrown()){
                if(response.getExceptionName().equals("FileNotFoundException"))
                    throw new FileNotFoundException(response.getExceptionMessage());
            }
        }catch (JSONException | RemoteMethodException e) {
            Log.d(TAG, "In remote getFileNames " + e.getMessage());
        }
    }
    //endregion

    //region Workspace Methods
    public void delete() throws NotDirectoryException {
        //TODO
    }
    //endregion
}
