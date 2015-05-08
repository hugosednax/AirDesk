package pt.ulisboa.tecnico.cmov.airdesk.WiFiDirect;

import org.json.JSONArray;
import org.json.JSONObject;

import pt.ulisboa.tecnico.cmov.airdesk.FileSystem.ADFile;
import pt.ulisboa.tecnico.cmov.airdesk.FileSystem.JSONHandler;
import pt.ulisboa.tecnico.cmov.airdesk.Workspace.Workspace;

/**
 * Created by Toninho on 5/2/2015.
 */
public class Message {

    String content;
    Workspace workspace;
    Type typeOfMessage;
    public enum Type{
        INVITE, INHERE, IGOTTHIS, INTEREST, UGOTTHIS, CONNECTO;
    }

    public Message(Type typeOfMessage){
        this.typeOfMessage = typeOfMessage;
    }

    public Message(Type typeOfMessage, String content){
        this.typeOfMessage = typeOfMessage;
        this.content = content;
    }

    public Message(Type typeOfMessage, Workspace workspace){
        this.typeOfMessage = typeOfMessage;
        this.workspace = workspace;
    }


    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Workspace getWorkspace() {
        return workspace;
    }

    public void setWorkspace(Workspace workspace) {
        this.workspace = workspace;
    }

    public Type getTypeOfMessage() {
        return typeOfMessage;
    }

    public void setTypeOfMessage(Type typeOfMessage) {
        this.typeOfMessage = typeOfMessage;
    }

    public JSONObject toJSON(){
        JSONObject json = new JSONObject();
        try {
            json.put("content", content);
            json.put("workspace", workspace.toJSON());
            json.put("typeOfMessage",typeOfMessage);
        }catch (Exception e){

        }
        return json;
    }
}
