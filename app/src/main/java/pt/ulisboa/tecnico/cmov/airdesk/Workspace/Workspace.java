package pt.ulisboa.tecnico.cmov.airdesk.Workspace;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import pt.ulisboa.tecnico.cmov.airdesk.Exception.ADFileNotFoundException;
import pt.ulisboa.tecnico.cmov.airdesk.Exception.CreateFileException;
import pt.ulisboa.tecnico.cmov.airdesk.Exception.DeleteFileException;
import pt.ulisboa.tecnico.cmov.airdesk.Exception.NotDirectoryException;
import pt.ulisboa.tecnico.cmov.airdesk.Exception.QuotaLimitExceededException;
import pt.ulisboa.tecnico.cmov.airdesk.FileSystem.ADFile;

/**
 * Created by hugo__000 on 10/03/2015.
 */
public abstract class     Workspace {

        //region Class Variables
        protected String name;
        protected List<ADFile> files;
        protected List<String> keywords;
        protected int quota;
        //endregion

        //region Constructors
        public Workspace(String name){
            this.name = name;
            this.files = new ArrayList<ADFile>();
            this.keywords = new ArrayList<String>();
            this.quota = 0;
        }
        //endregion

        //region Abstract Methods
    abstract public void delete() throws NotDirectoryException;

    abstract public void createFile(String name) throws QuotaLimitExceededException, CreateFileException, IOException;

    abstract public void removeFile(String name) throws ADFileNotFoundException, DeleteFileException;

    abstract public void updateFile(String name, String text) throws ADFileNotFoundException, NotDirectoryException, QuotaLimitExceededException;

    abstract public ADFile getFileByName(String name) throws ADFileNotFoundException;

    abstract public int getSize() throws NotDirectoryException;

    abstract public void setQuota(int quota) throws NotDirectoryException, QuotaLimitExceededException;

    abstract public boolean hasKeyword(String keyword);
    //endregion

    //region Getters
    public String getName() { return name; }

    public List<ADFile> getFiles(){
        return files;
    }

    public int getQuota(){
        return quota;
    }
    //endregion

    //region Setters
    @Override
    public String toString(){
        return name;
    }

    public JSONObject toJSON(){
        JSONObject json = new JSONObject();
        try {
            json.put("name", name);
            json.put("quota", quota);
            JSONArray jFiles = new JSONArray();
            for (ADFile file : getFiles())
                jFiles.put(file.toJSON());
            json.put("files", jFiles);
        }catch (Exception e){

        }
        return json;
    }
    //endregion
}
