package pt.ulisboa.tecnico.cmov.airdesk.Workspace;

import pt.ulisboa.tecnico.cmov.airdesk.FileSystem.ADFile;

/**
 * Created by Filipe Teixeira on 12/03/2015.
 */
public class ForeignWorkspace extends Workspace{


    public ForeignWorkspace(String name, int quota){
        super(name);
        setQuota(quota);
    }


    public void addFile(ADFile file){

    }

    public void removeFile(ADFile file){

    }

    public void delete(){

    }

    public void subscribe(){
        /*if(isPublic)
            if(!allowedUsers.contains(user.getUsername()))
                allowedUsers.add(user.getUsername());*/
    }
}
