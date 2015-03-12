package pt.ulisboa.tecnico.cmov.airdesk.Workspace;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Filipe Teixeira on 12/03/2015.
 */
public class OwnedWorkspace extends Workspace{

    private List<String> allowedUsers;

    public OwnedWorkspace(String name, boolean isPublic, int quota, String ownerName){
        this.name = name;
        this.isPublic = isPublic;
        files = new ArrayList<File>();
        keywords = new ArrayList<String>();
        allowedUsers = new ArrayList<String>();
        this.ownerName = ownerName;
    }

    public void addFile(File file){
        files.add(file);
    }

    public void removeFile(File file){
        files.remove(file);
    }

    public void editFile(File file){
        /*
        TODO
        NOTE: Whats the difference between editFile and saveFile in the backEnd?
        */
    }

    public void saveFile(File file){
        //TODO
    }

    public void invite(String username){
        /*if(isOwner(user.getUsername()))
            if(!allowedUsers.contains(username))
                allowedUsers.add(username);*/
    }
}
