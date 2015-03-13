package pt.ulisboa.tecnico.cmov.airdesk.Workspace;

import com.android.internal.util.Predicate;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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

    private List<User> allowedUsers;
    private boolean isPublic;

    public OwnedWorkspace(String name, boolean isPublic, int quota) throws CreateWorkspaceException {
        super(name);
        this.isPublic = isPublic;
        this.quota = quota;
        this.allowedUsers = new ArrayList<User>();
        File directory = new File(name);
        if(!directory.mkdirs())
            throw new CreateWorkspaceException("Can't create a new directory for this Workspace");
    }

    public boolean isPublic() {
        return isPublic;
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

    public void removeFile(String name) throws ADFileNotFoundException, DeleteFileException {
        ADFile file = getFileByName(name);
        files.remove(file);
        if(!file.getFile().delete())
            throw new DeleteFileException("Can't delete file in Android File System");
    }

    public void updateFile(String name, String text){
        //TODO
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
        File directory = new File(getName());
        if (directory.isDirectory())
            for (File child : directory.listFiles())
                if(!child.delete())
                    System.out.println("Error at deleting file: " + child.getName());
        else throw new NotDirectoryException("Can't delete workspace, the provided name isn't a directory.");
        if(!directory.delete())
            System.out.println("Error at deleting directory: " + directory.getName());
    }

    public void invite(String username){
        //todo 2nd deliever: network protocol
        /*if(isOwner(user.getUsername()))
            if(!allowedUsers.contains(username))
                allowedUsers.add(username);*/
    }
}
