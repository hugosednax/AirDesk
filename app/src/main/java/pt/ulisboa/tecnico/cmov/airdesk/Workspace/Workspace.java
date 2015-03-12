package pt.ulisboa.tecnico.cmov.airdesk.Workspace;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import pt.ulisboa.tecnico.cmov.airdesk.User;

/**
 * Created by hugo__000 on 10/03/2015.
 */
public abstract class Workspace {

    protected String name;
    protected List<File> files;
    protected List<String> keywords;
    protected boolean isPublic;
    protected int quota;
    protected String ownerName;
    protected User user;

    abstract void addFile(File file);

    abstract void removeFile(File file);

    abstract void editFile(File file);

    abstract void saveFile(File file);

    public String getName() { return name; }

    public List<File> getFiles(){
        return files;
    }

    public int getQuota(){
        return quota;
    }

    public boolean isPublic(){
        return isPublic;
    }
}
