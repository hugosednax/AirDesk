package pt.ulisboa.tecnico.cmov.airdesk.WiFiDirect;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.Messenger;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import pt.inesc.termite.wifidirect.SimWifiP2pBroadcast;
import pt.inesc.termite.wifidirect.SimWifiP2pDevice;
import pt.inesc.termite.wifidirect.SimWifiP2pDeviceList;
import pt.inesc.termite.wifidirect.SimWifiP2pInfo;
import pt.inesc.termite.wifidirect.SimWifiP2pManager;
import pt.inesc.termite.wifidirect.service.SimWifiP2pService;
import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocket;
import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocketManager;
import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocketServer;
import pt.ulisboa.tecnico.cmov.airdesk.Application.AirDeskApp;
import pt.ulisboa.tecnico.cmov.airdesk.DTO.WorkspaceDTO;
import pt.ulisboa.tecnico.cmov.airdesk.Exception.MessageParsingException;
import pt.ulisboa.tecnico.cmov.airdesk.Exception.ServiceNotBoundException;
import pt.ulisboa.tecnico.cmov.airdesk.Message.ImHereMessage;
import pt.ulisboa.tecnico.cmov.airdesk.Message.InviteWSMessage;
import pt.ulisboa.tecnico.cmov.airdesk.Message.Message;
import pt.ulisboa.tecnico.cmov.airdesk.R;

/**
 * Created by Filipe Teixeira on 30/04/2015.
 */
public class WifiNotificationHandler implements SimWifiP2pManager.PeerListListener, SimWifiP2pManager.GroupInfoListener {
    //region Constants
    private static final String TAG = "[AirDesk]";
    //endregion

    //region Class Variables
    private Activity currentActivity;
    private Context context;
    private SimWifiP2pManager mManager = null;
    private SimWifiP2pManager.Channel mChannel = null;
    private Messenger mService = null;
    private boolean mBound = false;
    private SimWifiP2pSocketServer mSrvSocket = null;
    private List<String> peersList;
    private TreeMap<String, SimWifiP2pSocket> userNetworkList;
    private String myUser;
    private boolean groupOwner = false;
    //endregion

    //region Constructor
    public WifiNotificationHandler(Context context) {
        this.context = context;
        this.peersList = new ArrayList<>();
        this.userNetworkList = new TreeMap<>();

        // initialize the WDSim API
        SimWifiP2pSocketManager.Init(context);

        // register broadcast receiver
        IntentFilter filter = new IntentFilter();
        filter.addAction(SimWifiP2pBroadcast.WIFI_P2P_STATE_CHANGED_ACTION);
        filter.addAction(SimWifiP2pBroadcast.WIFI_P2P_PEERS_CHANGED_ACTION);
        filter.addAction(SimWifiP2pBroadcast.WIFI_P2P_NETWORK_MEMBERSHIP_CHANGED_ACTION);
        filter.addAction(SimWifiP2pBroadcast.WIFI_P2P_GROUP_OWNERSHIP_CHANGED_ACTION);
        SimWifiP2pBroadcastReceiver receiver = new SimWifiP2pBroadcastReceiver(this);
        context.registerReceiver(receiver, filter);
    }
    //endregion

    //region Setters
    public void setCurrentActivity(Activity currentActivity) {
        this.currentActivity = currentActivity;
    }

    public void setMyUser(String user){
        this.myUser = user;
    }
    //endregion

    //region Service Controller
    private ServiceConnection mConnection = new ServiceConnection() {
        // callbacks for service binding, passed to bindService()

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            mService = new Messenger(service);
            mManager = new SimWifiP2pManager(mService);
            mChannel = mManager.initialize(currentActivity.getApplication(), context.getMainLooper(), null);
            mBound = true;
            mManager.requestPeers(mChannel, WifiNotificationHandler.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mService = null;
            mManager = null;
            mChannel = null;
            mBound = false;
        }
    };
    //endregion

    //region Notifiers
    protected void notifyWifiOn(){
        Toast.makeText(this.currentActivity, "WiFi Direct enabled",
                Toast.LENGTH_SHORT).show();
    }

    protected void notifyWifiOff(){
        Toast.makeText(this.currentActivity, "WiFi Direct disabled",
                Toast.LENGTH_SHORT).show();

    }

    protected void notifyPeersChanged(){
        Toast.makeText(this.currentActivity, "Peer list changed",
                Toast.LENGTH_SHORT).show();

        if (mBound) {
            mManager.requestPeers(mChannel, WifiNotificationHandler.this);
        }
    }

    protected void notifyNetworkChanged(){
        Toast.makeText(this.currentActivity, "Network membership changed",
                Toast.LENGTH_SHORT).show();
    }

    protected void notifyGroupChanged(){
        Toast.makeText(this.currentActivity, "Group ownership changed",
                Toast.LENGTH_SHORT).show();
        mManager.requestGroupInfo(mChannel, WifiNotificationHandler.this);
    }
    //endregion

    //region Wifi Controller
    public void wifiOn() {
        Intent intent = new Intent(context, SimWifiP2pService.class);
        context.bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        mBound = true;

        // spawn the chat server background task
        new IncomingCommTask().executeOnExecutor(
                AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void wifiOff() {
        if (mBound) {
            context.unbindService(mConnection);
            mBound = false;
            Toast.makeText(this.currentActivity, "WiFi Off",
                    Toast.LENGTH_SHORT).show();
        }
    }
    //endregion

    //region Communication API
    public void broadcast() throws ServiceNotBoundException {
        if (mBound) {
            for (String ip : peersList) {
                Log.d(TAG, "Broadcasting to " + ip);
                new OutgoingCommTask().execute(ip);
            }
        } else {
            throw new ServiceNotBoundException("Service not Bound");
        }
    }

    public void sendMessage(String message, String ip) throws ServiceNotBoundException {
        if (mBound) {
            //new OutgoingCommTask(message).execute(ip);
        } else {
            throw new ServiceNotBoundException("Service not Bound");
        }
    }
    //endregion

    //region Message API
    public void parseMessage(String message) throws JSONException, MessageParsingException {
        JSONObject JSONMessage = new JSONObject(message);
        Message.Type messageType = (Message.Type)JSONMessage.get(Message.MESSAGE_TYPE);

        if(messageType == Message.Type.IMHERE){
            executeMessage(new ImHereMessage((String) JSONMessage.get(Message.MESSAGE_USER)));

        } else if(messageType == Message.Type.INVITE){
            executeMessage(new InviteWSMessage((String) JSONMessage.get(Message.MESSAGE_USER),
                    new WorkspaceDTO((JSONObject) JSONMessage.get(Message.MESSAGE_WORKSPACE))));

        } else
            throw new MessageParsingException("No compatible Type found");
    }

    private void executeMessage(ImHereMessage message){
        //TODO
        Log.d("[AirDesk]", "Parsed an ImHereMessage from " + message.getUser());
    }

    private void executeMessage(InviteWSMessage message){
        //TODO
        Log.d("[AirDesk]", "Parsed an InviteMessage from" + message.getUser() + " for workspace " + message.getWorkspaceDTO().getName());
    }
    //endregion

    //region Listener Implementation
    @Override
    public void onPeersAvailable(SimWifiP2pDeviceList peers) {
        // compile list of devices in range
        List<String> peersListResult = new ArrayList<>();
        for (SimWifiP2pDevice device : peers.getDeviceList()) {
            peersListResult.add(device.getVirtIp());
            Log.d(TAG, "Reachable peer: " + device.getVirtIp());
        }
        peersList = peersListResult;
    }

    @Override
    public void onGroupInfoAvailable(SimWifiP2pDeviceList devices, SimWifiP2pInfo groupInfo) {
        groupOwner = groupInfo.askIsGO();
        if(this.groupOwner && mBound){
            try {
                broadcast();
            } catch (ServiceNotBoundException e) {
                Log.d(TAG, "This exception is verified, so this is bad, really bad");
            }
        }
    }
    //endregion

    private class IncomingCommTask extends AsyncTask<Void, SimWifiP2pSocket, Void> {
        String user = "error";

        @Override
        protected Void doInBackground(Void... params) {
            Log.d(TAG, "IncomingCommTask started and is waiting for connections(" + this.hashCode() + ").");

            try {
                mSrvSocket = new SimWifiP2pSocketServer(
                        Integer.parseInt(context.getString(R.string.port)));
            } catch (IOException e) {
                Log.d(TAG, e.getMessage());
            }
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    SimWifiP2pSocket socket = mSrvSocket.accept();
                    user = (new BufferedReader(new InputStreamReader(socket.getInputStream()))).readLine();
                    socket.getOutputStream().write(myUser.getBytes());
                    publishProgress(socket);
                } catch (IOException e) {
                    Log.d("Error accepting socket:", e.getMessage());
                    break;
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(SimWifiP2pSocket... values) {
            SimWifiP2pSocket socket = values[0];
            Log.d(TAG, "Inserting socket with User: " + user);
            userNetworkList.put(user, socket);
            user = "error";
            new ReceiveCommTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, socket);
        }
    }

    private class ReceiveCommTask extends AsyncTask<SimWifiP2pSocket, String, Void> {
        SimWifiP2pSocket s;

        @Override
        protected Void doInBackground(SimWifiP2pSocket... params) {
            BufferedReader sockIn;
            String st;

            s = params[0];
            try {
                sockIn = new BufferedReader(new InputStreamReader(s.getInputStream()));

                while ((st = sockIn.readLine()) != null) {
                    publishProgress(st);
                }
            } catch (IOException e) {
                Log.d(TAG, "Error reading socket: " + e.getMessage());
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            try {
                parseMessage(values[0]);
            }              catch (MessageParsingException e) {
                Log.d(TAG, "Received message with unknown type" + "\n Message:" + values[0]);
            } catch (JSONException e) {
                Log.d(TAG, "Received message with bad or none JSON encoding" + "\n Message:" + values[0]);
            }
        }

        @Override
        protected void onPostExecute(Void result) {
            if (!s.isClosed()) {
                try {
                    s.close();
                }
                catch (Exception e) {
                    Log.d(TAG, "Error closing socket: " + e.getMessage());
                }
            }
            s = null;
        }
    }

    private class OutgoingCommTask extends AsyncTask<String, Void, String> {
        SimWifiP2pSocket sendSocket;
        String user= "error";

        @Override
        protected void onPreExecute() {
            Log.d(TAG, "Connecting to socket");
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                sendSocket = new SimWifiP2pSocket(params[0],
                        Integer.parseInt(context.getString(R.string.port)));
                sendSocket.getOutputStream().write(myUser.getBytes());
                user = (new BufferedReader(new InputStreamReader(sendSocket.getInputStream()))).readLine();
                Log.d(TAG, "Inserting socket with User: " + user);
                userNetworkList.put(user, sendSocket);
            } catch (UnknownHostException e) {
                return "Unknown Host:" + e.getMessage();
            } catch (IOException e) {
                return "IO error:" + e.getMessage();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                Log.d(TAG, "Error at outgoing task: " + result);
            }
            else {
                Log.d(TAG, "Inserting socket with User: " + user);
                userNetworkList.put(user, sendSocket);
            }
        }
    }
}
