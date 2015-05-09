package pt.ulisboa.tecnico.cmov.airdesk.Message;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import pt.ulisboa.tecnico.cmov.airdesk.Exception.MessageParsingException;

/**
 * Created by Filipe Teixeira on 09/05/2015.
 */
public class MessageHandler {

    public static void parseMessage(String message) throws JSONException, MessageParsingException {
        JSONObject JSONMessage = new JSONObject(message);
        Message.Type messageType = (Message.Type)JSONMessage.get(Message.MESSAGE_TYPE);
        if(messageType == Message.Type.IMHERE){
            MessageHandler.executeMessage(new ImHereMessage((String)JSONMessage.get(Message.MESSAGE_USER)));
        } else if(messageType == Message.Type.INVITE){
            MessageHandler.executeMessage(new ImHereMessage((String)JSONMessage.get(Message.MESSAGE_USER)));
        } else
            throw new MessageParsingException("No compatible Type found");
    }

    private static void executeMessage(ImHereMessage message){
        //TODO
        Log.d("[AirDesk]", "Parsed an ImHereMessage from " + message.getUser());
    }
}
