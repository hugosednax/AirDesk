package pt.ulisboa.tecnico.cmov.airdesk;

import java.util.ArrayList;
import java.util.List;

import pt.ulisboa.tecnico.cmov.airdesk.Workspace.OwnedWorkspace;
import pt.ulisboa.tecnico.cmov.airdesk.Workspace.Workspace;

/**
 * Created by hugo__000 on 10/03/2015.
 */
public class User {

    private String name;
    private String email;
    private List<Workspace> foreignWorkspaces;
    private List<Workspace> ownedWorkspaces;

    public User(String name, String email){
        this.name = name;
        this.email = email;
        foreignWorkspaces = new ArrayList<Workspace>();
        ownedWorkspaces = new ArrayList<Workspace>();
    }

    public String getUsername(){
        return name;
    }

    public String getEmail(){
        return email;
    }

    public Workspace createWorkspace(String name, boolean isPublic, int quota){
        return new OwnedWorkspace(name, isPublic, quota, name);
    }

    public void deleteWorkspace(Workspace workspace){
        ownedWorkspaces.remove(workspace);
    }

    public List<Workspace> searchWorkspaces(String keywords[]){
        //TODO
        return null;
    }
}
