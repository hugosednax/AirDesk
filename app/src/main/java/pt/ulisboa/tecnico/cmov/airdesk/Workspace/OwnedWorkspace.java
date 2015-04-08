package pt.ulisboa.tecnico.cmov.airdesk.Workspace;

import android.content.Context;
import android.util.Log;

import com.android.internal.util.Predicate;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import pt.ulisboa.tecnico.cmov.airdesk.Application.AirDeskApp;
import pt.ulisboa.tecnico.cmov.airdesk.DTO.WorkspaceDTO;
import pt.ulisboa.tecnico.cmov.airdesk.Exception.ADFileNotFoundException;
import pt.ulisboa.tecnico.cmov.airdesk.Exception.CreateFileException;
import pt.ulisboa.tecnico.cmov.airdesk.Exception.CreateWorkspaceException;
import pt.ulisboa.tecnico.cmov.airdesk.Exception.DeleteFileException;
import pt.ulisboa.tecnico.cmov.airdesk.Exception.NotDirectoryException;
import pt.ulisboa.tecnico.cmov.airdesk.Exception.QuotaLimitExceededException;
import pt.ulisboa.tecnico.cmov.airdesk.FileSystem.ADFile;
import pt.ulisboa.tecnico.cmov.airdesk.Predicate.FileNamePredicate;
import pt.ulisboa.tecnico.cmov.airdesk.User.User;

/**
 * Created by Filipe Teixeira on 12/03/2015.
 */
public class OwnedWorkspace extends Workspace{

    //region Class Variables
    private List<String> allowedUsers;
    private boolean isPublic;
    //endregion

    //region Constructors
    public OwnedWorkspace(String name, boolean isPublic, int quota) throws CreateWorkspaceException {
        super(name);
        this.isPublic = isPublic;
        this.quota = quota;
        this.allowedUsers = new ArrayList<String>();
        File mainDir = AirDeskApp.getAppContext().getDir("data", Context.MODE_PRIVATE);
        File currentDir = new File(""+mainDir+File.separatorChar+name);
        currentDir.mkdir();
        if(!currentDir.isDirectory())
            throw new CreateWorkspaceException("Can't create a new directory for this Workspace");
    }

    public OwnedWorkspace(WorkspaceDTO workspaceDTO) {
        super(workspaceDTO.getName());
        this.isPublic = workspaceDTO.isPublic();
        this.quota = workspaceDTO.getQuota();
        this.allowedUsers = new ArrayList<String>();

        for(String username : workspaceDTO.getAllowedUsers())
            allowedUsers.add(username);

        File mainDir = AirDeskApp.getAppContext().getDir("data", Context.MODE_PRIVATE);
        File currentDir = new File(""+mainDir+File.separatorChar+name);

        if(currentDir.isDirectory()){
            for(File file : currentDir.listFiles()){
                ADFile savedFile = new ADFile(file);
                getFiles().add(savedFile);
                Log.d(AirDeskApp.LOG_TAG, "Loaded file: " + file.getName() + " from memory to app");
            }
        } else currentDir.mkdir();
    }
    //endregion

    //region Getters
    public boolean isPublic() {
        return isPublic;
    }

    public List<String> getAllowedUsers() {
        return allowedUsers;
    }

    public int getSize() throws NotDirectoryException {
        File mainDir = AirDeskApp.getAppContext().getDir("data", Context.MODE_PRIVATE);
        File currentDir = new File(""+mainDir+File.separatorChar+name);
        int workspaceSize = 0;

        if(currentDir.listFiles().length != files.size())
            Log.d(AirDeskApp.LOG_TAG, "File inconsistency noted.");

        if (currentDir.isDirectory())
            for (File child : currentDir.listFiles())
                workspaceSize += child.length();
        else throw new NotDirectoryException("Can't read workspace, the provided name isn't a directory.");

        return workspaceSize;
    }
    //endregion

    //region File Functions
    public void createFile(String fileName) throws QuotaLimitExceededException, CreateFileException, IOException {
        try {
            if(this.getSize() >= getQuota()){
                throw new QuotaLimitExceededException("Quota limit exceeded while trying to create " + fileName + " in " + this.getName() + " your Workspace.");
            } else {
                files.add(new ADFile(fileName, this.getName()));
            }
        } catch (NotDirectoryException e) {
            Log.d(AirDeskApp.LOG_TAG, e.getMessage());
            throw new CreateFileException("Problem occurred in getting workspace total size. See log for more info.");
            }
    }

    public void removeFile(String name) throws ADFileNotFoundException, DeleteFileException {
        ADFile file = getFileByName(name);
        files.remove(file);

        if(!file.getFile().delete())
            throw new DeleteFileException("Can't delete file in Android File System");
    }

    public void updateFile(String name, String text) throws ADFileNotFoundException, NotDirectoryException, QuotaLimitExceededException {
        ADFile file = getFileByName(name);
        if(this.getSize() - file.getSize() + text.length() > this.getQuota())
            throw new QuotaLimitExceededException("Quota limit exceeded while trying to update " + name + " in " + this.getName() + " your Workspace.");
        file.save(text);
    }

    public ADFile getFileByName(String name) throws ADFileNotFoundException {
        Predicate<ADFile> validator = new FileNamePredicate(name);
        ADFile result = null;
        for(ADFile file : getFiles())
            if (validator.apply(file)) {
                result = file;
                break;
            }
        if(result == null)
            throw new ADFileNotFoundException("File " + name + " not found in " + this.getName() + " Workspace.");
        return result;
    }
    //endregion

    //region Workspace Functions
    public void delete() throws NotDirectoryException{
        File mainDir = AirDeskApp.getAppContext().getDir("data", AirDeskApp.getAppContext().MODE_PRIVATE);
        File directory = new File(""+mainDir+File.separatorChar+name);
        if (directory.isDirectory())
            for (File child : directory.listFiles())
                if(!child.delete())
                    Log.d(AirDeskApp.LOG_TAG, "Error at deleting file: " + child.getName());
        else throw new NotDirectoryException("Can't delete workspace, the provided name isn't a directory.");
        if(!directory.delete())
            Log.d(AirDeskApp.LOG_TAG,"Error at deleting directory: " + directory.getName());
    }

    public void invite(String username){
        if(!allowedUsers.contains(username))
            allowedUsers.add(username);

        //todo 2nd deliever: network protocol
        /*if(isOwner(user.getUsername()))
            if(!allowedUsers.contains(username))
                allowedUsers.add(username);*/
    }

    public void uninvite(String username){
        if(allowedUsers.contains(username))
            allowedUsers.remove(username);

        //todo 2nd deliever: network protocol
        /*if(isOwner(user.getUsername()))
            if(!allowedUsers.contains(username))
                allowedUsers.add(username);*/
    }

    public void setQuota(int max) throws NotDirectoryException, QuotaLimitExceededException {
        long workspaceSize = this.getSize();
        if(max < workspaceSize)
            throw new QuotaLimitExceededException("Workspace size is " + workspaceSize + " and the new max size was " + max + ". Can't update to a smaller value.");
        this.quota = max;
    }
    //endregion
}
