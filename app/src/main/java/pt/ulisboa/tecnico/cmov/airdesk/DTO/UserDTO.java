package pt.ulisboa.tecnico.cmov.airdesk.DTO;

import pt.ulisboa.tecnico.cmov.airdesk.User.User;
import pt.ulisboa.tecnico.cmov.airdesk.Workspace.Workspace;

/**
 * Created by Filipe Teixeira on 21/03/2015.
 */
public class UserDTO {
    //region Class Variables
    private String username;
    //endregion

    //region Constructors
    public UserDTO(String username) {
        this.username = username;
    }

    public UserDTO(User user){
        this.username = user.getNick();
    }
    //endregion

    //region Getters
    public String getUsername() {
        return username;
    }
    //endregion

    //region Setters
    //endregion
}
