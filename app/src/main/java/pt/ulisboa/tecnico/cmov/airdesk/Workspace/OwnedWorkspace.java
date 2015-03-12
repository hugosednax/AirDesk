package pt.ulisboa.tecnico.cmov.airdesk.Workspace;

import java.util.ArrayList;
import java.util.List;

import pt.ulisboa.tecnico.cmov.airdesk.FileSystem.ADFile;

/**
 * Created by Filipe Teixeira on 12/03/2015.
 */
public class OwnedWorkspace extends Workspace{

    private List<String> allowedUsers;

    public OwnedWorkspace(String name, boolean isPublic, int quota, String ownerName){
        this.name = name;
        this.isPublic = isPublic;
        files = new ArrayList<ADFile>();
        keywords = new ArrayList<String>();
        allowedUsers = new ArrayList<String>();
        this.ownerName = ownerName;
    }

    public void addFile(ADFile file){
        files.add(file);
    }

    public void removeFile(ADFile file){
        files.remove(file);
    }

    public void invite(String username){
        /*if(isOwner(user.getUsername()))
            if(!allowedUsers.contains(username))
                allowedUsers.add(username);*/
    }
}
