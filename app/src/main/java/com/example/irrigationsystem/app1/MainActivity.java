package com.example.irrigationsystem.app1;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.irrigationsystem.R;
import com.example.irrigationsystem.muneer.connection.Repository;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;

import static com.example.irrigationsystem.app1.Constants.DEVICE;

public class MainActivity extends AppCompatActivity {
    boolean debug =true;
    private static final int REQUEST_CODE = 123;
    private static final String START = "1";
    private static final String STOP = "0";
    public Button btnOnOff, btnDiscover;
    ListView listView;
    TextView read_msg_box, connectionStatus;
   // EditText writeMsg;

    WifiManager wifiManager;
    WifiP2pManager mManager;
    WifiP2pManager.Channel mChannel;

    BroadcastReceiver mReceiver;
    IntentFilter mIntentFilter;

    List<WifiP2pDevice> peers = new ArrayList<WifiP2pDevice>();
    ArrayList<String> deviceNameArray;
    ArrayList<WifiP2pDevice> deviceArray;
    private boolean running=false;
    Button btnStartMotor;
    private boolean sendingCommand =false;
    private Socket socket;
    private boolean notAdded=true;
    private ArrayAdapter<String> adapter;
    private boolean connected=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initialWork();
        exqListener();
        Repository.getInstance();
    }

    private void exqListener() {
        btnOnOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {// if build version is less than Q try the old traditional method
                    if (!wifiManager.isWifiEnabled()) {
                        wifiManager.setWifiEnabled(true);
                    } else {
                        wifiManager.setWifiEnabled(false);
                    }
                } else {// if it is Android Q and above go for the newer way    NOTE: You can also use this code for less than android Q also
                    Intent panelIntent = new Intent(Settings.Panel.ACTION_WIFI);
                    startActivityForResult(panelIntent, 1);
                }
            }
        });
        btnDiscover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!wifiManager.isWifiEnabled()){
                    Toast.makeText(getBaseContext(),"Please turn on wifi",Toast.LENGTH_LONG).show();
                    return;
                }
                deviceArray.clear();
                deviceNameArray.clear();
                peers.clear();
                if (ActivityCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            REQUEST_CODE);
                    return;
                }
                Log.e("TAG", "onClick: Reached here");
                mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        connectionStatus.setText("Searching for Device");
                    }

                    @Override
                    public void onFailure(int reason) {
                        connectionStatus.setText("Searching Failed");
                    }
                });
                addCustom(5000);
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                try {
                    final WifiP2pDevice device = deviceArray.get(i);
                    WifiP2pConfig config = new WifiP2pConfig();
                    config.deviceAddress = device.deviceAddress;

                    if (ActivityCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    mManager.connect(mChannel, config, new WifiP2pManager.ActionListener() {
                        @Override
                        public void onSuccess() {
                            connectionStatus.setText("Connected to "+device.deviceName);

                        }

                        @Override
                        public void onFailure(int reason) {
                            connectionStatus.setText("Not Connected");
                        }
                    });
                }catch (Exception e){
                    connectionStatus.setText("Connecting...");
                    Repository.getInstance().connect();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(3000);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        connected=true;
                                        connectionStatus.setText("Connected to "+deviceNameArray.get(i));
                                    }
                                });
                            } catch (InterruptedException interruptedException) {
                                interruptedException.printStackTrace();
                            }
                        }
                    }).start();
                }

    }
});
    }

    private void initialWork() {
        btnOnOff = (Button)findViewById(R.id.onOff);
        btnDiscover = (Button)findViewById(R.id.discover);
        btnStartMotor = (Button)findViewById(R.id.btnStart);
        listView = (ListView) findViewById(R.id.peerListView);
        read_msg_box = (TextView) findViewById(R.id.readMsg);
        connectionStatus = (TextView) findViewById(R.id.connectionStatus);

        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this,getMainLooper(),null);

        mReceiver = new WiFiDirectBroadcastReceiver(mManager,mChannel,this);
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        deviceNameArray = new ArrayList<>();
        deviceArray = new ArrayList<>();
        adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1,deviceNameArray);
        listView.setAdapter(adapter);

    }

    WifiP2pManager.PeerListListener peerListListener = new WifiP2pManager.PeerListListener() {
        @Override
        public void onPeersAvailable(WifiP2pDeviceList peerList) {
            if(!peerList.getDeviceList().equals(peers) && peerList.getDeviceList().size()>0){
                peers.clear();
                deviceNameArray.clear();
                deviceArray.clear();
                peers.addAll(peerList.getDeviceList());

                for(WifiP2pDevice device : peerList.getDeviceList()){
                    deviceNameArray.add( device.deviceName);
                    deviceArray.add(device);
                }
            }else if(peers.size()==0){

            }
            adapter.notifyDataSetChanged();
            notAdded=true;
            addCustom(0);

        }
    };

    private void addCustom(int i) {
        if(!debug)return;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(i);
                    if(notAdded)
                    deviceNameArray.add(DEVICE);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyDataSetChanged();
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    WifiP2pManager.ConnectionInfoListener connectionInfoListener = new WifiP2pManager.ConnectionInfoListener() {
        @Override
        public void onConnectionInfoAvailable(WifiP2pInfo wifiP2pInfo) {
            final InetAddress groupOwnerAddress = wifiP2pInfo.groupOwnerAddress;
            if(wifiP2pInfo.groupFormed && wifiP2pInfo.isGroupOwner){
                connectionStatus.setText("Host");
            }
            else if(wifiP2pInfo.groupFormed){
                connectionStatus.setText("Client");
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mReceiver,mIntentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);

    }

    public void start(View view) {
        if(!connected){
            Toast.makeText(this,"Not connected to a suitable device",Toast.LENGTH_LONG).show();
            return;
        }
        if(sendingCommand){
            Toast.makeText(this,"Sending previous command",Toast.LENGTH_LONG).show();
            return;
        }
        if(running){
            read_msg_box.setText("Stopped");
            btnStartMotor.setText("Start");
            sendCommand(STOP);
        }else{
            read_msg_box.setText("Running");
            btnStartMotor.setText("Stop");
            sendCommand(START);
        }

        running=!running;
    }

    private void sendCommand(String command) {
        if(sendingCommand){
            Toast.makeText(this,"Sending previous command",Toast.LENGTH_LONG).show();
            return;
        }
        sendingCommand=true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(Repository.getInstance().send(command)){

                    sendingCommand=false;
                    return;
                }
                socket = null;
                SocketAddress address = new InetSocketAddress("192.168.4.1", 80);

                socket = new Socket();

                try {
                    socket.connect(address,3000);
                    socket.setSoTimeout(3000);
                    OutputStream out = socket.getOutputStream();
                    PrintWriter output=new PrintWriter(out);
                    output.println(command);
                    output.flush();




                    out.close();
                    output.close();
                    socket.close();
                } catch (IOException e) {
                    Log.e("TAG", "run: "+e.getMessage() );
                }finally {

                }


                sendingCommand =false;
            }
        }).start();
    }

    public class ServerClass extends Thread{
        Socket socket;
        ServerSocket serverSocket;

        @Override
        public void run() {
            try {
                serverSocket = new ServerSocket(8888);
                socket = serverSocket.accept();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public class ClientClass extends Thread{
        Socket socket;
        String hostAdd;
        public ClientClass(InetAddress hostAddress){
            hostAdd =hostAddress.getHostAddress();
            socket=new Socket();
        }

        @Override
        public void run() {
            try {
                socket.connect(new InetSocketAddress(hostAdd,8888),500);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}