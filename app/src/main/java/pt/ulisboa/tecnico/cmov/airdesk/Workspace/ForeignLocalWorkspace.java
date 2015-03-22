package pt.ulisboa.tecnico.cmov.airdesk.Workspace;

import java.io.IOException;
import java.util.List;

import pt.ulisboa.tecnico.cmov.airdesk.Exception.ADFileNotFoundException;
import pt.ulisboa.tecnico.cmov.airdesk.Exception.CreateFileException;
import pt.ulisboa.tecnico.cmov.airdesk.Exception.DeleteFileException;
import pt.ulisboa.tecnico.cmov.airdesk.Exception.NotDirectoryException;
import pt.ulisboa.tecnico.cmov.airdesk.Exception.QuotaLimitExceededException;
import pt.ulisboa.tecnico.cmov.airdesk.FileSystem.ADFile;

/**
 * Created by Filipe Teixeira on 12/03/2015.
 */

// 1st version of ForeignWorkspace, fully local
public class ForeignLocalWorkspace extends Workspace{

    private Workspace workspaceLink;

    public ForeignLocalWorkspace(Workspace workspace, String username) {
        super(workspace.name + "@" + username);
        this.workspaceLink = workspace;
    }

    @Override
    public List<ADFile> getFiles(){
        return workspaceLink.getFiles();
    }

    public void createFile(String fileName) throws QuotaLimitExceededException, IOException, CreateFileException {
        workspaceLink.createFile(fileName);
    }

    public void removeFile(String name) throws ADFileNotFoundException, DeleteFileException {
        workspaceLink.removeFile(name);
    }

    public void updateFile(String name, String text){
        workspaceLink.updateFile(name, text);
    }

    @Override
    public ADFile getFileByName(String name) throws ADFileNotFoundException {
        return workspaceLink.getFileByName(name);
    }

    public void delete() throws NotDirectoryException {
        //TODO
    }

    public int getSize() throws NotDirectoryException {
        return workspaceLink.getSize();
    }
}
