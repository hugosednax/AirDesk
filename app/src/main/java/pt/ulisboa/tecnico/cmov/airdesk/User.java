package pt.ulisboa.tecnico.cmov.airdesk;

import java.util.ArrayList;
import java.util.List;

import pt.ulisboa.tecnico.cmov.airdesk.FileSystem.ADFile;
import pt.ulisboa.tecnico.cmov.airdesk.Workspace.OwnedWorkspace;
import pt.ulisboa.tecnico.cmov.airdesk.Workspace.Workspace;

/**
 * Created by hugo__000 on 10/03/2015.
 */
public class User {

    private String nick;
    private String email;
    private List<Workspace> foreignWorkspaces;
    private List<Workspace> ownedWorkspaces;

    public User(String nick, String email){
        this.nick = nick;
        this.email = email;
        foreignWorkspaces = new ArrayList<Workspace>();
        ownedWorkspaces = new ArrayList<Workspace>();
    }

    public String getNick(){
        return nick;
    }

    public String getEmail(){
        return email;
    }

    public void createFile(String name, Workspace workspace){
        ADFile newFile = new ADFile(name, workspace);
    }

    public void deleteFile(String name, Workspace workspace){
        workspace.removeFile(name);
    }

    public Workspace createWorkspace(String name, boolean isPublic, int quota){
        Workspace newWorkspace = new OwnedWorkspace(name, isPublic, quota);
        ownedWorkspaces.add(newWorkspace);
        return newWorkspace;
    }

    public void addForeignWorkspace(Workspace workspace){
        foreignWorkspaces.add(workspace);
    }

    public void deleteWorkspace(Workspace workspace){
        workspace.delete();
        ownedWorkspaces.remove(workspace);
    }

    public List<Workspace> searchWorkspaces(String keywords[]){
        //TODO
        return null;
    }
}
