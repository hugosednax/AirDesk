package pt.ulisboa.tecnico.cmov.airdesk.Workspace;

import java.util.ArrayList;
import java.util.List;

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
        files = new ArrayList<ADFile>();
        keywords = new ArrayList<String>();
        quota = 0;
    }

    abstract public void addFile(ADFile file);

    abstract public void removeFile(ADFile file);

    abstract public void delete();

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
