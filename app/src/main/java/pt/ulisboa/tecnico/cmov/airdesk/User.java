package pt.ulisboa.tecnico.cmov.airdesk;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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
        return new Workspace(name, isPublic, quota, name);
    }

    public void deleteWorkspace(Workspace workspace){
        ownedWorkspaces.remove(workspace);
    }

    public List<Workspace> searchWorkspaces(String keywords[]){
        //TODO
        return null;
    }
}
