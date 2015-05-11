package pt.ulisboa.tecnico.cmov.airdesk.User;

import android.util.Log;

import com.android.internal.util.Predicate;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import pt.ulisboa.tecnico.cmov.airdesk.Application.AirDeskApp;
import pt.ulisboa.tecnico.cmov.airdesk.DTO.WorkspaceDTO;
import pt.ulisboa.tecnico.cmov.airdesk.Exception.ADFileNotFoundException;
import pt.ulisboa.tecnico.cmov.airdesk.Exception.CreateFileException;
import pt.ulisboa.tecnico.cmov.airdesk.Exception.CreateWorkspaceException;
import pt.ulisboa.tecnico.cmov.airdesk.Exception.DeleteFileException;
import pt.ulisboa.tecnico.cmov.airdesk.Exception.QuotaLimitExceededException;
import pt.ulisboa.tecnico.cmov.airdesk.Exception.ServiceNotBoundException;
import pt.ulisboa.tecnico.cmov.airdesk.Exception.WriteToFileException;
import pt.ulisboa.tecnico.cmov.airdesk.FileSystem.JSONHandler;
import pt.ulisboa.tecnico.cmov.airdesk.Message.InviteWSMessage;
import pt.ulisboa.tecnico.cmov.airdesk.Predicate.WorkspaceNamePredicate;
import pt.ulisboa.tecnico.cmov.airdesk.Exception.WorkspaceNotFoundException;
import pt.ulisboa.tecnico.cmov.airdesk.WiFiDirect.WifiNotificationHandler;
import pt.ulisboa.tecnico.cmov.airdesk.Workspace.ForeignLocalWorkspace;
import pt.ulisboa.tecnico.cmov.airdesk.Workspace.OwnedWorkspace;
import pt.ulisboa.tecnico.cmov.airdesk.Workspace.Workspace;

/**
 * Created by hugo__000 on 10/03/2015.
 */
public class User {

    private static final String TAG = "[AirDesk]";

    //region Class Variables
    private String nick;
    private String email;
    private List<Workspace> foreignWorkspaces;
    private List<Workspace> ownedWorkspaces;
    private List<String> interestKeywords;
    private JSONHandler settings;
    private WifiNotificationHandler wifiHandler;
    //endregion

    //region Constructors
    public User(String nick, String email, WifiNotificationHandler wifiHandler){
        this.nick = nick;
        this.email = email;
        this.wifiHandler = wifiHandler;
        foreignWorkspaces = new ArrayList<>();
        ownedWorkspaces = new ArrayList<>();
        interestKeywords = new ArrayList<>();

        try {
            Log.d(AirDeskApp.LOG_TAG, "create handler");
            //this.settings = new SettingsHandler();
            this.settings = new JSONHandler();
            Log.d(AirDeskApp.LOG_TAG, "finished handler creating");
        } catch (Exception e) {
            Log.d(AirDeskApp.LOG_TAG, e.getMessage());
        }

        if(settings.hadSettings()){
            loadSavedWorkspaces();
        }
    }
    //endregion

    //region Getters
    public String getNick(){
        return this.nick;
    }

    public String getEmail(){
        return this.email;
    }

    public List<String> getInterestKeywords() { return this.interestKeywords; }

    @Override
    public String toString(){
        return nick;
    }
    //endregion

    //region Private Methods
    private void loadSavedWorkspaces() {
        for(WorkspaceDTO wsDTO : settings.getOwnedWorkspaces()){
            OwnedWorkspace savedWS = new OwnedWorkspace(wsDTO);
            ownedWorkspaces.add(savedWS);
            if(savedWS.getAllowedUsers().size() != 0)
                for(String username : savedWS.getAllowedUsers())
                    this.invite(savedWS, username);
        }
        for(WorkspaceDTO wsDTO : settings.getForeignWorkspaces()){
            //TODO: something to do
        }
    }
    //endregion

    //region Workspace Methods
    public List<Workspace> getForeignWorkspaces() {
        return foreignWorkspaces;
    }

    public List<Workspace> getOwnedWorkspaces() {
        return ownedWorkspaces;
    }

    public ArrayList<String> getOwnedWorkspacesNames() {
        ArrayList<String> workspaceNames = new ArrayList<String>();
        for(int i=0;i<ownedWorkspaces.size();i++)
            workspaceNames.add(ownedWorkspaces.get(i).getName());
        return workspaceNames;
    }

    public ArrayList<String> getForeignWorkspacesNames() {
        ArrayList<String> workspaceNames = new ArrayList<String>();
        for(int i=0;i<ownedWorkspaces.size();i++)
            workspaceNames.add(ownedWorkspaces.get(i).getName());
        return workspaceNames;
    }

    public Workspace getOwnedWorkspaceByName(String name) throws WorkspaceNotFoundException{
        Predicate<Workspace> validator = new WorkspaceNamePredicate(name);
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

    public boolean existWorkspace(String name){
        Predicate<Workspace> validator = new WorkspaceNamePredicate(name);
        Workspace result = null;
        for(Workspace workspace : ownedWorkspaces){
            if(validator.apply(workspace)){
                result = workspace;
                break;
            }
        }
        if(result == null)
            return false;
        return true;
    }

    public boolean hasForeignWorkspaceByName(String name){
        Predicate<Workspace> validator = new WorkspaceNamePredicate(name);
        boolean result = false;

        for(Workspace workspace : foreignWorkspaces){
            if(validator.apply(workspace)){
                result = true;
                break;
            }
        }
        return result;
    }

    public Workspace getForeignWorkspaceByName(String name) throws WorkspaceNotFoundException{
        Predicate<Workspace> validator = new WorkspaceNamePredicate(name);
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
        if(existWorkspace(name)) throw new CreateWorkspaceException("Already exists a workspace with that name");

        Workspace newWorkspace = new OwnedWorkspace(name, isPublic, quota);
        ownedWorkspaces.add(newWorkspace);
        try {
            settings.saveOwnedWorkspace(new WorkspaceDTO((OwnedWorkspace)newWorkspace));
        } catch (WriteToFileException e) {
            throw new CreateWorkspaceException(e.getMessage());
        }
        return newWorkspace;
    }

    public void addForeignWorkspace(Workspace workspace){
        foreignWorkspaces.add(workspace);
    }

    public void invite(OwnedWorkspace workspace, String name){
        workspace.invite(name);
        Workspace newForeign = new ForeignLocalWorkspace(workspace, email);
        if(name.equals(this.getNick())){
            foreignWorkspaces.add(newForeign);
            try {
                settings.updateOwnedWorkspace(new WorkspaceDTO(workspace));
            } catch (Exception e) {
                Log.d("[AirDesk]", e.getMessage());
            }
        } else {
            if(wifiHandler.gotConnectionTo(name)){
                try {
                    wifiHandler.sendMessage(new InviteWSMessage(getNick(), new WorkspaceDTO(workspace)).toJSON().toString(), name);
                } catch (ServiceNotBoundException e) {
                    Log.d(TAG, "Service not bound at invite: " + e.getMessage());
                } catch (JSONException e) {
                    Log.d(TAG, "Error at converting to JSON in Invite: " + e.getMessage());
                }
            }
        }
    }

    public void uninvite(OwnedWorkspace workspace, String email){
        workspace.uninvite(email);
        Workspace newForeign = new ForeignLocalWorkspace(workspace, email);
        if(email.equals(this.getEmail())){
            foreignWorkspaces.remove(newForeign);
            try {
                settings.updateOwnedWorkspace(new WorkspaceDTO(workspace));
            } catch (Exception e) {
                Log.d("[AirDesk]", e.getMessage());
            }
        }
    }

    public void deleteWorkspace(Workspace workspace){
        try {
            workspace.delete();
            ownedWorkspaces.remove(workspace);
            settings.removeOwnedWorkspace(new WorkspaceDTO((OwnedWorkspace)workspace));
        } catch (Exception e) {
            Log.d("[AirDesk]", e.getMessage());
        }
    }

    public List<WorkspaceDTO> searchWorkspaces(String keyword){
        List<WorkspaceDTO> results = new ArrayList<>();
        for(Workspace workspace : this.getOwnedWorkspaces()){
            if(workspace.hasKeyword(keyword))
                results.add(new WorkspaceDTO((OwnedWorkspace) workspace));
        }
        return results;
    }
    //endregion

    //region File Methods
    public void createFile(String fileName, Workspace workspace) throws QuotaLimitExceededException, CreateFileException, IOException {
        workspace.createFile(fileName);
    }
    public void createFile(String fileName, String workspaceName) throws WorkspaceNotFoundException, QuotaLimitExceededException, CreateFileException, IOException {
        getOwnedWorkspaceByName(workspaceName).createFile(fileName);
    }

    public void deleteFile(String fileName, Workspace workspace) throws ADFileNotFoundException, DeleteFileException {
        workspace.removeFile(fileName);
    }
    public void deleteFile(String fileName, String workspaceName) throws WorkspaceNotFoundException, ADFileNotFoundException, DeleteFileException {
        getOwnedWorkspaceByName(workspaceName).removeFile(fileName);
    }
    //endregion

    //region User Methods
    public void addInterestKeyword(String keyword) { this.getInterestKeywords().add(keyword); }

    public void removeInterestKeyword(String keyword) { this.getInterestKeywords().remove(keyword); }
    //endregion
}
