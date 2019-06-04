package com.gorilla.attendance.enterprise.service;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.gorilla.attendance.enterprise.util.LOG;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

/**
 * Created by ggshao on 2017/4/7.
 */

public class BluetoothLeService extends Service {
    private final static String TAG = "BluetoothLeService";

    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;//蓝牙适配器
    //继承BluetoothProfile，通过BluetoothGatt可以连接设备（connect）,发现服务（discoverServices），并把相应地属性返回到 BluetoothGattCallback
    private BluetoothGatt mBluetoothGatt;


    public final static String ACTION_GATT_CONNECTED =//连接
            "com.example.bluetooth.le.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED =//无连接
            "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED =//连接了但是没有发现服务
            "com.example.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_DATA_AVAILABLE =//可以传输数据了
            "com.example.bluetooth.le.ACTION_DATA_AVAILABLE";
    public final static String EXTRA_DATA =
            "com.example.bluetooth.le.EXTRA_DATA";


    public final static String ACTION_GATT_FAIL =//无连接
            "com.example.bluetooth.le.ACTION_GATT_FAIL";


    //Guo Sheng use bluetooth door
    public final static UUID UUID_NOTIFY =
            UUID.fromString("0000ffe1-0000-1000-8000-00805f9b34fb");
    public final static UUID UUID_SERVICE =
            UUID.fromString("0000ffe0-0000-1000-8000-00805f9b34fb");


    public BluetoothGattCharacteristic mNotifyCharacteristic;
    private Timer timer1 = null;

    TimerTask timerTask = new TimerTask() {

        @Override
        public void run() {
            if(mNotifyCharacteristic != null){
                readCharacteristic(mNotifyCharacteristic);
            }

        }
    };

    public void WriteValue(String strValue) {
        if (mNotifyCharacteristic != null) {
            mNotifyCharacteristic.setValue(strValue.getBytes());
            if(mBluetoothGatt != null){
                mBluetoothGatt.writeCharacteristic(mNotifyCharacteristic);
            }
        }

    }

    public void WriteBytes(byte[] byteData) {
        if (mNotifyCharacteristic != null) {
            mNotifyCharacteristic.setValue(byteData);
            if(mBluetoothGatt != null){
                mBluetoothGatt.writeCharacteristic(mNotifyCharacteristic);
            }
        }

    }
    public void findService(List<BluetoothGattService> gattServices)
    {
        LOG.D(TAG, "Count is:" + gattServices.size());
        for (BluetoothGattService gattService : gattServices)
        {
            LOG.D(TAG, "gattService.getUuid() = " + gattService.getUuid());
            LOG.D(TAG, "UUID_SERVICE.toString() = " + UUID_SERVICE.toString());

            if(gattService.getUuid().toString().equalsIgnoreCase(UUID_SERVICE.toString()))
            {
                LOG.D(TAG, "UUID_SERVICE Same");

                List<BluetoothGattCharacteristic> gattCharacteristics = gattService.getCharacteristics();
                LOG.D(TAG, "UUID_SERVICE Same Count is:" + gattCharacteristics.size());

                for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics)
                {

                    LOG.D(TAG,"gattCharacteristic.getUuid().toString() = " + gattCharacteristic.getUuid().toString());
                    if(gattCharacteristic.getUuid().toString().equalsIgnoreCase(UUID_NOTIFY.toString()))
                    {
                        mNotifyCharacteristic = gattCharacteristic;
                        setCharacteristicNotification(gattCharacteristic, true);
                        broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);
                        return;
                    }
                }
            }
        }
    }

    // Implements callback methods for GATT events that the app cares about.  For example,
    // connection change and services discovered.
    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            String intentAction;
            LOG.D(TAG, "onConnectionStateChange oldStatus=" + status + " NewStates=" + newState);
            if(status == BluetoothGatt.GATT_SUCCESS)
            {
                if (newState == BluetoothProfile.STATE_DISCONNECTED) {

                    intentAction = ACTION_GATT_DISCONNECTED;
                    mBluetoothGatt.close();
                    mBluetoothGatt = null;

                    LOG.D(TAG, "Disconnected from GATT server.");
                    broadcastUpdate(intentAction);
                } else if (newState == BluetoothProfile.STATE_CONNECTED) {

                    intentAction = ACTION_GATT_CONNECTED;

                    broadcastUpdate(intentAction);


                    LOG.D(TAG, "STATE_CONNECTED timer1 = " + timer1);
                    LOG.D(TAG, "STATE_CONNECTED timerTask = " + timerTask);

                    if(timer1 != null){
                        timer1.cancel();
                        boolean isTimeCancelSuccess = timerTask.cancel();
                        LOG.D(TAG, "STATE_CONNECTED isTimeCancelSuccess = " + isTimeCancelSuccess);
                        timer1 = new Timer();
                    }else{
                        timer1 = new Timer();
                    }


                    timerTask = new TimerTask() {
                        @Override
                        public void run() {
                            if(mNotifyCharacteristic != null){
                                readCharacteristic(mNotifyCharacteristic);
                            }
                        }
                    };

                    timer1.schedule(timerTask, 1000, 2000);

                    LOG.D(TAG, "Connected to GATT server.");
                    // 在连接成功之后，尝试去发现服务
                    LOG.D(TAG, "Attempting to start service discovery:" + mBluetoothGatt.discoverServices());

                }

            }else{
                intentAction = ACTION_GATT_FAIL;
                mBluetoothGatt.close();
                mBluetoothGatt = null;

                LOG.D(TAG, "ACTION_GATT_FAIL");
                broadcastUpdate(intentAction);
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            LOG.D(TAG, "onServicesDiscovered status = " + status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                LOG.D(TAG, "onServicesDiscovered received: " + status);
                findService(gatt.getServices());
            } else {
                if(mBluetoothGatt.getDevice().getUuids() == null)
                    LOG.D(TAG, "onServicesDiscovered received: " + status);
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic,
                                         int status) {
            LOG.D(TAG,"onCharacteristicRead status = " + status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic) {
            LOG.D(TAG,"onCharacteristicChanged");
            broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
            LOG.D(TAG, "OnCharacteristicWrite");
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic,
                                          int status)
        {
            LOG.D(TAG, "OnCharacteristicWrite");
        }

        @Override
        public void onDescriptorRead(BluetoothGatt gatt,
                                     BluetoothGattDescriptor bd,
                                     int status) {
            LOG.D(TAG, "onDescriptorRead");
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt,
                                      BluetoothGattDescriptor bd,
                                      int status) {
            LOG.D(TAG, "onDescriptorWrite");
        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int a, int b)
        {
            LOG.D(TAG, "onReadRemoteRssi");
        }

        @Override
        public void onReliableWriteCompleted(BluetoothGatt gatt, int a)
        {
            LOG.D(TAG, "onReliableWriteCompleted");
        }

    };

    private void broadcastUpdate(final String action) {
        final Intent intent = new Intent(action);
        sendBroadcast(intent);
    }

    private void broadcastUpdate(final String action,
                                 final BluetoothGattCharacteristic characteristic) {
        final Intent intent = new Intent(action);

        // This is special handling for the Heart Rate Measurement profile.  Data parsing is
        // carried out as per profile specifications:
        // http://developer.bluetooth.org/gatt/characteristics/Pages/CharacteristicViewer.aspx?u=org.bluetooth.characteristic.heart_rate_measurement.xml
        // For all other profiles, writes the data formatted in HEX.
        final byte[] data = characteristic.getValue();
        if (data != null && data.length > 0) {
            //final StringBuilder stringBuilder = new StringBuilder(data.length);
            //for(byte byteChar : data)
            //    stringBuilder.append(String.format("%02X ", byteChar));
            //intent.putExtra(EXTRA_DATA, new String(data) + "\n" + stringBuilder.toString());
            intent.putExtra(EXTRA_DATA, new String(data));
//            intent.putExtra(EXTRA_DATA, data);
        }
        sendBroadcast(intent);
    }

    public class LocalBinder extends Binder {
        public BluetoothLeService getService() {
            return BluetoothLeService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        // After using a given device, you should make sure that BluetoothGatt.close() is called
        // such that resources are cleaned up properly.  In this particular example, close() is
        // invoked when the UI is disconnected from the Service.
        LOG.D(TAG, "BluetoothService onUnbind");
        close();
        return super.onUnbind(intent);
    }

    private final IBinder mBinder = new LocalBinder();

    /**
     * Initializes a reference to the local Bluetooth adapter.
     *
     * @return Return true if the initialization is successful.
     */
    public boolean initialize() {
        // For API level 18 and above, get a reference to BluetoothAdapter through
        // BluetoothManager.
        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null) {
                LOG.D(TAG, "Unable to initialize BluetoothManager.");
                return false;
            }
        }

        mBluetoothAdapter = mBluetoothManager.getAdapter();
//        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();//这两句没区别
        if (mBluetoothAdapter == null) {
            LOG.D(TAG, "Unable to obtain a BluetoothAdapter.");
            return false;
        }

        return true;
    }

    /**
     * Connects to the GATT server hosted on the Bluetooth LE device.
     *
     * @param address The device address of the destination device.
     *
     * @return Return true if the connection is initiated successfully. The connection result
     *         is reported asynchronously through the
     *         {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     *         callback.
     */
    public boolean connect(final String address) {
        initialize();
        if (mBluetoothAdapter == null || address == null) {
            LOG.D(TAG, "BluetoothAdapter not initialized or unspecified address.");
            return false;
        }
/*
        // Previously connected device.  Try to reconnect.
        if (mBluetoothDeviceAddress != null && address.equals(mBluetoothDeviceAddress)
                && mBluetoothGatt != null) {
            LOG.D(TAG, "Trying to use an existing mBluetoothGatt for connection.");
            if (mBluetoothGatt.connect()) {
                mConnectionState = STATE_CONNECTING;
                return true;
            } else {
                return false;
            }
        }
*/
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        if (device == null) {

            LOG.D(TAG, "Device not found.  Unable to connect.");
            return false;
        }
        // We want to directly connect to the device, so we are setting the autoConnect
        // parameter to false.
        if(mBluetoothGatt != null)
        {
            mBluetoothGatt.close();
            mBluetoothGatt = null;
        }
        mBluetoothGatt = device.connectGatt(this, false, mGattCallback);
        //mBluetoothGatt.connect();

        LOG.D(TAG, "Trying to create a new connection.");
        return true;
    }

    /**
     * Disconnects an existing connection or cancel a pending connection. The disconnection result
     * is reported asynchronously through the
     * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     * callback.
     */
    public void disconnect() {
        LOG.D(TAG,"disconnect");
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            LOG.D(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.disconnect();
    }

    /**
     * After using a given BLE device, the app must call this method to ensure resources are
     * released properly.
     */
    public void close() {
        if (mBluetoothGatt == null) {
            return;
        }
        mBluetoothGatt.close();
        mBluetoothGatt = null;
    }


    /**
     * Request a read on a given {@code BluetoothGattCharacteristic}. The read result is reported
     * asynchronously through the {@code BluetoothGattCallback#onCharacteristicRead(android.bluetooth.BluetoothGatt, android.bluetooth.BluetoothGattCharacteristic, int)}
     * callback.
     *
     * @param characteristic The characteristic to read from.
     */
    public void readCharacteristic(BluetoothGattCharacteristic characteristic) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            LOG.D(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.readCharacteristic(characteristic);
    }

    /**
     * Enables or disables notification on a give characteristic.
     *
     * @param characteristic Characteristic to act on.
     * @param enabled If true, enable notification.  False otherwise.
     */
    public void setCharacteristicNotification(BluetoothGattCharacteristic characteristic,
                                              boolean enabled) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            LOG.D(TAG, "setCharacteristicNotification BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);
/*
        // This is specific to Heart Rate Measurement.
        if (UUID_HEART_RATE_MEASUREMENT.equals(characteristic.getUuid())) {
            BluetoothGattDescriptor descriptor = characteristic.getDescriptor(
                    UUID.fromString(SampleGattAttributes.CLIENT_CHARACTERISTIC_CONFIG));
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            mBluetoothGatt.writeDescriptor(descriptor);
        }
        */
    }

    /**
     * Retrieves a list of supported GATT services on the connected device. This should be
     * invoked only after {@code BluetoothGatt#discoverServices()} completes successfully.
     *
     * @return A {@code List} of supported services.
     */
    public List<BluetoothGattService> getSupportedGattServices() {
        if (mBluetoothGatt == null) return null;

        return mBluetoothGatt.getServices();
    }
}
