package pt.ulisboa.tecnico.cmov.airdesk.Workspace;

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
    protected boolean isPublic;
    protected int quota;
    protected String ownerName;
    protected User user;

    abstract void addFile(ADFile file);

    abstract void removeFile(ADFile file);

    public String getName() { return name; }

    public List<ADFile> getFiles(){
        return files;
    }

    public int getQuota(){
        return quota;
    }

    public boolean isPublic(){
        return isPublic;
    }
}
