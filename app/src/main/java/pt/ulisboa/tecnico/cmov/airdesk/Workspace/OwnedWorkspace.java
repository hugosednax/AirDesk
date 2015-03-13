package pt.ulisboa.tecnico.cmov.airdesk.Workspace;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import pt.ulisboa.tecnico.cmov.airdesk.Exception.NotDirectoryException;
import pt.ulisboa.tecnico.cmov.airdesk.Exception.QuotaLimitExceededException;
import pt.ulisboa.tecnico.cmov.airdesk.FileSystem.ADFile;
import pt.ulisboa.tecnico.cmov.airdesk.User;

/**
 * Created by Filipe Teixeira on 12/03/2015.
 */
public class OwnedWorkspace extends Workspace{

    private List<User> allowedUsers;
    private boolean isPublic;

    public OwnedWorkspace(String name, boolean isPublic, int quota){
        super(name);
        this.isPublic = isPublic;
        this.quota = quota;
        allowedUsers = new ArrayList<User>();
        File directory = new File(name);
        directory.mkdirs();
    }

    public void createFile(String fileName) throws QuotaLimitExceededException{
        if(this.getSize() >= getQuota()){
            throw new QuotaLimitExceededException("Quota limit exceeded while trying to create " + fileName + " in " + this.getName() + " your Workspace.");
        } else {
            files.add(new ADFile(fileName, this.getName()));
        }
    }

    public void removeFile(String name){
        // TODO
    }

    public void updateFile(String name, String text){
        //TODO
    }

    public int getSize(){
        File directory = new File(getName());
        int workspaceSize = 0;

        if (directory.isDirectory())
            for (File child : directory.listFiles())
                workspaceSize += child.getTotalSpace();
        else System.out.println("Insert Exception HERE");
        //todo: not a directory exception

        return workspaceSize;
    }

    public void delete() throws NotDirectoryException{
        //todo: not tested
        File directory = new File(getName());
        if (directory.isDirectory())
            for (File child : directory.listFiles())
                child.delete();
        else throw new NotDirectoryException("Can't delete workspace, the provided name isn't a directory.");
        directory.delete();
    }

    public void invite(String username){
        //todo 2nd deliever: network protocol
        /*if(isOwner(user.getUsername()))
            if(!allowedUsers.contains(username))
                allowedUsers.add(username);*/
    }
}
