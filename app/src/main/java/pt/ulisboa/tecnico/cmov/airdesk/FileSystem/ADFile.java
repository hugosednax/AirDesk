package pt.ulisboa.tecnico.cmov.airdesk.FileSystem;
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
        file = new File(name + ".txt");
        editable = true;
        this.workspace = workspace;
    }

    public File getFile() {
        return file;
    }

    public boolean isEditable() {
        return editable;
    }

    public void save(String text){
        //TODO: Exception handle
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
