package pt.ulisboa.tecnico.cmov.airdesk.Workspace;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import pt.ulisboa.tecnico.cmov.airdesk.Exception.FileNotFoundException;
import pt.ulisboa.tecnico.cmov.airdesk.Exception.CreateFileException;
import pt.ulisboa.tecnico.cmov.airdesk.Exception.DeleteFileException;
import pt.ulisboa.tecnico.cmov.airdesk.Exception.NotDirectoryException;
import pt.ulisboa.tecnico.cmov.airdesk.Exception.QuotaLimitExceededException;
import pt.ulisboa.tecnico.cmov.airdesk.FileSystem.ADFile;

/**
 * Created by hugo__000 on 10/03/2015.
 */
public abstract class Workspace {

    //region Class Variables
    protected String name;
    //endregion

    //region Constructors
    public Workspace(String name){
        this.name = name;
    }
    //endregion

    //region Abstract Methods
    abstract public void createFile(String name) throws QuotaLimitExceededException, CreateFileException, IOException;

    abstract public void removeFile(String name) throws FileNotFoundException, DeleteFileException;

    abstract public void updateFile(String name, String text) throws FileNotFoundException, NotDirectoryException, QuotaLimitExceededException;

    abstract public ADFile getFileByName(String name) throws FileNotFoundException;

    abstract public List<String> getFileNames();
    //endregion

    //region Getters
    public String getName() { return name; }
    //endregion

    @Override
    public String toString(){ return name;}
}
