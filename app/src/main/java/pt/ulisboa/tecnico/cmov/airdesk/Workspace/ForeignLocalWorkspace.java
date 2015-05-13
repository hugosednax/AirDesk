package pt.ulisboa.tecnico.cmov.airdesk.Workspace;

import java.io.IOException;
import java.util.List;

import pt.ulisboa.tecnico.cmov.airdesk.Exception.FileNotFoundException;
import pt.ulisboa.tecnico.cmov.airdesk.Exception.CreateFileException;
import pt.ulisboa.tecnico.cmov.airdesk.Exception.DeleteFileException;
import pt.ulisboa.tecnico.cmov.airdesk.Exception.NotDirectoryException;
import pt.ulisboa.tecnico.cmov.airdesk.Exception.QuotaLimitExceededException;
import pt.ulisboa.tecnico.cmov.airdesk.FileSystem.ADFile;
/**
 * Created by Filipe Teixeira on 12/03/2015.
 */
// 1st version of ForeignWorkspace, fully local, Deprecated
public class ForeignLocalWorkspace extends Workspace{
    //region Class Variables
    private OwnedWorkspace workspaceLink;
    //endregion

    //region Constructors
    public ForeignLocalWorkspace(OwnedWorkspace workspace, String username) {
        super(workspace.getName() + "@" + username);
        this.workspaceLink = workspace;
    }
    //endregion

    //region File Methods
    @Override
    public List<String> getFileNames() {

        return null;
    }

    public void createFile(String fileName) throws QuotaLimitExceededException, IOException, CreateFileException {
        workspaceLink.createFile(fileName);
    }

    public void removeFile(String name) throws FileNotFoundException, DeleteFileException {
        workspaceLink.removeFile(name);
    }

    public void updateFile(String name, String text) throws QuotaLimitExceededException, FileNotFoundException {
        workspaceLink.updateFile(name, text);
    }

    @Override
    public String getFileContent(String filename) throws FileNotFoundException {
        return workspaceLink.getFileContent(filename);
    }
    //endregion

    //region Workspace Methods
    public void delete() throws NotDirectoryException {
        //TODO
    }
    //endregion
}
