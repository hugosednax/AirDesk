package pt.ulisboa.tecnico.cmov.airdesk.FileSystem;
import java.io.File;

/**
 * Created by hugo__000 on 12/03/2015.
 */
public class ADFile {

    File file;

    public ADFile(String name) {
        file = new File(name);
    }

    public File getFile() {
        return file;
    }

    public boolean isBeingEdited() {
        return false;
    }

    public void edit(){

    }

    public void save(){

    }
}
