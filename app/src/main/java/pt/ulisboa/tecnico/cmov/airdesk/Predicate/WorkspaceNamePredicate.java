package pt.ulisboa.tecnico.cmov.airdesk.Predicate;

import com.android.internal.util.Predicate;

import java.util.Comparator;

import pt.ulisboa.tecnico.cmov.airdesk.Workspace.Workspace;

/**
 * Created by Filipe Teixeira on 13/03/2015.
 */
public class WorkspaceNamePredicate implements Predicate<Workspace> {
    private String name;

    public WorkspaceNamePredicate(String name){
        this.name = name;
    }

    public boolean apply(Workspace workspace){
        return workspace.getName().equals(name);
    }
}
