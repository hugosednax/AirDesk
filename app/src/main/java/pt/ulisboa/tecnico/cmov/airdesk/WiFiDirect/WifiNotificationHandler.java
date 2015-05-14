package pt.ulisboa.tecnico.cmov.airdesk.WiFiDirect;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.Messenger;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import pt.inesc.termite.wifidirect.SimWifiP2pBroadcast;
import pt.inesc.termite.wifidirect.SimWifiP2pDevice;
import pt.inesc.termite.wifidirect.SimWifiP2pDeviceList;
import pt.inesc.termite.wifidirect.SimWifiP2pInfo;
import pt.inesc.termite.wifidirect.SimWifiP2pManager;
import pt.inesc.termite.wifidirect.service.SimWifiP2pService;
import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocket;
import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocketManager;
import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocketServer;
import pt.ulisboa.tecnico.cmov.airdesk.DTO.WorkspaceDTO;
import pt.ulisboa.tecnico.cmov.airdesk.Exception.MessageParsingException;
import pt.ulisboa.tecnico.cmov.airdesk.Exception.RemoteMethodException;
import pt.ulisboa.tecnico.cmov.airdesk.Exception.ServiceNotBoundException;
import pt.ulisboa.tecnico.cmov.airdesk.Exception.WorkspaceNotFoundException;
import pt.ulisboa.tecnico.cmov.airdesk.Message.FuncCallMessage;
import pt.ulisboa.tecnico.cmov.airdesk.Message.FuncResponseMessage;
import pt.ulisboa.tecnico.cmov.airdesk.Message.InterestMessage;
import pt.ulisboa.tecnico.cmov.airdesk.Message.InviteWSMessage;
import pt.ulisboa.tecnico.cmov.airdesk.Message.Message;
import pt.ulisboa.tecnico.cmov.airdesk.Message.RemoveInviteMessage;
import pt.ulisboa.tecnico.cmov.airdesk.R;
import pt.ulisboa.tecnico.cmov.airdesk.User.User;
import pt.ulisboa.tecnico.cmov.airdesk.Workspace.ForeignRemoteWorkspace;
/**
 * Created by Filipe Teixeira
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
    private TreeMap<String, ReceiveCommTask> commReceiveTaskTreeMap;
    private User myUser;
    private boolean groupOwner = false;
    //endregion

    //region Constructor
    public WifiNotificationHandler(Context context) {
        this.context = context;
        this.peersList = new ArrayList<>();
        this.userNetworkList = new TreeMap<>();
        this.commReceiveTaskTreeMap = new TreeMap<>();

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

    public void setMyUser(User user){
        this.myUser = user;
    }
    //endregion

    //region Getters
    public User getMyUser(){
        return myUser;
    }

    public String getMyUserEmail() { return getMyUser().getEmail(); }

    public boolean gotConnectionTo(String name){
        return userNetworkList.get(name) != null;
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

    protected void notifyToast(String message){
        final Context context = this.currentActivity;
        final CharSequence text = message;
        currentActivity.runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
            }
        });
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
                new OutgoingCommTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, ip);
            }
        } else {
            throw new ServiceNotBoundException("Service not Bound");
        }
    }

    public void broadcastMessage(String message) throws ServiceNotBoundException {
        if(mBound){
            List<String> usersConnected = new ArrayList<>(commReceiveTaskTreeMap.keySet());
            for(String user : usersConnected)
                sendMessage(message, user);
        } else {
            throw new ServiceNotBoundException("Service not Bound");
        }
    }

    public void sendMessage(String message, String user) throws ServiceNotBoundException {
        if (mBound) {
            new RemoteSendMessageTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, user, message);
        } else {
            throw new ServiceNotBoundException("Service not Bound");
        }
    }

    public FuncResponseMessage remoteMethodInvoke(String user, FuncCallMessage message) throws JSONException, RemoteMethodException {
        RemoteMethodCallTask remoteMethodCallTask = new RemoteMethodCallTask();
        remoteMethodCallTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, user, message.toJSON().toString());
        try {
            remoteMethodCallTask.get(2000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            throw new RemoteMethodException("Remote Method Call Task exception Interrupted thrown: " + e.getMessage());
        } catch (ExecutionException e) {
            throw new RemoteMethodException("Remote Method Call Task exception Execution thrown: " + e.getMessage());
        } catch (TimeoutException e) {
            throw new RemoteMethodException("Remote Method Call Task exception Timeout thrown: " + e.getMessage());
        }

        String result = remoteMethodCallTask.getResult();
        JSONObject responseJSON = new JSONObject(result);
        if(responseJSON.getBoolean(Message.MESSAGE_EXCEPTION_THROWN))
            return new FuncResponseMessage("remove", responseJSON.getBoolean(Message.MESSAGE_EXCEPTION_THROWN),
                    responseJSON.getString(Message.MESSAGE_EXCEPTION_NAME),
                    responseJSON.getString(Message.MESSAGE_EXCEPTION_MESSAGE));
        else
            return new FuncResponseMessage("remove", responseJSON.getBoolean(Message.MESSAGE_EXCEPTION_THROWN),
                    responseJSON.getString(Message.MESSAGE_RESULT));
    }
    //endregion

    //region Message API
    public Message parseMessage(String message) throws JSONException, MessageParsingException {
        Log.d(TAG, "Parsing message : " + message);
        JSONObject JSONMessage = new JSONObject(message);
        Message.Type messageType = Message.stringToEnum(JSONMessage.getString(Message.MESSAGE_TYPE));

        if(messageType == Message.Type.INVITE) {
            return new InviteWSMessage((String) JSONMessage.get(Message.MESSAGE_USER),
                    new WorkspaceDTO((JSONObject) JSONMessage.get(Message.MESSAGE_WORKSPACE)));

        } else if(messageType == Message.Type.FUNC_CALL) {
            FuncCallMessage.FuncType funcType = FuncCallMessage.stringToFuncEnum(JSONMessage.getString(Message.MESSAGE_FUNC_TYPE));
            if (funcType == FuncCallMessage.FuncType.CREATE_FILE)
                return new FuncCallMessage(FuncCallMessage.FuncType.CREATE_FILE,
                        JSONMessage.getString(Message.MESSAGE_USER),
                        JSONMessage.getString(Message.MESSAGE_ARG1),
                        JSONMessage.getString(Message.MESSAGE_ARG2));

            else if (funcType == FuncCallMessage.FuncType.REMOVE_FILE)
                return new FuncCallMessage(FuncCallMessage.FuncType.REMOVE_FILE,
                        JSONMessage.getString(Message.MESSAGE_USER),
                        JSONMessage.getString(Message.MESSAGE_ARG1),
                        JSONMessage.getString(Message.MESSAGE_ARG2));

            else if (funcType == FuncCallMessage.FuncType.UPDATE_FILE)
                return new FuncCallMessage(FuncCallMessage.FuncType.UPDATE_FILE,
                        JSONMessage.getString(Message.MESSAGE_USER),
                        JSONMessage.getString(Message.MESSAGE_ARG1),
                        JSONMessage.getString(Message.MESSAGE_ARG2),
                        JSONMessage.getString(Message.MESSAGE_ARG3));

            else if (funcType == FuncCallMessage.FuncType.GET_FILE_NAMES)
                return new FuncCallMessage(FuncCallMessage.FuncType.GET_FILE_NAMES,
                        JSONMessage.getString(Message.MESSAGE_USER),
                        JSONMessage.getString(Message.MESSAGE_ARG1));

            else if (funcType == FuncCallMessage.FuncType.GET_FILE_CONTENT)
                return new FuncCallMessage(FuncCallMessage.FuncType.GET_FILE_CONTENT,
                        JSONMessage.getString(Message.MESSAGE_USER),
                        JSONMessage.getString(Message.MESSAGE_ARG1),
                        JSONMessage.getString(Message.MESSAGE_ARG2));

            else if (funcType == FuncCallMessage.FuncType.EDITABLE)
                return new FuncCallMessage(FuncCallMessage.FuncType.EDITABLE,
                        JSONMessage.getString(Message.MESSAGE_USER),
                        JSONMessage.getString(Message.MESSAGE_ARG1),
                        JSONMessage.getString(Message.MESSAGE_ARG2));

            else if (funcType == FuncCallMessage.FuncType.CANCEL_EDIT)
                return new FuncCallMessage(FuncCallMessage.FuncType.CANCEL_EDIT,
                        JSONMessage.getString(Message.MESSAGE_USER),
                        JSONMessage.getString(Message.MESSAGE_ARG1),
                        JSONMessage.getString(Message.MESSAGE_ARG2));

            else throw new MessageParsingException("No compatible FuncType found");

        } else if(messageType == Message.Type.FUNC_RESP){
            return new FuncResponseMessage("", true, "");

        } else if(messageType == Message.Type.INTEREST){
            ArrayList<String> list = new ArrayList<>();
            JSONArray jsonArray = (JSONArray)JSONMessage.get(Message.MESSAGE_KEYWORDS);
            if (jsonArray != null) {
                int len = jsonArray.length();
                for (int i=0;i<len;i++){
                    list.add(jsonArray.get(i).toString());
                }
            }
            return new InterestMessage(JSONMessage.getString(Message.MESSAGE_USER), list);

        } else if(messageType == Message.Type.REMOVE_INVITE){
            return new RemoveInviteMessage(JSONMessage.getString(Message.MESSAGE_USER), JSONMessage.getString(Message.MESSAGE_WORKSPACE));
        }
        throw new MessageParsingException("No compatible Type found");
    }
    //endregion

    //region Private Methods
    private void addForeignWorkspace(InviteWSMessage inviteWSMessage) {
        String wsName = inviteWSMessage.getWorkspaceDTO().getName();
        Log.d(TAG, "Adding new foreign workspace " + wsName + " from " + inviteWSMessage.getUser());
        if(!getMyUser().hasForeignWorkspaceByName(inviteWSMessage.getWorkspaceDTO().getName())){
            notifyToast("Added " + wsName + " to ForeignWorkspace");
            getMyUser().addForeignWorkspace(new ForeignRemoteWorkspace(this, inviteWSMessage.getWorkspaceDTO(), inviteWSMessage.getUser()));
        }
    }

    private void inviteThroughKeywords(String user, List<String> keywords) {
        List<WorkspaceDTO> workspacesInterested = new ArrayList<>();
        for(String keyword : keywords)
            workspacesInterested.addAll(getMyUser().searchWorkspaces(keyword));
        for(WorkspaceDTO ws : workspacesInterested)
            try {
                sendMessage(new InviteWSMessage(getMyUserEmail(), ws).toJSON().toString(), user);
            } catch (ServiceNotBoundException e) {
                Log.d(TAG, "Cant update keywords with unbound service");
            } catch (JSONException e) {
                Log.d(TAG, "Error JSON parsing the InterestMessage for updateKeywords");
            }
    }
    //endregion

    //region Listener Implementation
    @Override
    public void onPeersAvailable(SimWifiP2pDeviceList peers) {
        // compile list of devices in range
        List<String> peersListResult = new ArrayList<>();
        for (SimWifiP2pDevice device : peers.getDeviceList()) {
            peersListResult.add(device.getVirtIp());
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

    public void closeSockets() {
        List<SimWifiP2pSocket> list = new ArrayList<>(userNetworkList.values());
        for(SimWifiP2pSocket socket : list)
            try {
                Log.d(TAG, "Closing socket");
                socket.close();

            } catch (IOException e) {
                Log.d(TAG, "Error closing socket in socketClose");
            }
        try {
            Log.d(TAG, "Closing Server Socket");
            mSrvSocket.close();
        } catch (IOException e) {
            Log.d(TAG, "Error closing server socket in socketClose");
        }
    }
    //endregion

    //region Remote Protocol Tasks
    private class IncomingCommTask extends AsyncTask<Void, SimWifiP2pSocket, Void> {
        String user = "error";
        String ipPeerList = "";

        @Override
        protected Void doInBackground(Void... params) {
            Log.d(TAG, "IncomingCommTask started and is waiting for connections(" + this.hashCode() + ").");

            try {
                if(mSrvSocket == null)
                    mSrvSocket = new SimWifiP2pSocketServer(
                            Integer.parseInt(context.getString(R.string.port)));
            } catch (IOException e) {
                Log.d(TAG, "Exception at Server socket: " + e.getMessage());
            }
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Log.d(TAG, "Accepted socket");
                    SimWifiP2pSocket socket = mSrvSocket.accept();

                    Log.d(TAG, "Waiting for user");
                    user = (new BufferedReader(new InputStreamReader(socket.getInputStream()))).readLine();


                    Log.d(TAG, "Waiting for ip list");
                    ipPeerList = (new BufferedReader(new InputStreamReader(socket.getInputStream()))).readLine();

                    if(!groupOwner)
                        if(!ipPeerList.equals("")){
                            List<String> list = parseIPlist(ipPeerList);
                            for(int i = 0; i < list.size(); i++){
                                Log.d(TAG, "Trying to connect to ip: " + list.get(i));
                                if(!peersList.contains(list.get(i))){
                                    new OutgoingCommTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, list.get(i));
                                }
                            }
                        }

                    socket.getOutputStream().write((myUser + "\n").getBytes());
                    publishProgress(socket);
                } catch (IOException e) {
                    Log.d(TAG, "Error accepting socket: " + e.getMessage());
                    break;
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(SimWifiP2pSocket... values) {
            if(user.equals(getMyUserEmail()))
                return;
            SimWifiP2pSocket socket = values[0];
            userNetworkList.put(user, socket);
            ReceiveCommTask receiveCommTask = new ReceiveCommTask(user);
            commReceiveTaskTreeMap.put(user, receiveCommTask);
            receiveCommTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, socket);
            Log.d(TAG, "Connected to " + user);
            getMyUser().updateInvites(user);
            getMyUser().updateKeywords(user);
            user = "error";
        }

        @Override
        protected void onPostExecute(Void result) {
            try {
                mSrvSocket.close();
            } catch (Exception e) {
                Log.d(TAG, "Error closing socket: " + e.getMessage());
            }
        }

        protected ArrayList<String> parseIPlist(String ipList){
            String delimit = "[|]";
            String[] tokens = ipList.split(delimit);
            ArrayList<String> list = new ArrayList<>();
            Collections.addAll(list, tokens);
            return list;
        }
    }

    private class ReceiveCommTask extends AsyncTask<SimWifiP2pSocket, String, Void> {
        SimWifiP2pSocket s;
        String response = null;
        String userEmail = "";

        ReceiveCommTask(String userEmail){
            this.userEmail = userEmail;
        }

        @Override
        protected Void doInBackground(SimWifiP2pSocket... params) {
            BufferedReader sockIn;
            String st;

            s = params[0];
            try {
                sockIn = new BufferedReader(new InputStreamReader(s.getInputStream()));
                while (!isCancelled() && (st = sockIn.readLine()) != null) {
                    try {
                        Message message = parseMessage(st);
                        if(message.getClass().equals(FuncResponseMessage.class)){
                            response = st;

                        } else if(message.getClass().equals(InviteWSMessage.class)){
                            InviteWSMessage inviteWSMessage = (InviteWSMessage) message;
                            String wsName = inviteWSMessage.getWorkspaceDTO().getName();
                            Log.d(TAG, "Received WS invite to " + wsName);
                            addForeignWorkspace(inviteWSMessage);

                        } else if (message.getClass().equals(FuncCallMessage.class)){
                            FuncCallMessage funcCallMessage = (FuncCallMessage) message;
                            FuncResponseMessage funcResponseMessage = funcCallMessage.execute(getMyUser());
                            s.getOutputStream().write((funcResponseMessage.toJSON().toString()+"\n").getBytes());

                        } else if (message.getClass().equals(InterestMessage.class)){
                            Log.d(TAG, "Received interest message");
                            InterestMessage interestMessage = (InterestMessage) message;
                            inviteThroughKeywords(interestMessage.getUser(), interestMessage.getKeywords());

                        } else if (message.getClass().equals(RemoveInviteMessage.class)){
                            Log.d(TAG, "Received remove invite message");
                            RemoveInviteMessage removeInviteMessage = (RemoveInviteMessage) message;
                            try {
                                getMyUser().getOwnedWorkspaceByName(removeInviteMessage.getWorkspaceName()).removeFromAllowedUsers(message.getUser());
                            } catch (WorkspaceNotFoundException e) {
                                Log.d(TAG, "A remove invite message was called over a non existent workspace: " + ((RemoveInviteMessage) message).getWorkspaceName());
                            }
                        }
                        //else ignore
                    } catch (JSONException | MessageParsingException e) {
                        Log.d(TAG, "Error at Receive task: " + e.getMessage());
                    }
                }
            } catch (IOException e) {
                Log.d(TAG, "Error reading socket: " + e.getMessage());
            }
            return null;
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
            userNetworkList.remove(userEmail);
        }
    }

    private class OutgoingCommTask extends AsyncTask<String, Void, String> {
        SimWifiP2pSocket sendSocket;
        String user= "error";

        @Override
        protected String doInBackground(String... params) {
            try {
                Log.d(TAG, "Outgoing Connecting to ip: " + params[0]);
                sendSocket = new SimWifiP2pSocket(params[0],
                        Integer.parseInt(context.getString(R.string.port)));

                Log.d(TAG, "Sending user");
                sendSocket.getOutputStream().write((myUser + "\n").getBytes());
                String peerListString="";
                if(groupOwner){
                    for(String ipClient : peersList){
                        peerListString+=ipClient+"|";
                    }
                }
                Thread.sleep(10);

                Log.d(TAG, "Sending ip list " + peerListString);
                sendSocket.getOutputStream().write((peerListString+"\n").getBytes());
                user = (new BufferedReader(new InputStreamReader(sendSocket.getInputStream()))).readLine();
            } catch (UnknownHostException e) {
                return "Unknown Host:" + e.getMessage();
            } catch (IOException e) {
                return "IO error:" + e.getMessage();
            } catch (InterruptedException e) {
                //ignore
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                Log.d(TAG, "Error at outgoing task: " + result);
            }
            else {
                if(user.equals(getMyUserEmail()))
                    return;
                userNetworkList.put(user, sendSocket);
                ReceiveCommTask receiveCommTask = new ReceiveCommTask(user);
                commReceiveTaskTreeMap.put(user, receiveCommTask);
                receiveCommTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, sendSocket);
                Log.d(TAG, "Connected to " + user);
                getMyUser().updateInvites(user);
                getMyUser().updateKeywords(user);
                user = "error";
            }
        }
    }

    private class RemoteMethodCallTask extends AsyncTask<String, Void, String>{
        String result = "error";
        String user = "";
        SimWifiP2pSocket socket;

        public String getResult(){ return result; }

        private void setResult(String result) { this.result = result; }

        @Override
        protected String doInBackground(String... params) {
            user = params[0];
            Log.d(TAG, "Calling remote method to " + user);
            String message = params[1];
            String result = null;
            socket = userNetworkList.get(user);
            if (socket == null){
                Log.d(TAG, "User " + user + " not in network list");
                return null;
            }
            try {
                ReceiveCommTask receiveCommTask = commReceiveTaskTreeMap.get(user);
                socket.getOutputStream().write((message + "\n").getBytes());
                while(true) {
                    if (!(receiveCommTask.response == null)) break;
                }
                result = receiveCommTask.response;
                receiveCommTask.response = null;
            } catch (IOException e) {
                Log.d(TAG, "IO error: " + e.getMessage());
            }
            this.setResult(result);
            return result;
        }
    }

    private class RemoteSendMessageTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {

            Log.d(TAG, "Sending message " + params[0]);
            String user = params[0];
            String message = params[1];
            SimWifiP2pSocket socket = userNetworkList.get(user);
            if (socket == null){
                Log.d(TAG, "User " + user + " not in network list");
            }else{
                try {
                    socket.getOutputStream().write((message + "\n").getBytes());
                } catch (IOException e) {
                    Log.d(TAG, "IO error: " + e.getMessage());
                }
            }
            return null;
        }
    }
    //endregion
}
