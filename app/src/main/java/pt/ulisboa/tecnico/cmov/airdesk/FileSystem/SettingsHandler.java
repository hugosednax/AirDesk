package pt.ulisboa.tecnico.cmov.airdesk.FileSystem;

import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
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
    //TODO: Foreign workspaces save implemented but still need the @<username> completion testing etc...
    //TODO: control of IO exceptions is not good, must discuss a way to solve it

    //region Private Class Constants
    private static final String DEFAULT_FILE_CONTENT ="OwnedWorkspaces" + "\n" + "ForeignWorkspaces" + "\n";
    //endregion

    //region Private Class Variables
    private File settings;
    private List<WorkspaceDTO> ownedWorkspaces;
    private List<WorkspaceDTO> foreignWorkspaces;
    private boolean hadSettings;
    JSONHandler handler;
    //endregion

    //region Constructor
    public SettingsHandler() throws CantCreateFileException, WriteToFileException, FileNotFoundException {
        File mainDir = AirDeskApp.getAppContext().getDir("data", AirDeskApp.getAppContext().MODE_PRIVATE);
        settings = new File(mainDir, "settings.txt");

        ownedWorkspaces = new ArrayList<WorkspaceDTO>();
        foreignWorkspaces = new ArrayList<WorkspaceDTO>();
        hadSettings = false;

        handler = new JSONHandler();

        if(settings.isFile()){
            hadSettings = true;
            readSettingsFile();
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
        }
    }
    //endregion

    //region Private Methods
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

    private void writeToFile(String content) throws IOException {
        PrintWriter writer = new PrintWriter(settings);
        writer.print(content);
        writer.close();
    }

    private boolean readSettingsFile() throws FileNotFoundException {
        boolean result = false;
        boolean atOwned = true;
        if(settings.getTotalSpace() > 0){
            BufferedReader br = new BufferedReader(new FileReader(settings.getPath()));
            try {
                String line = br.readLine();

                while (line != null) {
                    if(atOwned){
                        if(!line.equals("OwnedWorkspaces")){
                            if(line.equals("ForeignWorkspaces")){
                                atOwned = false;
                            } else{
                                String[] workspaceSettings = line.split("\\s+");
                                WorkspaceDTO newWS = new WorkspaceDTO(workspaceSettings[0], (Integer.parseInt(workspaceSettings[1]) != 0), Integer.parseInt(workspaceSettings[2]));
                                ownedWorkspaces.add(newWS);
                                br.mark(10);
                                String nextLine = br.readLine();
                                String[] nextLineSplit = nextLine.split("\\s+");
                                while(nextLineSplit[0].equals("[P]")){
                                    newWS.addAllowedUser(nextLineSplit[1]);
                                    br.mark(10);
                                    nextLine = br.readLine();
                                    nextLineSplit = nextLine.split("\\s+");
                                }
                                while(nextLineSplit[0].equals("[K]")){
                                    newWS.addKeyword(nextLineSplit[1]);
                                    br.mark(10);
                                    nextLine = br.readLine();
                                    nextLineSplit = nextLine.split("\\s+");
                                }
                                br.reset();
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
    //endregion

    //region Public API
    public List<WorkspaceDTO> getOwnedWorkspaces() {
        return ownedWorkspaces;
    }

    public List<WorkspaceDTO> getForeignWorkspaces() {
        return foreignWorkspaces;
    }

    public void saveOwnedWorkspace(WorkspaceDTO ws) throws FileNotFoundException {
        try {
            handler.saveOwnedWorkspace(ws);
        } catch (WriteToFileException e) {
            Log.d("[AirDesk]", e.getMessage());
        }
        BufferedReader br = new BufferedReader(new FileReader(settings.getPath()));
        String fileContent = "";
        try {
            String line = br.readLine();
            fileContent += line + "\n";
            fileContent += ws.getName() + " " + (ws.isPublic() ? 1 : 0) + " " + ws.getQuota() + "\n";

            for(String user : ws.getAllowedUsers())
                fileContent += "[P] " + user + "\n";

            for(String keyword : ws.getKeywords())
                fileContent += "[K] " + keyword + "\n";

            line = br.readLine();
            while (line != null) {
                fileContent += line + "\n";
                line = br.readLine();
            }
            writeToFile(fileContent);
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
    }

    public void updateOwnedWorkspace(WorkspaceDTO ws) throws FileNotFoundException{
        removeOwnedWorkspace(ws);
        saveOwnedWorkspace(ws);
    }

    public void removeOwnedWorkspace(WorkspaceDTO ws) throws FileNotFoundException {
        BufferedReader br = new BufferedReader(new FileReader(settings.getPath()));
        String fileContent = "";
        boolean atOwned = true;
        boolean atWS = false;
        try {
            String line = br.readLine();
            fileContent += line + "\n";
            line = br.readLine();
            while (line != null) {
                if(line.equals("ForeignWorkspaces")) {
                    atOwned = false;
                    fileContent += line + "\n";
                } else if(atOwned){
                    String[] wsLine = line.split("\\s+");
                    if(wsLine[0].equals(ws.getName()))
                        atWS = true;
                    else if(atWS){
                        if(!wsLine[0].equals("[P]")&&!wsLine[0].equals("[K]")) {
                            fileContent += line + "\n";
                            atWS = false;
                        }
                        // else ignore
                    } else fileContent += line + "\n";
                } else fileContent += line + "\n";
                line = br.readLine();
            }
            writeToFile(fileContent);
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
    }

    public void saveForeignWorkspace(WorkspaceDTO ws) throws FileNotFoundException {
        BufferedReader br = new BufferedReader(new FileReader(settings.getPath()));
        String fileContent = "";
        try {
            String line = br.readLine();
            while (line != null) {
                fileContent += line + "\n";
                line = br.readLine();
            }
            fileContent += ws.getName() + " " + (ws.isPublic() ? 1 : 0) + " " + ws.getQuota() + "\n";
            writeToFile(fileContent);
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
    }

    public void removeForeignWorkspace(WorkspaceDTO ws) throws FileNotFoundException {
        BufferedReader br = new BufferedReader(new FileReader(settings.getPath()));
        String fileContent = "";
        boolean atOwned = true;
        try {
            String line = br.readLine();
            fileContent += line + "\n";
            line = br.readLine();
            while (line != null) {
                if(line.equals("ForeignWorkspaces")) {
                    atOwned = false;
                    fileContent += line + "\n";
                } else if(!atOwned){
                    String[] wsLine = line.split("\\s+");
                    if(!wsLine[0].equals(ws.getName()))
                        fileContent += line + "\n";
                } else fileContent += line + "\n";
                line = br.readLine();
            }
            writeToFile(fileContent);
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
    }

    public boolean hadSettings() {
        return hadSettings;
    }
    //endregion
}
