package pt.ulisboa.tecnico.cmov.airdesk.WiFiDirect;

import android.app.Application;
import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Messenger;

import java.nio.channels.Channel;

import pt.inesc.termite.wifidirect.SimWifiP2pBroadcast;
import pt.inesc.termite.wifidirect.SimWifiP2pManager;
import pt.inesc.termite.wifidirect.service.SimWifiP2pService;
import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocketManager;

/**
 * Created by hugo__000 on 06/05/2015.
 */
public class WIFIDirect {
    private SimWifiP2pManager mManager = null;
    private Channel mChannel = null;
    private Messenger mService = null;
    private Application app;

    private ServiceConnection mConnection = new ServiceConnection() {
        // callbacks for service binding, passed to bindService()
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            mService = new Messenger(service);
            mManager = new SimWifiP2pManager(mService);
            //mChannel = mManager.initialize(app, getMainLooper(),
                    //null);
            //...
        }
        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mService = null;
            mManager = null;
            mChannel = null;
            //...
        }
    };

    public WIFIDirect(Application app){
        SimWifiP2pSocketManager.Init(app.getApplicationContext());

        //Registar os eventos em que estamos interessados
        IntentFilter filter = new IntentFilter();
        filter.addAction(SimWifiP2pBroadcast.WIFI_P2P_STATE_CHANGED_ACTION);
        filter.addAction(SimWifiP2pBroadcast.WIFI_P2P_PEERS_CHANGED_ACTION);
        filter.addAction(SimWifiP2pBroadcast.WIFI_P2P_NETWORK_MEMBERSHIP_CHANGED_ACTION);
        filter.addAction(SimWifiP2pBroadcast.WIFI_P2P_GROUP_OWNERSHIP_CHANGED_ACTION);
        //SimWifiP2pBroadcastReceiver receiver = new SimWifiP2pBroadcastReceiver(this);
        //registerReceiver(receiver, filter);

        //bind ao servico termite
        Intent intent = new Intent(app.getApplicationContext(), SimWifiP2pService.class);
        //bindService(intent, mConnection, app.getApplicationContext().BIND_AUTO_CREATE);
    }
}
