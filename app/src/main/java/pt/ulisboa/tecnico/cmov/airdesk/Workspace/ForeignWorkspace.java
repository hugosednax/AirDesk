package pt.ulisboa.tecnico.cmov.airdesk.Workspace;

import pt.ulisboa.tecnico.cmov.airdesk.FileSystem.ADFile;

/**
 * Created by Filipe Teixeira on 12/03/2015.
 */

// 1st version of ForeignWorkspace, fully local
public class ForeignWorkspace extends Workspace{

    private String directoryPath;

    public ForeignWorkspace(String name, int quota, String directoryPath){
        super(name);
        setQuota(quota);
        this.directoryPath = directoryPath;
    }


    public void addFile(ADFile file){
        if(this.getSize() >= getQuota()){
            System.out.println();
            //todo: Quota exceeded exception
        } else files.add(file);
    }

    public void removeFile(String name){
        //TODO
    }

    public void delete(){
        //TODO
    }

    public int getSize(){
        //TODO
        return 0;
    }

    public void subscribe(){
        /*if(isPublic)
            if(!allowedUsers.contains(user.getUsername()))
                allowedUsers.add(user.getUsername());*/
    }
}
