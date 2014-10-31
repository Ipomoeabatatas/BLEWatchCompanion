package sg.android.tpk.blewatchcompanion;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;


public class SetWatchActivity extends Activity implements SeekBar.OnSeekBarChangeListener {

 //   private static final String TAG = "SetWatchActivity";
    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";
    private final static String TAG = SetWatchActivity.class.getSimpleName();



    private TextView tvConnectState, tvDeviceAddress;
    private SeekBar bar;
    private TextView textAge, textMaxHeartRate;
    private TextView tvMinVeryLight, tvMaxVeryLight, tvMinLight, tvMaxLight, tvMinModerate, tvMaxModerate;
    private TextView tvMinMaximal, tvMaxMaximal, tvMinVigorous, tvMaxVigorous;
    private Button bnSetWatch;

    private int maxHR;

    private int minVeryLightHR, maxVeryLightHR;
    private int minLightHR, maxLightHR;

    private int minModerateHR, maxModerateHR;

    private int minVigorousHR, maxVigorousHR;

    private int minMaximalHR, maxMaximalHR;

    private String strDeviceName, strDeviceAddress;
    private BluetoothAdapter mBluetoothAdapter;
    private SparseArray<BluetoothDevice> mDevices;
    private BluetoothGatt mConnectedGatt;
    private ProgressDialog mProgress;

    private BluetoothLeService mBluetoothLeService;

    private boolean mConnected = false;


    // Code to manage Service lifecycle.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }
            // Automatically connects to the device upon successful start-up initialization.
            mBluetoothLeService.connect(strDeviceAddress);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };

    // Handles various events fired by the Service.
    // ACTION_GATT_CONNECTED: connected to a GATT server.
    // ACTION_GATT_DISCONNECTED: disconnected from a GATT server.
    // ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.
    // ACTION_DATA_AVAILABLE: received data from the device.  This can be a result of read
    //                        or notification operations.

    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                mConnected = true;
              //  Toast.makeText(this,"connection success", Toast.LENGTH_SHORT).show();
                updateConnectionState(R.string.connected);
                invalidateOptionsMenu();
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                mConnected = false;
                updateConnectionState(R.string.disconnected);
                invalidateOptionsMenu();
            //    clearUI();
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                // Show all the supported services and characteristics on the user interface.
            //    displayGattServices(mBluetoothLeService.getSupportedGattServices());
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
            //    displayData(intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
            } else if (BluetoothLeService.ACTION_CHARACTERISTIC_WRITE.equals(action)) {
                //    displayData(intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
            }
        }
    };


    // Need to have something to save the Seek Bar value into perm storage for future use
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setwatch);

        final Intent intent = getIntent();
        strDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
        strDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);
        setProgressBarIndeterminate(true);

        bar = (SeekBar) findViewById(R.id.sb_Age); // make age seekbar object
        bar.setOnSeekBarChangeListener(this); // set seekbar listener
        getActionBar().setTitle(R.string.app_name);
        textAge = (TextView) findViewById(R.id.tv_Age);
        textMaxHeartRate = (TextView) findViewById(R.id.tv_CalcHeartRate);


        tvMinVeryLight = (TextView) findViewById(R.id.tv_MinVeryLight);
        tvMaxVeryLight = (TextView) findViewById(R.id.tv_MaxVeryLight);

        tvMinLight = (TextView) findViewById(R.id.tv_MinLight);
        tvMaxLight = (TextView) findViewById(R.id.tv_MaxLight);

        tvMinModerate = (TextView) findViewById(R.id.tv_MinModerate);
        tvMaxModerate = (TextView) findViewById(R.id.tv_MaxModerate);

        tvMinVigorous = (TextView) findViewById(R.id.tv_MinVigorous);
        tvMaxVigorous = (TextView) findViewById(R.id.tv_MaxVigorous);

        tvMinMaximal = (TextView) findViewById(R.id.tv_MinMaximal);
        tvMaxMaximal = (TextView) findViewById(R.id.tv_MaxMaximal);
        bnSetWatch = (Button) findViewById(R.id.bn_SetWatch);

        tvConnectState = (TextView) findViewById(R.id.tv_ConnectState);
        tvDeviceAddress = (TextView) findViewById(R.id.tv_DeviceAddress);


        // need to pick up EXTRAS_DEVICE_NAME FORM THE INTENT
        Toast.makeText(this, strDeviceName, Toast.LENGTH_SHORT).show();
        Toast.makeText(this, strDeviceAddress, Toast.LENGTH_SHORT).show();


                /*
         * Bluetooth in Android 4.3 is accessed via the BluetoothManager, rather than
         * the old static BluetoothAdapter.getInstance()
         */

        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);

        BluetoothManager manager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);


        mBluetoothAdapter = manager.getAdapter();

        mDevices = new SparseArray<BluetoothDevice>();

        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            //Bluetooth is disabled
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(enableBtIntent);
            finish();
            return;
        }


        bnSetWatch.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Do something in response to button click
                Context context = getApplicationContext();
                CharSequence text = "You pressed update watch!";
                int duration = Toast.LENGTH_SHORT;
                Toast.makeText(context, text, duration).show();
// to call the methods or services to
// connect , update and get status of BLE device

                mBluetoothLeService.writeCharacteristic(strDeviceAddress);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.gatt_services, menu);
        if (mConnected) {
            menu.findItem(R.id.menu_connect).setVisible(false);
            menu.findItem(R.id.menu_disconnect).setVisible(true);
        } else {
            menu.findItem(R.id.menu_connect).setVisible(true);
            menu.findItem(R.id.menu_disconnect).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.menu_connect:
                Toast.makeText(this, "before clicking connect", Toast.LENGTH_SHORT).show();
                mBluetoothLeService.connect(strDeviceAddress);
                Toast.makeText(this, "after clicking connect"+strDeviceAddress, Toast.LENGTH_SHORT).show();
                return true;
            case R.id.menu_disconnect:
                mBluetoothLeService.disconnect();
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void updateConnectionState(final int resourceId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                tvConnectState.setText(resourceId);


            }
        });
    }

    private void displayData(String data) {
       // if (data != null) {
       //     tvDataField.setText(data);
       // }
    }
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress,
                                  boolean fromUser) {
        // TODO Auto-generated method stub

        // change progress text label with current seekbar value
        textAge.setText("Your age is: " + progress);
        maxHR = 220 - progress;
        textMaxHeartRate.setText("" + maxHR);
        // change action text label to changing
        // textAction.setText("changing");

        minVeryLightHR = (int) Math.ceil(0.2 * maxHR);
        maxVeryLightHR = (int) Math.floor(0.57 * maxHR);

        minLightHR = (int) Math.ceil(0.57 * maxHR);
        maxLightHR = (int) Math.floor(0.63 * maxHR);

        minModerateHR = (int) Math.ceil(0.63 * maxHR);
        maxModerateHR = (int) Math.floor(0.76 * maxHR);

        minVigorousHR = (int) Math.ceil(0.76 * maxHR);
        maxVigorousHR = (int) Math.floor(0.95 * maxHR);

        minMaximalHR = (int) Math.ceil(0.95 * maxHR);
        maxMaximalHR = (int) Math.floor(1.00 * maxHR);

        tvMinVeryLight.setText("" + minVeryLightHR);
        tvMaxVeryLight.setText("" + maxVeryLightHR);

        tvMinLight.setText("" + minLightHR);
        tvMaxLight.setText("" + maxLightHR);

        tvMinModerate.setText("" + minModerateHR);
        tvMaxModerate.setText("" + maxModerateHR);

        tvMinVigorous.setText("" + minVigorousHR);
        tvMaxVigorous.setText("" + maxVigorousHR);

        tvMinMaximal.setText("" + minMaximalHR);
        tvMaxMaximal.setText("" + maxMaximalHR);

    }


    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // TODO Auto-generated method stub
        textAge.setText("starting to track touch");

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        // TODO Auto-generated method stub
        seekBar.setSecondaryProgress(seekBar.getProgress());
        // textAction.setText("ended tracking touch");
    }


    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        if (mBluetoothLeService != null) {
            final boolean result = mBluetoothLeService.connect(strDeviceAddress);
            Log.d(TAG, "Connect request result=" + result);
        }
    }

    protected void onPause() {
        super.onPause();
        unregisterReceiver(mGattUpdateReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConnection);
        mBluetoothLeService = null;
    }
/*
    private void startScan() {
        Log.i(TAG, "Start method startScan");
        mBluetoothAdapter.startLeScan(this);
        setProgressBarIndeterminateVisibility(true);

        mHandler.postDelayed(mStopRunnable, 2500);
    }

    private void stopScan() {
        mBluetoothAdapter.stopLeScan(this);
        setProgressBarIndeterminateVisibility(false);
        Log.i(TAG, "Stop method startScan");
*/

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }}