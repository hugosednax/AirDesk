package pt.ulisboa.tecnico.cmov.airdesk.Predicate;

import com.android.internal.util.Predicate;

import pt.ulisboa.tecnico.cmov.airdesk.FileSystem.ADFile;
import pt.ulisboa.tecnico.cmov.airdesk.Workspace.Workspace;

/**
 * Created by Filipe Teixeira on 13/03/2015.
 */
public class FileNamePredicate implements Predicate<ADFile> {
    private String name;

    public FileNamePredicate(String name){
        this.name = name;
    }

    public boolean apply(ADFile file){
        return file.getName().equals(name);
    }
}
