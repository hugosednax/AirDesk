package pt.ulisboa.tecnico.cmov.airdesk.FileSystem;
import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import pt.ulisboa.tecnico.cmov.airdesk.Application.AirDeskApp;
import pt.ulisboa.tecnico.cmov.airdesk.Workspace.Workspace;

/**
 * Created by hugo__000 on 12/03/2015.
 */
public class ADFile {

    private File file;
    private String name;
    private boolean editable;

    public ADFile(String name, String workspaceName) throws IOException {
        this.file =  new File(""+AirDeskApp.getAppContext().getDir("data", Context.MODE_PRIVATE)+File.separatorChar+workspaceName, name + ".txt");
        file.createNewFile();
        this.name = name;
        this.editable = true;
    }

    public String getName() {
        return name;
    }

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

    /*
    public void save(String text){
        //TODO: Exception handle and creation

        // File size calculation
        long fileSize = text.length() * 8;
        long oldFileSize = file.getTotalSpace();

        // Check if quota is passed
        if(workspace.getSize()+fileSize-oldFileSize >= workspace.getQuota()){
            System.out.println("TODO EXCEPTION");
        } else {
            PrintWriter writer = null;
            try {
                writer = new PrintWriter(file.getName(), "UTF-8");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            writer.println(text);
            writer.close();
        }
    }*/
}
