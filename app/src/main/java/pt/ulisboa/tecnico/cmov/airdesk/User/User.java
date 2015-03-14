package pt.ulisboa.tecnico.cmov.airdesk.User;

import com.android.internal.util.Predicate;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import pt.ulisboa.tecnico.cmov.airdesk.Exception.ADFileNotFoundException;
import pt.ulisboa.tecnico.cmov.airdesk.Exception.CreateFileException;
import pt.ulisboa.tecnico.cmov.airdesk.Exception.CreateWorkspaceException;
import pt.ulisboa.tecnico.cmov.airdesk.Exception.DeleteFileException;
import pt.ulisboa.tecnico.cmov.airdesk.Exception.NotDirectoryException;
import pt.ulisboa.tecnico.cmov.airdesk.Exception.QuotaLimitExceededException;
import pt.ulisboa.tecnico.cmov.airdesk.Predicate.WorkspaceNamePredicate;
import pt.ulisboa.tecnico.cmov.airdesk.Exception.WorkspaceNotFoundException;
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
        deleteAllWorkspaces();
        // TEST ADD
        try {
            createWorkspace("workspace", true, 1);
            createWorkspace("workspace1", true, 1);
        } catch (CreateWorkspaceException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }

    // User functions
    public String getNick(){
        return nick;
    }

    public String getEmail(){
        return email;
    }

    // Workspace functions

    public List<Workspace> getForeignWorkspaces() {
        return foreignWorkspaces;
    }

    public List<Workspace> getOwnedWorkspaces() {
        return ownedWorkspaces;
    }

    public ArrayList<String> getOwnedWorkspacesNames() {
        ArrayList<String> workspaceNames = new ArrayList();
        for(int i=0;i<ownedWorkspaces.size();i++)
            workspaceNames.add(ownedWorkspaces.get(i).getName());
        return workspaceNames;
    }

    public ArrayList<String> getForeignWorkspacesNames() {
        ArrayList<String> workspaceNames = new ArrayList();
        for(int i=0;i<ownedWorkspaces.size();i++)
            workspaceNames.add(ownedWorkspaces.get(i).getName());
        return workspaceNames;
    }

    public Workspace getOwnedWorkspaceByName(String name) throws WorkspaceNotFoundException{
        Predicate validator = new WorkspaceNamePredicate(name);
        Workspace result = null;
        for(Workspace workspace : ownedWorkspaces){
            if(validator.apply(workspace)){
                result = workspace;
                break;
            }
        }
        if(result == null)
            throw new WorkspaceNotFoundException("Workspace " + name + " not found in OwnedWorkspaces");
        return result;
    }

    public Workspace getForeignWorkspaceByName(String name) throws WorkspaceNotFoundException{
        Predicate validator = new WorkspaceNamePredicate(name);
        Workspace result = null;
        for(Workspace workspace : foreignWorkspaces){
            if(validator.apply(workspace)){
                result = workspace;
                break;
            }
        }
        if(result == null)
            throw new WorkspaceNotFoundException("Workspace " + name + " not found in ForeignWorkspaces");
        return result;
    }

    public Workspace createWorkspace(String name, boolean isPublic, int quota) throws CreateWorkspaceException {
        Workspace newWorkspace = new OwnedWorkspace(name, isPublic, quota);
        ownedWorkspaces.add(newWorkspace);
        return newWorkspace;
    }

        /*
    public List<Workspace> loadExistingWorkspaces() {
        List<Workspace> existingWorkspaces;
        File file = new File(".");
        String[] fileNames = file.list();
        for (String fileName : fileNames) {
            ownedWorkspaces.add(new OwnedWorkspace(fileName, boolean isPublic, int quota,true))
        }

    }
    */

    public void addForeignWorkspace(Workspace workspace){
        foreignWorkspaces.add(workspace);
    }

    public void deleteAllWorkspaces(){
        File file = new File(".");
        for (File workFile : file.listFiles()) {
            workFile.delete();
        }
        ownedWorkspaces = new ArrayList<Workspace>();
    }

    public void deleteWorkspace(Workspace workspace){
        //try {
            //workspace.delete();
            ownedWorkspaces.remove(workspace);
        //} catch (NotDirectoryException e) {
            //TODO: Correct exception handling (severe problem if this happens)
          //  e.printStackTrace();
        //}
    }

    public List<Workspace> searchWorkspaces(String keywords[]){
        //TODO
        return null;
    }

    // File functions
    public void createFile(String fileName, Workspace workspace) throws QuotaLimitExceededException, CreateFileException {
        workspace.createFile(fileName);
    }
    public void createFile(String fileName, String workspaceName) throws WorkspaceNotFoundException, QuotaLimitExceededException, CreateFileException {
        getOwnedWorkspaceByName(workspaceName).createFile(fileName);
    }

    public void deleteFile(String fileName, Workspace workspace) throws ADFileNotFoundException, DeleteFileException {
        workspace.removeFile(fileName);
    }
    public void deleteFile(String fileName, String workspaceName) throws WorkspaceNotFoundException, ADFileNotFoundException, DeleteFileException {
        getOwnedWorkspaceByName(workspaceName).removeFile(fileName);
    }
}
