package pt.ulisboa.tecnico.cmov.airdesk.Workspace;

import java.io.IOException;
import java.util.List;

import pt.ulisboa.tecnico.cmov.airdesk.Exception.FileNotFoundException;
import pt.ulisboa.tecnico.cmov.airdesk.Exception.CreateFileException;
import pt.ulisboa.tecnico.cmov.airdesk.Exception.DeleteFileException;
import pt.ulisboa.tecnico.cmov.airdesk.Exception.QuotaLimitExceededException;
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

    abstract public void updateFile(String name, String text) throws FileNotFoundException, QuotaLimitExceededException;

    abstract public String getFileContent(String filename) throws FileNotFoundException;

    abstract public List<String> getFileNames();

    abstract public boolean editable(String filename) throws FileNotFoundException;

    abstract public void setEditable(String filename) throws FileNotFoundException;
    //endregion

    //region Getters
    public String getName() { return name; }
    //endregion

    @Override
    public String toString(){ return name;}
}
