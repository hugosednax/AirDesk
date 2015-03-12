package pt.ulisboa.tecnico.cmov.airdesk.Workspace;

import android.os.Environment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import pt.ulisboa.tecnico.cmov.airdesk.FileSystem.ADFile;

/**
 * Created by Filipe Teixeira on 12/03/2015.
 */
public class OwnedWorkspace extends Workspace{

    private List<String> allowedUsers;
    private boolean isPublic;
    private String directoryPath;

    public OwnedWorkspace(String name, boolean isPublic, int quota){
        super(name);
        this.isPublic = isPublic;
        this.quota = quota;
        allowedUsers = new ArrayList<String>();
        directoryPath = Environment.getExternalStorageDirectory() + File.separator + name;
        File directory = new File(directoryPath);
        directory.mkdirs();
    }

    public void addFile(ADFile file){
        files.add(file);
    }

    public void removeFile(ADFile file){
        files.remove(file);
    }

    public void delete(){
        //todo: not tested
        File directory = new File(directoryPath);
        if (directory.isDirectory())
            for (File child : directory.listFiles())
                child.delete();
        else System.out.println("Insert Exception HERE");
        //todo: not a directory exception
        directory.delete();
    }

    public void invite(String username){
        //todo 2nd deliever: network protocol
        /*if(isOwner(user.getUsername()))
            if(!allowedUsers.contains(username))
                allowedUsers.add(username);*/
    }
}
