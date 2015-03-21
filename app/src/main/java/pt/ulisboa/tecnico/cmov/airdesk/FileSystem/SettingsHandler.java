package pt.ulisboa.tecnico.cmov.airdesk.FileSystem;

import android.provider.MediaStore;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import pt.ulisboa.tecnico.cmov.airdesk.Application.AirDeskApp;
import pt.ulisboa.tecnico.cmov.airdesk.DTO.WorkspaceDTO;
import pt.ulisboa.tecnico.cmov.airdesk.Exception.CantCreateFileException;
import pt.ulisboa.tecnico.cmov.airdesk.Exception.WriteToFileException;

/**
 * Created by Filipe Teixeira on 20/03/2015.
 */
public class SettingsHandler {
    //Foreign workspaces save not used
    private static final String DEFAULT_FILE_CONTENT ="OwnedWorkspaces" + "\n" + "NONE" + "\n" + "ForeignWorkspaces" + "\n"
            + "NONE";
    File settings;
    List<WorkspaceDTO> ownedWorkspaces;
    List<WorkspaceDTO> foreignWorkspaces;
    boolean hadSettings;


    public SettingsHandler() throws CantCreateFileException, WriteToFileException {

        File mainDir = AirDeskApp.getAppContext().getDir("data", AirDeskApp.getAppContext().MODE_PRIVATE);
        settings = new File(mainDir, ".settings.txt");

        if(settings.exists()){
            hadSettings = true;
            try {
                readSettingsFile();
            } catch (FileNotFoundException e) {
                //TODO
                e.printStackTrace();
            }
        } else{
            try {
                settings.createNewFile();
            } catch (IOException e) {
                throw new CantCreateFileException(e.getMessage());
            }

            try {
                writeDefaultFile();
            } catch (IOException e) {
                throw new WriteToFileException(e.getMessage());
            }

            ownedWorkspaces = new ArrayList<WorkspaceDTO>();
            foreignWorkspaces = new ArrayList<WorkspaceDTO>();
            hadSettings = false;
        }
    }

    private void writeDefaultFile() throws IOException {
        FileWriter fw = new FileWriter(settings.getAbsoluteFile());
        BufferedWriter bw = new BufferedWriter(fw);
        try {
            bw.write(DEFAULT_FILE_CONTENT);
            bw.close();
        } catch (IOException e) {
            Log.d("[AirDesk]", "Error writing default string to Settings file" + "\n" + e.getMessage());
        }
    }

    private boolean readSettingsFile() throws FileNotFoundException {
        boolean result = false;
        boolean atOwned = true;
        if(settings.getTotalSpace() > 0){
            BufferedReader br = new BufferedReader(new FileReader(settings.getPath()));
            try {
                String line = br.readLine();

                while (line != null) {
                    System.out.println(line);
                    if(atOwned){
                        if(!line.equals("OwnedWorkspaces")){
                            if(line.equals("ForeignWorkspaces")){
                                atOwned = false;
                            } else{
                                if(!line.equals("NONE")){
                                    String[] workspaceSettings = line.split("\\s+");
                                    WorkspaceDTO newWS = new WorkspaceDTO(workspaceSettings[0], (Integer.parseInt(workspaceSettings[1]) != 0), Integer.parseInt(workspaceSettings[2]));
                                    ownedWorkspaces.add(newWS);
                                }
                            }
                        }
                    } else{
                        //TODO: saved foreign workspaces
                    }
                    line = br.readLine();
                }
            } catch (IOException e) {
                //TODO
                e.printStackTrace();
            } finally {
                try {
                    br.close();
                } catch (IOException e) {
                    // TODO
                    e.printStackTrace();
                }
            }
            result = true;
        }
        return result;
    }

    public boolean hadSettings() {
        return hadSettings;
    }
}
