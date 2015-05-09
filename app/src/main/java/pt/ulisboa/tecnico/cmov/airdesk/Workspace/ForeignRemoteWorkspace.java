package pt.ulisboa.tecnico.cmov.airdesk.Workspace;

import java.io.IOException;
import java.util.List;

import pt.ulisboa.tecnico.cmov.airdesk.DTO.WorkspaceDTO;
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
public class ForeignRemoteWorkspace extends Workspace{

    //region Class Variables

    //endregion

    //region Constructors
    public ForeignRemoteWorkspace(WorkspaceDTO workspaceDTO, String owner, String ip){
        super(workspaceDTO.getName() + "@" + owner);

    }
    //endregion

    //region Getters
    @Override
    public List<ADFile> getFiles(){
        //TODO (no idea)
        return null;
    }

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
    public ADFile getFileByName(String name) throws ADFileNotFoundException {
        //TODO
        return null;
    }

    public void createFile(String fileName) throws QuotaLimitExceededException, IOException, CreateFileException {
        //TODO
    }

    public void removeFile(String name) throws ADFileNotFoundException, DeleteFileException {
        //TODO
    }

    public void updateFile(String name, String text) throws QuotaLimitExceededException, ADFileNotFoundException, NotDirectoryException {
        //TODO
    }
    //endregion

    //region Workspace Methods
    public void delete() throws NotDirectoryException {
        //TODO
    }
    //endregion
}
