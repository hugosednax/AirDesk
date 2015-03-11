package pt.ulisboa.tecnico.cmov.airdesk;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hugo__000 on 10/03/2015.
 */
public class Workspace {

    private String name;
    private List<File> files;
    private List<String> keywords;
    private boolean isPublic;
    private int quota;
    private String ownerName;
    private List<String> allowedUsers;
    private User user;

    public Workspace(String name, boolean isPublic, int quota, String ownerName){
        this.name = name;
        this.isPublic = isPublic;
        files = new ArrayList<File>();
        keywords = new ArrayList<String>();
        allowedUsers = new ArrayList<String>();
        this.ownerName = ownerName;
    }

    public String getName(){
        return name;
    }

    public List<File> getFiles(){
        return files;
    }

    public int getQuota(){
        return quota;
    }

    public boolean isPublic(){
        return isPublic;
    }

    public void addFile(File file){
        files.add(file);
    }

    public void removeFile(File file){
        files.remove(file);
    }

    public void editFile(File file){
        //TODO
    }

    public void saveFile(File file){
        //TODO
    }

    public boolean isOwner(String name){
        return name.equals(ownerName);
    }

    public void invite(String username){
        if(isOwner(user.getUsername()))
            if(!allowedUsers.contains(username))
                allowedUsers.add(username);
    }

    public void subscribe(){
        if(isPublic)
            if(!allowedUsers.contains(user.getUsername()))
                allowedUsers.add(user.getUsername());
    }
}
