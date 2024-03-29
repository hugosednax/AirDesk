package pt.ulisboa.tecnico.cmov.airdesk.User;

import android.util.Log;

import com.android.internal.util.Predicate;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import pt.ulisboa.tecnico.cmov.airdesk.Application.AirDeskApp;
import pt.ulisboa.tecnico.cmov.airdesk.DTO.WorkspaceDTO;
import pt.ulisboa.tecnico.cmov.airdesk.Exception.FileNotFoundException;
import pt.ulisboa.tecnico.cmov.airdesk.Exception.CreateFileException;
import pt.ulisboa.tecnico.cmov.airdesk.Exception.CreateWorkspaceException;
import pt.ulisboa.tecnico.cmov.airdesk.Exception.DeleteFileException;
import pt.ulisboa.tecnico.cmov.airdesk.Exception.NotDirectoryException;
import pt.ulisboa.tecnico.cmov.airdesk.Exception.QuotaLimitExceededException;
import pt.ulisboa.tecnico.cmov.airdesk.Exception.ServiceNotBoundException;
import pt.ulisboa.tecnico.cmov.airdesk.Exception.WriteToFileException;
import pt.ulisboa.tecnico.cmov.airdesk.FileSystem.JSONHandler;
import pt.ulisboa.tecnico.cmov.airdesk.Message.InterestMessage;
import pt.ulisboa.tecnico.cmov.airdesk.Message.InviteWSMessage;
import pt.ulisboa.tecnico.cmov.airdesk.Message.RemoveInviteMessage;
import pt.ulisboa.tecnico.cmov.airdesk.Predicate.WorkspaceNamePredicate;
import pt.ulisboa.tecnico.cmov.airdesk.Exception.WorkspaceNotFoundException;
import pt.ulisboa.tecnico.cmov.airdesk.WiFiDirect.WifiNotificationHandler;
import pt.ulisboa.tecnico.cmov.airdesk.Workspace.ForeignRemoteWorkspace;
import pt.ulisboa.tecnico.cmov.airdesk.Workspace.OwnedWorkspace;
import pt.ulisboa.tecnico.cmov.airdesk.Workspace.Workspace;

/**
 * Created by hugo__000
 */
public class User {
    //region Class Const
    private static final String TAG = "[AirDesk]";
    //endregion

    //region Class Variables
    private String nick;
    private String email;
    private List<ForeignRemoteWorkspace> foreignWorkspaces;
    private List<OwnedWorkspace> ownedWorkspaces;
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
            this.settings = new JSONHandler();
        } catch (Exception e) {
            Log.d(AirDeskApp.LOG_TAG, "Error at creating JSONHandler " + e.getMessage());
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
        return email;
    }
    //endregion

    //region Private Methods
    private void loadSavedWorkspaces() {
        for(WorkspaceDTO wsDTO : settings.getOwnedWorkspaces()){
            OwnedWorkspace savedWS = new OwnedWorkspace(wsDTO);
            ownedWorkspaces.add(savedWS);
            if(savedWS.getAllowedUsers().size() != 0)
                for(String email : savedWS.getAllowedUsers())
                    this.invite(savedWS, email);
        }
    }

    private String parseWSName(String arg1) {
        String delimit = "[@]";
        String[] tokens = arg1.split(delimit);
        return tokens[0];
    }
    //endregion

    //region Workspace Methods
    public List<ForeignRemoteWorkspace> getForeignWorkspaces() {
        return foreignWorkspaces;
    }

    public List<OwnedWorkspace> getOwnedWorkspaces() {
        return ownedWorkspaces;
    }

    public ArrayList<String> getOwnedWorkspacesNames() {
        ArrayList<String> workspaceNames = new ArrayList<>();
        for(int i=0;i<ownedWorkspaces.size();i++)
            workspaceNames.add(ownedWorkspaces.get(i).getName());
        return workspaceNames;
    }

    public ArrayList<String> getForeignWorkspacesNames() {
        ArrayList<String> workspaceNames = new ArrayList<>();
        for(int i=0;i<ownedWorkspaces.size();i++)
            workspaceNames.add(ownedWorkspaces.get(i).getName());
        return workspaceNames;
    }

    public OwnedWorkspace getOwnedWorkspaceByName(String name) throws WorkspaceNotFoundException{
        Predicate<Workspace> validator = new WorkspaceNamePredicate(name);
        OwnedWorkspace result = null;
        for(OwnedWorkspace workspace : ownedWorkspaces){
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
        return result != null;
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

    public ForeignRemoteWorkspace getForeignWorkspaceByName(String name) throws WorkspaceNotFoundException{
        Predicate<Workspace> validator = new WorkspaceNamePredicate(name);
        ForeignRemoteWorkspace result = null;
        for(ForeignRemoteWorkspace workspace : foreignWorkspaces){
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

        OwnedWorkspace newWorkspace = new OwnedWorkspace(name, isPublic, quota);
        ownedWorkspaces.add(newWorkspace);
        try {
            settings.saveOwnedWorkspace(new WorkspaceDTO(newWorkspace));
        } catch (WriteToFileException e) {
            throw new CreateWorkspaceException(e.getMessage());
        }
        return newWorkspace;
    }

    public void addForeignWorkspace(ForeignRemoteWorkspace workspace){
        if(!hasForeignWorkspaceByName(workspace.getName())){
            getForeignWorkspaces().add(workspace);
        }
    }

    public void removeForeignWorkspace(String workspace){
        //TODO
    }

    public void invite(OwnedWorkspace workspace, String email){
        workspace.addToAllowedUsers(email);
        try {
            settings.updateOwnedWorkspace(new WorkspaceDTO(workspace));
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
        }
        if(email.equals(this.getEmail())){
            Log.d(TAG, "added yourself to the list, whaaaat?");
        } else {
            if(wifiHandler.gotConnectionTo(email)){
                Log.d(TAG, "Got connection to " + email + ", going to send message");
                try {
                    wifiHandler.sendMessage(new InviteWSMessage(getEmail(), new WorkspaceDTO(workspace)).toJSON().toString(), email);
                } catch (ServiceNotBoundException e) {
                    Log.d(TAG, "Service not bound at invite: " + e.getMessage());
                } catch (JSONException e) {
                    Log.d(TAG, "Error at converting to JSON in Invite: " + e.getMessage());
                }
            }
        }
    }

    public void unInvite(OwnedWorkspace workspace, String email){
        workspace.removeFromAllowedUsers(email);
        try {
            settings.updateOwnedWorkspace(new WorkspaceDTO(workspace));
        } catch (Exception e) {
            Log.d("[AirDesk]", e.getMessage());
        }
    }

    public void deleteWorkspace(OwnedWorkspace workspace){
        try {
            ownedWorkspaces.remove(workspace);
            settings.removeOwnedWorkspace(new WorkspaceDTO(workspace));
            workspace.delete();
        } catch (Exception e) {
            Log.d("[AirDesk]", e.getMessage());
        }
    }

    public void deleteWorkspace(ForeignRemoteWorkspace workspace){
        try {
            foreignWorkspaces.remove(workspace);
            wifiHandler.sendMessage(new RemoveInviteMessage(this.getEmail(), parseWSName(workspace.getName())).toJSON().toString(), workspace.getOwner());
        } catch (JSONException e) {
            //TODO
        } catch (ServiceNotBoundException e) {
            //TODO
        }
    }

    public List<WorkspaceDTO> searchWorkspaces(String keyword){
        List<WorkspaceDTO> results = new ArrayList<>();
        for(OwnedWorkspace workspace : this.getOwnedWorkspaces()){
            if(workspace.hasKeyword(keyword))
                results.add(new WorkspaceDTO(workspace));
        }
        return results;
    }

    public void cleanAllWorkspaces(){
        for(OwnedWorkspace ws : getOwnedWorkspaces())
            ws.cleanAllFiles();
    }

    //endregion

    //region File Methods
    public void createFile(String fileName, Workspace workspace) throws QuotaLimitExceededException, CreateFileException, IOException {
        workspace.createFile(fileName);
    }
    public void createFile(String fileName, String workspaceName) throws WorkspaceNotFoundException, QuotaLimitExceededException, CreateFileException {
        getOwnedWorkspaceByName(workspaceName).createFile(fileName);
    }

    public void updateFile(String fileName, OwnedWorkspace workspace, String text) throws QuotaLimitExceededException, FileNotFoundException, NotDirectoryException {
        workspace.updateFile(fileName, text);
    }
    public void updateFile(String fileName, String workspaceName, String text) throws WorkspaceNotFoundException, QuotaLimitExceededException, FileNotFoundException, NotDirectoryException {
        getOwnedWorkspaceByName(workspaceName).updateFile(fileName, text);
    }

    public void deleteFile(String fileName, Workspace workspace) throws FileNotFoundException, DeleteFileException {
        workspace.removeFile(fileName);
    }
    public void deleteFile(String fileName, String workspaceName) throws WorkspaceNotFoundException, FileNotFoundException, DeleteFileException {
        getOwnedWorkspaceByName(workspaceName).removeFile(fileName);
    }

    public String getFileContent(String fileName, OwnedWorkspace workspace) throws FileNotFoundException, IOException {
        return workspace.getFileByName(fileName).getContent();
    }
    public String getFileContent(String fileName, String workspace) throws FileNotFoundException, IOException, WorkspaceNotFoundException {
        return getOwnedWorkspaceByName(workspace).getFileByName(fileName).getContent();
    }
    //endregion

    //region User Methods
    public void addInterestKeyword(String keyword) {
        this.getInterestKeywords().add(keyword);
        List<String> keywords = new ArrayList<>();
        keywords.add(keyword);
        try {
            wifiHandler.broadcastMessage(new InterestMessage(getEmail(), keywords).toJSON().toString());
        } catch (ServiceNotBoundException e) {
            Log.d(TAG, "Service not bound, can't broadcast new interests");
        } catch (JSONException e) {
            Log.d(TAG, "JSON exception at User addInterestKeyword: " + e.getMessage());
        }
    }

    public void removeInterestKeyword(String keyword) { this.getInterestKeywords().remove(keyword); }

    public void updateInvites(String guestName){
        for(OwnedWorkspace ws : getOwnedWorkspaces()){
            for(String guest : ws.getAllowedUsers()){
                if(guest.equals(guestName) && wifiHandler.gotConnectionTo(guestName)){
                    Log.d(TAG, "Got connection to " + guestName + ", and found a reference to " + ws.getName() + ", going to invite him");
                    try {
                        wifiHandler.sendMessage(new InviteWSMessage(getEmail(), new WorkspaceDTO(ws)).toJSON().toString(), guestName);
                    } catch (ServiceNotBoundException e) {
                        Log.d(TAG, "Service not bound at invite: " + e.getMessage());
                    } catch (JSONException e) {
                        Log.d(TAG, "Error at converting to JSON in Invite: " + e.getMessage());
                    }
                }
            }
        }
    }

    public void updateAllInvites(){
        for(OwnedWorkspace ws : getOwnedWorkspaces()){
            for(String guest : ws.getAllowedUsers()){
                if(wifiHandler.gotConnectionTo(guest)){
                    Log.d(TAG, "Got connection to " + guest + ", and found a reference to " + ws.getName() + ", going to reInvite him");
                    try {
                        wifiHandler.sendMessage(new InviteWSMessage(getEmail(), new WorkspaceDTO(ws)).toJSON().toString(), guest);
                    } catch (ServiceNotBoundException e) {
                        Log.d(TAG, "Service not bound at invite: " + e.getMessage());
                    } catch (JSONException e) {
                        Log.d(TAG, "Error at converting to JSON in Invite: " + e.getMessage());
                    }
                }
            }
        }
    }

    public void updateKeywords(String user){
        try {
            wifiHandler.sendMessage(new InterestMessage(getEmail(), getInterestKeywords()).toJSON().toString(), user);
        } catch (ServiceNotBoundException e) {
            Log.d(TAG, "Cant update keywords with unbound service");
        } catch (JSONException e) {
            Log.d(TAG, "Error JSON parsing the InterestMessage for updateKeywords");
        }
    }

    public void updateAllKeywords(){
        try {
            wifiHandler.broadcastMessage(new InterestMessage(getEmail(), getInterestKeywords()).toJSON().toString());
        } catch (ServiceNotBoundException e) {
            Log.d(TAG, "Cant update keywords with unbound service");
        } catch (JSONException e) {
            Log.d(TAG, "Error JSON parsing the InterestMessage for updateKeywords");
        }
    }
    //endregion
}
