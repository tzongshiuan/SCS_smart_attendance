package com.gorilla.attendance.enterprise.fragment;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.gorilla.attendance.enterprise.MainActivity;
import com.gorilla.attendance.enterprise.R;
import com.gorilla.attendance.enterprise.util.Constants;
import com.gorilla.attendance.enterprise.util.DeviceUtils;
import com.gorilla.attendance.enterprise.util.EnterpriseUtils;
import com.gorilla.attendance.enterprise.util.LOG;
import com.gorilla.attendance.enterprise.util.NetworkManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by ggshao on 2017/4/6.
 */

public class ConfigureSettingBluetoothFragment extends BaseFragment {
    public static final String TAG = "ConfigureSettingBluetoothFragment";

    private View mView = null;

    private MainActivity mMainActivity;
    private FragmentActivity mActivity = null;
    private Context mContext = null;

    private NetworkManager mNetworkManager = null;
    private Handler mActivityHandler = null;

    private EditText mEdtConfigureAccount = null;
    private EditText mEdtConfigurePassword = null;

    private Button mBtnDone = null;
    private Button mBtnCancel = null;
    private Button mBtnScan = null;

    private Button mBtnOpenDoor1 = null;
    private Button mBtnOpenDoor2 = null;

    private LeDeviceListAdapter mLeDeviceListAdapter = null;
    private BluetoothAdapter mBluetoothAdapter;
    private ListView mBluetoothDeviceListView = null;

    private BluetoothLeScanner mBluetoothLeScanner = null;

    private boolean mScanning;
    private Handler mHandler = null;
    private static final int REQUEST_ENABLE_BT = 1;
    private static final long SCAN_PERIOD = 10000;

    private ProgressBar mProgress = null;
    private TextView mBluetoothState = null;

    private EditText mEdtBluetoothCloseDoorSeconds = null;
    private EditText mEdtBluetoothPassword = null;


    private SharedPreferences mSharedPreference = null;

    private static int mDoor1Flag = 0;
    private static int mDoor2Flag = 0;


    private Timer mTimerDoor1 = null;
    private Timer mTimerDoor2 = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        LOG.D(TAG,"onCreate");
        super.onCreate(savedInstanceState);
        mActivity = getActivity();
        mMainActivity = (MainActivity) getActivity();
        mContext = getActivity();

        mActivityHandler = mMainActivity.getHandler();
        BluetoothManager bluetoothManager = (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        mBluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();

        mSharedPreference = mContext.getSharedPreferences(Constants.PREF_NAME_ATTENDANCE_ENTERPRISE, Context.MODE_PRIVATE);
        mLeDeviceListAdapter = new LeDeviceListAdapter(mMainActivity);
        mHandler = new Handler();

        LOG.D(TAG,"onCreate mBluetoothAdapter = " + mBluetoothAdapter);

        if(getFragmentManager().getBackStackEntryCount() > 0){
            mActivityHandler.removeMessages(Constants.LAUNCH_VIDEO);
            mActivityHandler.removeMessages(Constants.BACK_TO_INDEX_PAGE);
            mActivityHandler.sendEmptyMessageDelayed(Constants.BACK_TO_INDEX_PAGE, DeviceUtils.SETTING_DETAIL_DELAYED_TIME);
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LOG.V(TAG, "[onCreateView] ");

        if (mView == null) {
            mView = inflater.inflate(R.layout.configure_setting_bluetooth_fragment, null);
        } else {
            ViewGroup parent = (ViewGroup) mView.getParent();
            if (parent != null) {
                parent.removeView(mView);
            }
        }

//        initView(mView);

        initView();
        return mView;
    }

    @Override
    public void onPause() {
        LOG.D(TAG,"onPause");
        super.onPause();

        String bluetoothDoorPassword = mSharedPreference.getString(Constants.PREF_KEY_BLUETOOTH_DOOR_PASSWORD, null);

        if(mTimerDoor1 != null){
            mTimerDoor1.cancel();
            EnterpriseUtils.closeDoorOne(bluetoothDoorPassword);
        }

        if(mTimerDoor2 != null){
            mTimerDoor2.cancel();
            EnterpriseUtils.closeDoorTwo(bluetoothDoorPassword);
        }


    }

    @Override
    public void onResume(){
        LOG.D(TAG,"onResume getFragmentManager().getBackStackEntryCount() = " + getFragmentManager().getBackStackEntryCount());
        super.onResume();

        if (!mBluetoothAdapter.isEnabled()) {
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }

        String bluetoothDoorPassword = mSharedPreference.getString(Constants.PREF_KEY_BLUETOOTH_DOOR_PASSWORD, null);
        if(bluetoothDoorPassword != null){
            mEdtBluetoothPassword.setText(bluetoothDoorPassword);
        }

        int bluetoothDoorIdleSeconds = mSharedPreference.getInt(Constants.PREF_KEY_BLUETOOTH_DOOR_IDEL_SECONDS, 5);
        if(bluetoothDoorIdleSeconds != -1){
            mEdtBluetoothCloseDoorSeconds.setText(String.valueOf(bluetoothDoorIdleSeconds));
        }

        if(DeviceUtils.mIsBTConnected){
            mBluetoothState.setText(getString(R.string.txt_bluetooth_state_success));
        }else{
            mBluetoothState.setText(getString(R.string.txt_bluetooth_state));
        }



        LOG.D(TAG,"onResume bluetoothDoorPassword = " + bluetoothDoorPassword);
        LOG.D(TAG,"onResume bluetoothDoorIdleSeconds = " + bluetoothDoorIdleSeconds);

    }

    @Override
    public void onStop() {
        LOG.D(TAG,"onStop");
        super.onStop();
    }

    @Override
    public void onDestroy() {
        LOG.D(TAG,"onDestroy");
        super.onDestroy();

        mBtnOpenDoor1 = null;
        mBtnOpenDoor2 = null;
    }


    private void initView(){
//        mEdtConfigureAccount = (EditText) mView.findViewById(R.id.edt_conf_login_account);
//        mEdtConfigurePassword = (EditText) mView.findViewById(R.id.edt_conf_login_password);

        mBtnDone = (Button) mView.findViewById(R.id.btn_conf_done);
        mBtnCancel = (Button) mView.findViewById(R.id.btn_conf_cancel);
        mBtnScan = (Button) mView.findViewById(R.id.btn_conf_scan);

        mEdtBluetoothCloseDoorSeconds = (EditText) mView.findViewById(R.id.edt_bluetooth_close_door_seconds);
        mEdtBluetoothPassword = (EditText) mView.findViewById(R.id.edt_bluetooth_password);


        mBtnOpenDoor1 = (Button) mView.findViewById(R.id.btn_open_door1);
        mBtnOpenDoor2 = (Button) mView.findViewById(R.id.btn_open_door2);

        mProgress = (ProgressBar) mView.findViewById(R.id.marker_progress);
        mBluetoothState = (TextView) mView.findViewById(R.id.txt_bluetooth_state);

        mBluetoothDeviceListView = (ListView) mView.findViewById(R.id.list_bluetooth);
        mBluetoothDeviceListView.setAdapter(mLeDeviceListAdapter);
        mBluetoothDeviceListView.setOnItemClickListener(mBluetoothDeviceListViewOnItemClickListener);

        mBtnDone.setOnClickListener(mBtnDoneClickListener);
        mBtnCancel.setOnClickListener(mBtnCancelClickListener);
        mBtnScan.setOnClickListener(mBtnScanClickListener);

        mBtnOpenDoor1.setOnClickListener(mBtnOpenDoor1ClickListener);
        mBtnOpenDoor2.setOnClickListener(mBtnOpenDoor2ClickListener);



    }

    private Button.OnClickListener mBtnDoneClickListener = new Button.OnClickListener() {
        public void onClick(View v) {
//            launchFragment(Constants.FRAGMENT_TAG_CONF_END_POINT_SETTING, false);

            if(mEdtBluetoothCloseDoorSeconds.getText().toString().isEmpty()){
                Toast.makeText(mContext, "time can't be null", Toast.LENGTH_SHORT).show();
                return;
            }

            mSharedPreference.edit().putInt(Constants.PREF_KEY_BLUETOOTH_DOOR_IDEL_SECONDS, Integer.parseInt(mEdtBluetoothCloseDoorSeconds.getText().toString())).apply();
            mSharedPreference.edit().putString(Constants.PREF_KEY_BLUETOOTH_DOOR_PASSWORD, mEdtBluetoothPassword.getText().toString()).apply();

            Toast.makeText(mContext, "Save Success", Toast.LENGTH_SHORT).show();

//            mMainActivity.onBackPressed();
        }
    };

    private Button.OnClickListener mBtnCancelClickListener = new Button.OnClickListener() {
        public void onClick(View v) {
//            mActivityHandler.removeMessages(Constants.BACK_TO_INDEX_PAGE);
//            mActivityHandler.sendEmptyMessage(Constants.BACK_TO_INDEX_PAGE);

            mMainActivity.onBackPressed();

        }
    };

    private Button.OnClickListener mBtnScanClickListener = new Button.OnClickListener() {
        public void onClick(View v) {

            mLeDeviceListAdapter.clear();
            scanLeDevice(true);

        }
    };

    private Button.OnClickListener mBtnOpenDoor1ClickListener = new Button.OnClickListener() {
        public void onClick(View v) {
            LOG.D(TAG,"mBtnOpenDoor1ClickListener DeviceUtils.mIsBTConnected = " + DeviceUtils.mIsBTConnected);

            if(!DeviceUtils.mIsBTConnected){
//                Toast.makeText(mContext, "蓝牙没有连接，请先检查设备", Toast.LENGTH_SHORT).show();
                Toast.makeText(mContext, getString(R.string.txt_no_connect_bluetooth_door), Toast.LENGTH_SHORT).show();
                return;
            }

            String bluetoothDoorPassword = mSharedPreference.getString(Constants.PREF_KEY_BLUETOOTH_DOOR_PASSWORD, null);


            if(mDoor1Flag == 0)
            {
                mBtnOpenDoor1.setText(getString(R.string.txt_close_door_1));
                EnterpriseUtils.openDoorOne(bluetoothDoorPassword, mContext);
                mDoor1Flag = 1;

                int bluetoothDoorIdleSeconds = mSharedPreference.getInt(Constants.PREF_KEY_BLUETOOTH_DOOR_IDEL_SECONDS, 5);

//                Timer timer = new Timer();
                mTimerDoor1 = new Timer();
                mTimerDoor1.schedule(new TimerTask() {

                    @Override
                    public void run() {
                        Message msg = new Message();
                        msg.what = 1;

                        mDoorHandler.sendMessage(msg);
                    }
                }, bluetoothDoorIdleSeconds * 1000);




            }else{
                mBtnOpenDoor1.setText(getString(R.string.txt_open_door_1));
                EnterpriseUtils.closeDoorOne(bluetoothDoorPassword);
                mDoor1Flag = 0;
            }




        }
    };

    private Button.OnClickListener mBtnOpenDoor2ClickListener = new Button.OnClickListener() {
        public void onClick(View v) {

            LOG.D(TAG,"mBtnOpenDoor1ClickListener DeviceUtils.mIsBTConnected = " + DeviceUtils.mIsBTConnected);

            if(!DeviceUtils.mIsBTConnected){
//                Toast.makeText(mContext, "蓝牙没有连接，请先检查设备", Toast.LENGTH_SHORT).show();
                Toast.makeText(mContext, getString(R.string.txt_no_connect_bluetooth_door), Toast.LENGTH_SHORT).show();
                return;
            }

            String bluetoothDoorPassword = mSharedPreference.getString(Constants.PREF_KEY_BLUETOOTH_DOOR_PASSWORD, null);


            if(mDoor2Flag == 0)
            {
                mBtnOpenDoor2.setText(getString(R.string.txt_close_door_2));
                EnterpriseUtils.openDoorTwo(bluetoothDoorPassword);
                mDoor2Flag = 1;

                int bluetoothDoorIdleSeconds = mSharedPreference.getInt(Constants.PREF_KEY_BLUETOOTH_DOOR_IDEL_SECONDS, 5);

                mTimerDoor2 = new Timer();
                mTimerDoor2.schedule(new TimerTask() {

                    @Override
                    public void run() {
                        Message msg = new Message();
                        msg.what = 2;

                        mDoorHandler.sendMessage(msg);
                    }
                }, bluetoothDoorIdleSeconds * 1000);




            }else{
                mBtnOpenDoor2.setText(getString(R.string.txt_open_door_2));
                EnterpriseUtils.closeDoorTwo(bluetoothDoorPassword);
                mDoor2Flag = 0;
            }



        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // User chose not to enable Bluetooth.
        LOG.D(TAG,"onActivityResult requestCode = " + requestCode);
        LOG.D(TAG,"onActivityResult resultCode = " + resultCode);
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
//            finish();
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private GridView.OnItemClickListener mBluetoothDeviceListViewOnItemClickListener = new GridView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            LOG.D(TAG, "mOnItemClickListener position = " + position);

            mBluetoothState.setText(getString(R.string.txt_bluetooth_state_connect));

            BluetoothDevice device = mLeDeviceListAdapter.getDevice(position);
            LOG.D(TAG, "mOnItemClickListener device = " + device);
            if (device == null) {
                return;
            }

            LOG.D(TAG, "mOnItemClickListener DeviceUtils.mBluetoothLeService = " + DeviceUtils.mBluetoothLeService);
            if(DeviceUtils.mBluetoothLeService != null){
                LOG.D(TAG, "mOnItemClickListener device 0000" );
                mMainActivity.bluetoothDoorDisConnect();
            }

            LOG.D(TAG, "mOnItemClickListener device 1111 mScanning = " + mScanning );

            if (mScanning) {
                mBluetoothAdapter.stopLeScan(mLeScanCallback);
                mScanning = false;
            }

            //connect
            mMainActivity.bluetoothDoorConnect(device.getAddress());



        }
    };

    private void scanLeDevice(boolean enable){
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(mScanning)
                    {
                        //10 sec
                        mScanning = false;
                        mBluetoothAdapter.stopLeScan(mLeScanCallback);
//                		invalidateOptionsMenu();

//                        mBluetoothLeScanner.stopScan(scanCallback);
                    }
                }
            }, SCAN_PERIOD);


            mScanning = true;
            //F000E0FF-0451-4000-B000-000000000000
            boolean isStartLeScan = mBluetoothAdapter.startLeScan(mLeScanCallback);

//            LOG.D(TAG,"isStartLeScan = " + isStartLeScan);
//            mBluetoothAdapter.startDiscovery();
//            LOG.D(TAG,"mBluetoothLeScanner GO");
//            mBluetoothLeScanner.startScan(scanCallback);

        } else {
            //停止
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
    }

    // Device scan callback.
    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {

                public void onLeScan(final BluetoothDevice device, final int rssi, final byte[] scanRecord) {
                    mActivity.runOnUiThread(new Runnable() {
                        public void run() {

                            LOG.D(TAG, "device = " + device);
                            LOG.D(TAG, "device.getName() = " + device.getName());
                            LOG.D(TAG, "device.getAddress() = " + device.getAddress());
                            LOG.D(TAG, "device.getType() = " + device.getType());
                            LOG.D(TAG, "rssi = " + rssi);
                            LOG.D(TAG, "scanRecord.length = " + scanRecord.length);
                            LOG.D(TAG, "scanRecord.length = " + scanRecord.length);

                            mLeDeviceListAdapter.addDevice(device, rssi, scanRecord);
//                	System.out.println("扫描页面得到的UUID"+device.getUuids());
                            mLeDeviceListAdapter.notifyDataSetChanged();
                        }
                    });
                }
            };

    private ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            LOG.D(TAG, "onScanResult result = " + result);
            LOG.D(TAG, "onScanResult result.getDevice().getName() = " + result.getDevice().getName());
            LOG.D(TAG, "onScanResult result.getDevice().getAddress() = " + result.getDevice().getAddress());
            LOG.D(TAG, "onScanResult result.getDevice().getType() = " + result.getDevice().getType());
//            LOG.D(TAG, "onScanResult result.getDevice().getName() = " + result.getDevice().getName());
            LOG.D(TAG, "onScanResult callbackType = " + callbackType);

        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);
            LOG.D(TAG, "onBatchScanResults results = " + results);
            LOG.D(TAG, "onBatchScanResults results.size() = " + results.size());
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
            LOG.D(TAG, "onScanFailed errorCode = " + errorCode);
        }
    };

    // Adapter for holding devices found through scanning.
    private class LeDeviceListAdapter extends BaseAdapter {
        private LayoutInflater mInflater = null;
        private ArrayList<BluetoothDevice> mLeDevices;
        private ArrayList<Integer> rssis;
        private ArrayList<byte[]> bRecord;
        private LayoutInflater mInflator;
        private MainActivity mMainActivity;

        public LeDeviceListAdapter(MainActivity activity) {
            super();
            mLeDevices = new ArrayList<BluetoothDevice>();
            rssis = new ArrayList<Integer>();
            bRecord = new ArrayList<byte[]>();

            mMainActivity = activity;
            mInflater = (LayoutInflater) mMainActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public void addDevice(BluetoothDevice device, int rs, byte[] record) {
            if(!mLeDevices.contains(device)) {
                mLeDevices.add(device);
                rssis.add(rs);
                bRecord.add(record);
            }
        }

        public BluetoothDevice getDevice(int position) {
            return mLeDevices.get(position);
        }

        public void clear() {
            mLeDevices.clear();
            rssis.clear();
            bRecord.clear();
        }

        @Override
        public int getCount() {
            return mLeDevices.size();
        }

        @Override
        public Object getItem(int i) {
            return mLeDevices.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null) {
                convertView = mInflater.inflate(R.layout.list_item_bluetooth_device, parent, false);
            }


            TextView txtTitle = (TextView) convertView.findViewById(R.id.txt_bluetooth_device_name);
            TextView txtAddress = (TextView) convertView.findViewById(R.id.txt_bluetooth_address);

            BluetoothDevice device = mLeDevices.get(position);

            String deviceName = null;
            if(device.getName() == null){
                deviceName = "Unknown";
            }else{
                deviceName = device.getName();
            }

            txtTitle.setText(deviceName);
            txtAddress.setText(device.getAddress());


            return convertView;
        }
    }


    public void setBluetoothState(String bluetoothState){
        mBluetoothState.setText(bluetoothState);
    }



    private Handler mDoorHandler = new Handler(){
        public void handleMessage(Message msg) {
            String bluetoothDoorPassword = mSharedPreference.getString(Constants.PREF_KEY_BLUETOOTH_DOOR_PASSWORD, null);
            switch(msg.what){
                case 1:
                    //1路关
                    mDoor1Flag = 0;
                    if(mBtnOpenDoor1 != null){
                        mBtnOpenDoor1.setText(getString(R.string.txt_open_door_1));
                    }

                    EnterpriseUtils.closeDoorOne(bluetoothDoorPassword);
                    mTimerDoor1 = null;

                    break;
                case 2:
                    //2路关
                    mDoor1Flag = 0;
                    if(mBtnOpenDoor2 != null){
                        mBtnOpenDoor2.setText(getString(R.string.txt_open_door_2));
                    }

                    EnterpriseUtils.closeDoorTwo(bluetoothDoorPassword);
                    mTimerDoor2 = null;
                    break;
            }
        };
    };




}
