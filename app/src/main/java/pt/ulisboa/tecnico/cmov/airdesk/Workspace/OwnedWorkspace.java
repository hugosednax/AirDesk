package pt.ulisboa.tecnico.cmov.airdesk.Workspace;

import android.content.Context;
import android.util.Log;

import com.android.internal.util.Predicate;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import pt.ulisboa.tecnico.cmov.airdesk.Application.AirDeskApp;
import pt.ulisboa.tecnico.cmov.airdesk.DTO.WorkspaceDTO;
import pt.ulisboa.tecnico.cmov.airdesk.Exception.FileNotFoundException;
import pt.ulisboa.tecnico.cmov.airdesk.Exception.CreateFileException;
import pt.ulisboa.tecnico.cmov.airdesk.Exception.CreateWorkspaceException;
import pt.ulisboa.tecnico.cmov.airdesk.Exception.DeleteFileException;
import pt.ulisboa.tecnico.cmov.airdesk.Exception.NotDirectoryException;
import pt.ulisboa.tecnico.cmov.airdesk.Exception.QuotaLimitExceededException;
import pt.ulisboa.tecnico.cmov.airdesk.FileSystem.ADFile;
import pt.ulisboa.tecnico.cmov.airdesk.Predicate.FileNamePredicate;
/**
 * Created by Filipe Teixeira on 12/03/2015.
 */
public class OwnedWorkspace extends Workspace{
    //region Class Const
    private static final String TAG = "[AirDesk]";
    //endregion

    //region Class Variables
    private List<String> allowedUsers;
    private boolean isPublic;
    private List<String> keywords;
    protected List<ADFile> files;
    protected int quota;
    private boolean filesPresent;
    //endregion

    //region Constructors
    public OwnedWorkspace(String name, boolean isPublic, int quota) throws CreateWorkspaceException {
        super(name);
        this.isPublic = isPublic;
        this.quota = quota;
        this.allowedUsers = new ArrayList<>();
        this.files = new ArrayList<>();
        File mainDir = AirDeskApp.getAppContext().getDir("data", Context.MODE_PRIVATE);
        File currentDir = new File(""+mainDir+File.separatorChar+name);
        currentDir.mkdir();
        if(!currentDir.isDirectory())
            throw new CreateWorkspaceException("Can't create a new directory for this Workspace");
        this.keywords = new ArrayList<>();
    }

    public OwnedWorkspace(WorkspaceDTO workspaceDTO) {
        super(workspaceDTO.getName());
        this.isPublic = workspaceDTO.isPublic();
        this.quota = workspaceDTO.getQuota();
        this.allowedUsers = new ArrayList<>();
        this.keywords = new ArrayList<>();
        this.files = new ArrayList<>();
        this.filesPresent = false;

        for(String username : workspaceDTO.getAllowedUsers())
            allowedUsers.add(username);
        for(String keyword : workspaceDTO.getKeywords())
            keywords.add(keyword);

        File mainDir = AirDeskApp.getAppContext().getDir("data", Context.MODE_PRIVATE);
    }
    //endregion

    //region Getters
    public boolean isPublic() {
        return isPublic;
    }

    public List<ADFile> getFiles() { return files; }

    public int getQuota() { return quota; }

    public List<String> getAllowedUsers() {
        return allowedUsers;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public int getSize(){
        File mainDir = AirDeskApp.getAppContext().getDir("data", Context.MODE_PRIVATE);
        File currentDir = new File(""+mainDir+File.separatorChar+name);
        int workspaceSize = 0;

        if(currentDir.listFiles().length != files.size())
            Log.d(AirDeskApp.LOG_TAG, "File inconsistency noted.");

        if (currentDir.isDirectory())
            for (File child : currentDir.listFiles())
                workspaceSize += child.length();
        else Log.d("[AirDesk]", "Workspace folder isn't a directory, FileSystem error at getSize of Workspace " + this.getName());

        return workspaceSize;
    }
    //endregion

    //region File Functions
    public void createFile(String fileName) throws QuotaLimitExceededException, CreateFileException{
        if(this.getSize() >= getQuota()){
            throw new QuotaLimitExceededException("Quota limit exceeded while trying to create " + fileName + " in " + this.getName() + " your Workspace.");
        }
        if(existFile(fileName)) throw new CreateFileException("Already exists a file with that name.");
        try {
            files.add(new ADFile(fileName, this.getName()));
            ParseUser parseUser = ParseUser.getCurrentUser();
            ParseObject fileTable = new ParseObject("File");
            byte[] data = "".getBytes();
            ParseFile file = new ParseFile(fileName, data);
            fileTable.put("user", parseUser.getEmail());
            fileTable.put("workspace", this.getName());
            fileTable.put("fileName", fileName+".txt");
            fileTable.put("file", file);
            fileTable.saveInBackground();
        } catch (IOException e) {
            Log.d(TAG, "Error in create file regarding fileSystem: " + e.getMessage());
            throw new CreateFileException("Error at creating the file in the FileSystem.");
        }


    }

    public void removeFile(String name) throws FileNotFoundException, DeleteFileException {
        ADFile file = getFileByName(name);
        files.remove(file);
        ParseUser parseUser = ParseUser.getCurrentUser();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("File");
        ParseObject fileEntry = null;
        query.whereEqualTo("user", parseUser.getEmail());
        query.whereEqualTo("workspace", this.getName());
        query.whereEqualTo("fileName", name);
        Log.d(TAG, "querying user=" + parseUser.getEmail() + " workspace=" + this.getName() + " fileName=" + name);
        try {
            fileEntry = query.find().get(0);
            fileEntry.deleteInBackground();
        } catch (Exception e) {
            Log.d(TAG, "Parse error at updateFile: " + e.getMessage());
        }
        if(!file.getFile().delete())
            throw new DeleteFileException("Can't delete file in Android File System");
    }

    public void updateFile(String name, String text) throws FileNotFoundException, QuotaLimitExceededException {
        ADFile file = getFileByName(name);
        if(this.getSize() - file.getSize() + text.length() > this.getQuota())
            throw new QuotaLimitExceededException("Quota limit exceeded while trying to update " + name + " in " + this.getName() + " your Workspace.");
        file.save(text);
        file.setEditable(true);
        ParseUser parseUser = ParseUser.getCurrentUser();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("File");
        ParseObject fileEntry = null;
        query.whereEqualTo("user", parseUser.getEmail());
        query.whereEqualTo("workspace", this.getName());
        query.whereEqualTo("fileName", name);
        Log.d(TAG, "querying user=" + parseUser.getEmail() + " workspace=" + this.getName() + " fileName=" + name);
        try {
            fileEntry = query.find().get(0);
            fileEntry.put("file", new ParseFile(name, text.getBytes()));
            fileEntry.save();
        } catch (Exception e) {
            Log.d(TAG, "Parse error at updateFile: " + e.getMessage());
        }
    }

    public ADFile getFileByName(String name) throws FileNotFoundException {
        ADFile result = null;
        for(ADFile file : getFiles())
            if (file.getFileName().equals(name)) {
                result = file;
                break;
            }
        if(result == null)
            throw new FileNotFoundException("File " + name + " not found in " + this.getName() + " Workspace.");
        return result;
    }

    public String getFileContent(String filename) throws FileNotFoundException{
        try {
            return getFileByName(filename).getContent();
        } catch (IOException e) {
            return "Error reading the file";
        }
    }

    public void accessCloudFiles(){
        if(!filesPresent) {
            Log.d(TAG, "Accessing Cloud files");
            ParseUser parseUser = ParseUser.getCurrentUser();
            ParseQuery<ParseObject> query = ParseQuery.getQuery("File");
            query.whereEqualTo("user", parseUser.getEmail());
            query.whereEqualTo("workspace", this.getName());
            Log.d(TAG, "querying user=" + parseUser.getEmail() + " workspace=" + this.getName());
            try {
                List<ParseObject> listParse = query.find();
                for (ParseObject parseObject : listParse) {
                    ParseFile file = parseObject.getParseFile("file");
                    ADFile savedFile = new ADFile(parseObject.getString("fileName"), getName(), true);
                    savedFile.save(new String(file.getData()));
                    files.add(savedFile);
                    Log.d(AirDeskApp.LOG_TAG, "Loaded file: " + parseObject.getString("fileName") + " from cloud to app");
                }

            } catch (Exception e) {
                Log.d(TAG, "Parse error at updateFile: " + e.getMessage());
            }
            /*for(File file : currentDir.listFiles()){
                ADFile savedFile = new ADFile(file);
                files.add(savedFile);
                Log.d(AirDeskApp.LOG_TAG, "Loaded file: " + file.getName() + " from memory to app");
            }*/
            filesPresent=true;
        }
    }

    public void cleanAllFiles(){
        File mainDir = AirDeskApp.getAppContext().getDir("data", AirDeskApp.getAppContext().MODE_PRIVATE);
        File directory = new File(""+mainDir+File.separatorChar+name);
        for (File child : directory.listFiles())
            if(!child.delete())
                Log.d(AirDeskApp.LOG_TAG, "Error at deleting file: " + child.getName());
        files = new ArrayList<>();
        filesPresent = false;
    }

    @Override
    public List<String> getFileNames() {
        if(!filesPresent)
            accessCloudFiles();
        List<String> result = new ArrayList<>();
        for(ADFile file : getFiles()) {
            result.add(file.getFileName());
        }
        return result;
    }

    public boolean existFile(String name){
        Predicate<ADFile> validator = new FileNamePredicate(name);
        ADFile result = null;
        for(ADFile file : getFiles())
            if (validator.apply(file)) {
                result = file;
                break;
            }
        if(result == null)
            return false;
        return true;
    }

    public boolean editable(String fileName) throws FileNotFoundException {
        boolean editable = getFileByName(fileName).isEditable();
        if(editable) getFileByName(fileName).setEditable(false);
        return editable;
    }

    public void setEditable(String fileName) throws FileNotFoundException {
        getFileByName(fileName).setEditable(true);
    }

    //endregion

    //region Workspace Functions
    public void delete(){
        File mainDir = AirDeskApp.getAppContext().getDir("data", AirDeskApp.getAppContext().MODE_PRIVATE);
        File directory = new File(""+mainDir+File.separatorChar+name);
        for(String file : getFileNames())
            try {
                removeFile(file);
            } catch (FileNotFoundException | DeleteFileException e) {
                Log.d(AirDeskApp.LOG_TAG, "Error at removing file: " + file);
            }
        for (File child : directory.listFiles())
            if(!child.delete())
                Log.d(AirDeskApp.LOG_TAG, "Error at deleting file: " + child.getName());
        if(!directory.delete())
            Log.d(AirDeskApp.LOG_TAG,"Error at deleting directory: " + directory.getName());
    }

    public void addToAllowedUsers(String email){
        if(!allowedUsers.contains(email))
            allowedUsers.add(email);
    }

    public void removeFromAllowedUsers(String email){
        if(allowedUsers.contains(email))
            allowedUsers.remove(email);
    }

    public void setQuota(int max) throws QuotaLimitExceededException {
        long workspaceSize = this.getSize();
        if(max < workspaceSize)
            throw new QuotaLimitExceededException("Workspace size is " + workspaceSize + " and the new max size was " + max + ". Can't update to a smaller value.");
        this.quota = max;
    }

    public void setName(String name){ //needs to throw some new exceptions
        if(!name.equals(this.getName()))
            this.name = name;
    }

    public void addKeyword(String keyword){
        this.getKeywords().add(keyword);
    }

    public void removeKeyword(String keyword){
        this.getKeywords().remove(keyword);
    }

    public boolean hasKeyword(String keyword){
        return this.getKeywords().contains(keyword);
    }
    //endregion
}
