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
// 2nd version of ForeignWorkspace, remote
public class ForeignRemoteWorkspace extends Workspace{

    private static final String TAG ="[AirDesk]";

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

    //region File Methods
    public String getFileContent(String filename) throws FileNotFoundException {
        Log.d(TAG, "Calling Remote getFileContent");
        //FuncCallMessage newFuncCallMessage = new FuncCallMessage(owner, filename);
        return null;
    }

    @Override
    public List<String> getFileNames() {
        Log.d(TAG, "Calling Remote getFileNames");

        FuncCallMessage newFuncCallMessage = new FuncCallMessage(FuncCallMessage.FuncType.GET_FILE_NAMES, myUser, parseWSName(this.getName()));
        List<String> result = new ArrayList<>();
        try {
            FuncResponseMessage response = wifiHandler.remoteMethodInvoke(owner, newFuncCallMessage);
            String list = response.getResult();
            JSONArray jsonList = (new JSONObject(list)).getJSONArray("LIST");
            for(int i = 0; i<jsonList.length(); i++){
                result.add(jsonList.getString(i));
            }
        } catch (JSONException e) {
            Log.d(TAG, "In remote getFileNames " + e.getMessage());
        } catch (RemoteMethodException e) {
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

        }catch(RemoteMethodException e){
            //throw new RemoteMethodException();
        }catch(JSONException e) {
            //help
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
        }catch(RemoteMethodException e){
            //throw new RemoteMethodException();
        }catch(JSONException e) {
            //help
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
        }catch(RemoteMethodException e){
            //TODO throw new RemoteMethodException();
        }catch(JSONException e) {
            //TODO help
        }
    }
    //endregion

    //region Workspace Methods
    public void delete() throws NotDirectoryException {
        //TODO
    }
    //endregion

    private String parseWSName(String arg1) {
        String delimit = "[@]";
        String[] tokens = arg1.split(delimit);
        return tokens[0];
    }
}
