package pt.ulisboa.tecnico.cmov.airdesk.Workspace;

import java.util.ArrayList;
import java.util.List;

import pt.ulisboa.tecnico.cmov.airdesk.Exception.ADFileNotFoundException;
import pt.ulisboa.tecnico.cmov.airdesk.Exception.CreateFileException;
import pt.ulisboa.tecnico.cmov.airdesk.Exception.NotDirectoryException;
import pt.ulisboa.tecnico.cmov.airdesk.Exception.QuotaLimitExceededException;
import pt.ulisboa.tecnico.cmov.airdesk.FileSystem.ADFile;
import pt.ulisboa.tecnico.cmov.airdesk.User;

/**
 * Created by hugo__000 on 10/03/2015.
 */
public abstract class Workspace {

    protected String name;
    protected List<ADFile> files;
    protected List<String> keywords;
    protected int quota;

    public Workspace(String name){
        this.name = name;
        this.files = new ArrayList<ADFile>();
        this.keywords = new ArrayList<String>();
        this.quota = 0;
    }

    abstract public void delete() throws NotDirectoryException;

    abstract public void createFile(String name) throws QuotaLimitExceededException, CreateFileException;

    abstract public void removeFile(String name) throws ADFileNotFoundException;

    abstract public void updateFile(String name, String text);

    abstract public ADFile getFileByName(String name) throws ADFileNotFoundException;

    abstract public int getSize() throws NotDirectoryException;

    public String getName() { return name; }

    public List<ADFile> getFiles(){
        return files;
    }

    public int getQuota(){
        return quota;
    }

    public void setQuota(int quota){
        this.quota = quota;
    }
}
