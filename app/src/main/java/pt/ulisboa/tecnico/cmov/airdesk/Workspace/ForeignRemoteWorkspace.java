package pt.ulisboa.tecnico.cmov.airdesk.Workspace;

import android.widget.Toast;

import org.json.JSONException;

import java.io.IOException;
import java.util.List;

import pt.ulisboa.tecnico.cmov.airdesk.DTO.WorkspaceDTO;
import pt.ulisboa.tecnico.cmov.airdesk.Exception.ADFileNotFoundException;
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
 * Created by Filipe Teixeira on 12/03/2015.
 */

// 2nd version of ForeignWorkspace, remote
public class ForeignRemoteWorkspace extends Workspace{

    //region Class Variables
    String owner;
    String ip;
    WifiNotificationHandler wifiHandler;
    String myUser;
    //endregion

    //region Constructors
    public ForeignRemoteWorkspace(WifiNotificationHandler wifiHandler, WorkspaceDTO workspaceDTO, String owner, String ip){
        super(workspaceDTO.getName() + "@" + owner);
        this.owner = owner;
        this.ip = ip;
        this.wifiHandler = wifiHandler;
        this.myUser = wifiHandler.getMyUser();
    }
    //endregion

    //region Getters

    public int getSize() throws NotDirectoryException {
        //TODO
        return 0;
    }
    //endregion

    //region Setters
    @Override
    public void setQuota(int quota) throws NotDirectoryException, QuotaLimitExceededException {
       //TODO impossible
    }

    @Override
    public boolean hasKeyword(String keyword) {
        //TODO impossible
        return false;
    }
    //endregion

    //region File Methods
    @Override
    public ADFile getFileByName(String filename) throws ADFileNotFoundException {
        //FuncCallMessage newFuncCallMessage = new FuncCallMessage(owner, filename);
        return null;
    }

    public void createFile(String fileName) throws QuotaLimitExceededException, IOException, CreateFileException {
        FuncCallMessage newFuncCallMessage = new FuncCallMessage(FuncCallMessage.FuncType.CREATE_FILE, myUser, fileName);

        try {
            FuncResponseMessage response = wifiHandler.remoteMethodInvoke(owner, newFuncCallMessage);
            if(response.isExceptionThrown()){
                Exception exc = response.getException();
                Class<?> excClass = exc.getClass();

                if(excClass.equals(QuotaLimitExceededException.class)) {
                    throw new QuotaLimitExceededException(exc.getMessage());
                }else if(excClass.equals(IOException.class)){
                    throw new IOException(exc.getMessage());
                }else if(excClass.equals(CreateFileException.class)){
                    throw new CreateFileException(exc.getMessage());
                }
            }

        }catch(RemoteMethodException e){
            //throw new RemoteMethodException();
        }catch(JSONException e) {
            //welp
        }
    }

    public void removeFile(String filename) throws ADFileNotFoundException, DeleteFileException {
        FuncCallMessage newFuncCallMessage = new FuncCallMessage(FuncCallMessage.FuncType.REMOVE_FILE, myUser, filename);

        try {
            FuncResponseMessage response = wifiHandler.remoteMethodInvoke(owner, newFuncCallMessage);
            if(response.isExceptionThrown()){
                Exception exc = response.getException();
                Class<?> excClass = exc.getClass();

                if(excClass.equals(ADFileNotFoundException.class)) {
                    throw new ADFileNotFoundException(exc.getMessage());
                }else if(excClass.equals(DeleteFileException.class)) {
                    throw new DeleteFileException(exc.getMessage());
                }
            }
        }catch(RemoteMethodException e){
            //throw new RemoteMethodException();
        }catch(JSONException e) {
            //welp
        }
    }

    public void updateFile(String filename, String text) throws QuotaLimitExceededException, ADFileNotFoundException, NotDirectoryException {
        FuncCallMessage newFuncCallMessage = new FuncCallMessage(FuncCallMessage.FuncType.UPDATE_FILE, myUser, filename, text);

        try {
            FuncResponseMessage response = wifiHandler.remoteMethodInvoke(owner, newFuncCallMessage);
            if(response.isExceptionThrown()){
                Exception exc = response.getException();
                Class<?> excClass = exc.getClass();

                if(excClass.equals(ADFileNotFoundException.class)) {
                    throw new ADFileNotFoundException(exc.getMessage());
                }else if(excClass.equals(QuotaLimitExceededException.class)) {
                    throw new QuotaLimitExceededException(exc.getMessage());
                }else if(excClass.equals(NotDirectoryException.class)) {
                    throw new NotDirectoryException(exc.getMessage());
                }
            }
        }catch(RemoteMethodException e){
            //throw new RemoteMethodException();
        }catch(JSONException e) {
            //welp
        }
    }
    //endregion

    //region Workspace Methods
    public void delete() throws NotDirectoryException {
        //TODO
    }
    //endregion
}
