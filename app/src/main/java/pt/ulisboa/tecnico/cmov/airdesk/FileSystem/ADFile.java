package pt.ulisboa.tecnico.cmov.airdesk.FileSystem;
import android.os.Environment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import pt.ulisboa.tecnico.cmov.airdesk.Workspace.Workspace;

/**
 * Created by hugo__000 on 12/03/2015.
 */
public class ADFile {

    private File file;
    private boolean editable;
    private Workspace workspace;

    public ADFile(String name, Workspace workspace) {
        file = new File(Environment.getExternalStorageDirectory() + File.separator + workspace.getName() + File.separator + name + ".txt");
        editable = true;
        this.workspace = workspace;
        workspace.addFile(this);
    }

    public File getFile() {
        return file;
    }

    public boolean isEditable() {
        return editable;
    }

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
    }
}
