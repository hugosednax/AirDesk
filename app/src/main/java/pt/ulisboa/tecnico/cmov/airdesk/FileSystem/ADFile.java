package pt.ulisboa.tecnico.cmov.airdesk.FileSystem;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import pt.ulisboa.tecnico.cmov.airdesk.Application.AirDeskApp;

/**
 * Created by hugo__000 on 12/03/2015.
 */
public class ADFile {
    //region Class Variables
    private File file;
    private String name;
    private boolean editable;
    //endregion

    //region Constructors
    public ADFile(String name, String workspaceName) throws IOException {
        this.file =  new File(""+AirDeskApp.getAppContext().getDir("data", Context.MODE_PRIVATE)+File.separatorChar+workspaceName, name + ".txt");
        file.createNewFile();
        this.name = name;
        this.editable = true;
    }

    public ADFile(File file) {
        this.file = file;
        this.name = file.getName();
        this.editable = true;
    }
    //endregion

    //region Getters
    public String getName() {
        return name;
    }

    public long getSize() { return file.length(); }

    public File getFile() {
        return file;
    }

    public String getFileName(){return file.getName();}

    public boolean isEditable() {
        return editable;
    }

    public String toString(){
        return name;
    }

    public JSONObject toJSON(){
        JSONObject json = new JSONObject();
        try {
            json.put("name", name);
            json.put("editable", editable);
            json.put("file", file.toString());
        }catch (Exception e){

        }
        return json;
    }
    //endregion

    //region File Methods
    public void save(String text){
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(file.getAbsolutePath());
            writer.print(text);
        } catch (FileNotFoundException e) {
            Log.d(AirDeskApp.LOG_TAG, e.getMessage());
        } finally{
            writer.close();
        }

        //Bellow code was a previous implementation, this check must be done in the workspace environment

        /*// File size calculation
        long fileSize = text.length() * 8;
        long oldFileSize = file.getTotalSpace();

        // Check if quota is passed
        if(workspace.getSize()+fileSize-oldFileSize >= workspace.getQuota()){
            System.out.println("TODO EXCEPTION");
        } else {
            WriteToFile (String text)
        }*/
    }
    //endregion
}
