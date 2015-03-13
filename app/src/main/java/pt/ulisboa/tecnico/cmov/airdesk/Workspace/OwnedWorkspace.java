package pt.ulisboa.tecnico.cmov.airdesk.Workspace;

import com.android.internal.util.Predicate;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import pt.ulisboa.tecnico.cmov.airdesk.Exception.ADFileNotFoundException;
import pt.ulisboa.tecnico.cmov.airdesk.Exception.CreateFileException;
import pt.ulisboa.tecnico.cmov.airdesk.Exception.NotDirectoryException;
import pt.ulisboa.tecnico.cmov.airdesk.Exception.QuotaLimitExceededException;
import pt.ulisboa.tecnico.cmov.airdesk.FileSystem.ADFile;
import pt.ulisboa.tecnico.cmov.airdesk.Predicate.FileNamePredicate;
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
        this.allowedUsers = new ArrayList<User>();
        File directory = new File(name);
        directory.mkdirs();
    }

    public void createFile(String fileName) throws QuotaLimitExceededException, CreateFileException {
        try {
            if(this.getSize() >= getQuota()){
                throw new QuotaLimitExceededException("Quota limit exceeded while trying to create " + fileName + " in " + this.getName() + " your Workspace.");
            } else {
                files.add(new ADFile(fileName, this.getName()));
            }
        } catch (NotDirectoryException e) {
            e.printStackTrace();
            throw new CreateFileException("Problem occurred in getting workspace total size. See log for more info.");
            }
    }

    public void removeFile(String name) throws ADFileNotFoundException {
        ADFile file = getFileByName(name);
        files.remove(file);
        file.getFile().delete();
        file = null;
    }

    public void updateFile(String name, String text){
        //TODO
    }

    public ADFile getFileByName(String name) throws ADFileNotFoundException {
        Predicate validator = new FileNamePredicate(name);
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

    public int getSize() throws NotDirectoryException {
        File directory = new File(getName());
        int workspaceSize = 0;

        if(directory.listFiles().length != files.size())
           System.out.println("File inconsistency noted.");

        if (directory.isDirectory())
            for (File child : directory.listFiles())
                workspaceSize += child.getTotalSpace();
        else throw new NotDirectoryException("Can't read workspace, the provided name isn't a directory.");

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
        directory = null;
    }

    public void invite(String username){
        //todo 2nd deliever: network protocol
        /*if(isOwner(user.getUsername()))
            if(!allowedUsers.contains(username))
                allowedUsers.add(username);*/
    }
}
