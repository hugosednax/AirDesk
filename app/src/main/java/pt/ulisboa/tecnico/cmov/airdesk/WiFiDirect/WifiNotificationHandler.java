package pt.ulisboa.tecnico.cmov.airdesk.WiFiDirect;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.Messenger;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.UnknownHostException;
import java.nio.channels.NotYetBoundException;
import java.util.ArrayList;
import java.util.List;

import pt.inesc.termite.wifidirect.SimWifiP2pBroadcast;
import pt.inesc.termite.wifidirect.SimWifiP2pDevice;
import pt.inesc.termite.wifidirect.SimWifiP2pDeviceList;
import pt.inesc.termite.wifidirect.SimWifiP2pInfo;
import pt.inesc.termite.wifidirect.SimWifiP2pManager;
import pt.inesc.termite.wifidirect.service.SimWifiP2pService;
import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocket;
import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocketManager;
import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocketServer;
import pt.ulisboa.tecnico.cmov.airdesk.Exception.ServiceNotBoundException;
import pt.ulisboa.tecnico.cmov.airdesk.R;

/**
 * Created by Filipe Teixeira on 30/04/2015.
 */
public class WifiNotificationHandler implements SimWifiP2pManager.PeerListListener, SimWifiP2pManager.GroupInfoListener {
    Activity currentActivity;
    Context context;
    private SimWifiP2pManager mManager = null;
    private SimWifiP2pManager.Channel mChannel = null;
    private Messenger mService = null;
    private boolean mBound = false;
    private SimWifiP2pSocketServer mSrvSocket = null;
    private SimWifiP2pSocket mCliSocket = null;
    private ReceiveCommTask mComm = null;

    List<String> peersList;

    private final String TAG = "[AirDesk]";

    public WifiNotificationHandler(Context context) {
        Log.d(TAG, "WifiNotificationHandler started");
        this.context = context;
        this.peersList = new ArrayList<>();

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

        Log.d(TAG, "WifiNotificationHandler creation done");
    }

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
    }
    //endregion

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

    public void broadcast(String message) throws ServiceNotBoundException {
        if (mBound) {
            for (String ip : peersList) {
                new OutgoingCommTask(message).execute(ip);
            }
        } else {
            throw new ServiceNotBoundException("Service not Bound");
        }
    }

    public void setCurrentActivity(Activity currentActivity) {
        this.currentActivity = currentActivity;
    }

    @Override
    public void onPeersAvailable(SimWifiP2pDeviceList peers) {
        // compile list of devices in range
        List<String> peersListResult = new ArrayList<>();
        for (SimWifiP2pDevice device : peers.getDeviceList()) {
            peersListResult.add(device.getVirtIp());
            Log.d(TAG, "peer: " + device.getVirtIp());
        }
        peersList = peersListResult;
    }

    @Override
    public void onGroupInfoAvailable(SimWifiP2pDeviceList devices, SimWifiP2pInfo groupInfo) {
        //TODO
    }


    /*
	 * Classes implementing message exchange
	 */

    public class IncomingCommTask extends AsyncTask<Void, SimWifiP2pSocket, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            Log.d(TAG, "IncomingCommTask started (" + this.hashCode() + ").");

            try {
                mSrvSocket = new SimWifiP2pSocketServer(
                        Integer.parseInt(context.getString(R.string.port)));
            } catch (IOException e) {
                Log.d(TAG, e.getMessage());
            }
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Log.d(TAG, "IncomingCommTask waiting for connection");
                    SimWifiP2pSocket sock = mSrvSocket.accept();
                    publishProgress(sock);
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
            new ReceiveCommTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, socket);
        }
    }

    public class ReceiveCommTask extends AsyncTask<SimWifiP2pSocket, String, Void> {
        SimWifiP2pSocket s;

        @Override
        protected void onPreExecute() {
            Log.d(TAG, "Receiving info");
        }

        @Override
        protected Void doInBackground(SimWifiP2pSocket... params) {
            BufferedReader sockIn;
            String st, result = "";

            s = params[0];
            try {
                sockIn = new BufferedReader(new InputStreamReader(s.getInputStream()));

                while ((st = sockIn.readLine()) != null) {
                    Log.d(TAG, "Received: " + st);
                    result = result + "\n" + st;
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
        }
    }

    public class OutgoingCommTask extends AsyncTask<String, Void, String> {
        SimWifiP2pSocket sendSocket;
        String message;

        public OutgoingCommTask(String message){
            this.message = message;
        }

        @Override
        protected void onPreExecute() {
            Log.d(TAG, "Connecting to socket");
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                sendSocket = new SimWifiP2pSocket(params[0],
                        Integer.parseInt(context.getString(R.string.port)));
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
                Log.d(TAG, "Error at sending " + result);
            }
            else {
                try {
                    Log.d(TAG, "Sending: " + message);
                    sendSocket.getOutputStream().write(message.getBytes());
                    sendSocket.close();
                } catch (IOException e) {
                    Log.d(TAG, "Error at sending " + e.getMessage());
                }
            }
        }
    }
}
