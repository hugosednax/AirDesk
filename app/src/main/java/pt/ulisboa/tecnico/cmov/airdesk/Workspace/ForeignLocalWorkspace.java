package pt.ulisboa.tecnico.cmov.airdesk.Workspace;

import java.io.IOException;

import pt.ulisboa.tecnico.cmov.airdesk.Exception.ADFileNotFoundException;
import pt.ulisboa.tecnico.cmov.airdesk.Exception.NotDirectoryException;
import pt.ulisboa.tecnico.cmov.airdesk.Exception.QuotaLimitExceededException;
import pt.ulisboa.tecnico.cmov.airdesk.FileSystem.ADFile;

/**
 * Created by Filipe Teixeira on 12/03/2015.
 */

// 1st version of ForeignWorkspace, fully local
public class ForeignLocalWorkspace extends Workspace{

    private String directoryPath;

    public ForeignLocalWorkspace(String name, int quota, String directoryPath){
        super(name);
        setQuota(quota);
        this.directoryPath = directoryPath;
    }


    public void createFile(String fileName) throws QuotaLimitExceededException, IOException {
        if(this.getSize() >= getQuota()){
            throw new QuotaLimitExceededException("Quota limit exceeded while trying to create " + fileName + " in " + this.getName() + " remote Workspace.");
        } else files.add(new ADFile(fileName, this.getName()));
    }

    public void removeFile(String name){
        //TODO
    }

    public void updateFile(String name, String text){
        //TODO
    }

    @Override
    public ADFile getFileByName(String name) throws ADFileNotFoundException {
        // TODO
        return null;
    }

    public void delete() throws NotDirectoryException {
        //TODO
    }

    public int getSize(){
        //TODO
        return 0;
    }

    public void subscribe(){
        /*if(isPublic)
            if(!allowedUsers.contains(user.getUsername()))
                allowedUsers.add(user.getUsername());*/
    }
}
