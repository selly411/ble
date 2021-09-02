package com.superdroid.test.zzafire;


import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class find_wd extends AppCompatActivity {
    private final static String TAG = find_wd.class.getSimpleName();

    ListView listView;//리스트뷰 객체
    BleList bleList = null;//리스트 어댑터
    private BluetoothAdapter mBluetoothAdapter;
    private boolean mScanning;
    private Handler mHandler;
    private BluetoothLeService mBluetoothLeService;
    private ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics =
            new ArrayList<ArrayList<BluetoothGattCharacteristic>>();
    private boolean mConnected = false;
    private BluetoothGattCharacteristic mNotifyCharacteristic;


    private ExpandableListView deviceService;


    private final String LIST_NAME = "NAME";
    private final String LIST_UUID = "UUID";



    buttonInfo buttonInfo;

    private static final int REQUEST_ENABLE_BT = 1;
    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 10000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_wd);

        //리스트뷰 설정
        bleList = new BleList();
        listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(bleList);
        mHandler = new Handler();


        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "ble지원이 되지 않는 기기입니다.", Toast.LENGTH_SHORT).show();
            finish();
        }
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "bluetooth가 지원이 되지 않는 기기입니다.", Toast.LENGTH_SHORT).show();
            finish();
            return;
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

    public void mOnClick_update_device(View v) {
        scanLeDevice(true);
    }

    private void scanLeDevice(final boolean enable) {
        if (enable) {
            bleList.clear();
            bleList.notifyDataSetChanged();
            mBluetoothAdapter.startLeScan(mLeScanCallback);
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    Toast.makeText(find_wd.this, "기기검색을 완료했습니다.", Toast.LENGTH_SHORT).show();
                }
            }, SCAN_PERIOD);
            mScanning = true;

        } else {
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
            bleList.clear();
            bleList.notifyDataSetChanged();
        }
    }

    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {

                @Override
                public void onLeScan(final BluetoothDevice device, final int rssi, byte[] scanRecord) {

                            String connect ="비연결";
                            bleList.addDevice(device,rssi,connect);
                            bleList.notifyDataSetChanged();

                }
            };

    private class BleList extends BaseAdapter {
        private ArrayList<BluetoothDevice> devices;
        private ArrayList<BluetoothDevice> addresss;
        private ArrayList<Integer> RSSIs;
        private ArrayList<String> connects;
        private LayoutInflater inflater;


        public BleList() {
            super();
            devices = new ArrayList<BluetoothDevice>();
            addresss = new ArrayList<BluetoothDevice>();
            RSSIs = new ArrayList<Integer>();
            connects = new ArrayList<String>();
            inflater = ((Activity) find_wd.this).getLayoutInflater();
        }

        public void addDevice(BluetoothDevice device, int rssi, String connect) {
            if(!devices.contains(device)){
                devices.add(device);
                addresss.add(device);
                RSSIs.add(rssi);
                connects.add(connect);
            }
            else{
                RSSIs.set(devices.indexOf(device),rssi);
            }
        }

        public void clear() {
            devices.clear();
            addresss.clear();
            RSSIs.clear();
            connects.clear();
        }

        @Override
        public int getCount() {
            return devices.size();
        }

        @Override
        public Object getItem(int position) {
            return devices.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;

            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = inflater.inflate(R.layout.find_wd_listview, null);
                viewHolder.deviceName = (TextView) convertView.findViewById(R.id.text_name);
                viewHolder.deviceAddress = (TextView) convertView.findViewById(R.id.text_address);
                viewHolder.deviceRssi = (TextView) convertView.findViewById(R.id.text_rssi);
                viewHolder.deviceConnect = (TextView) convertView.findViewById(R.id.text_connect);
                viewHolder.deviceButton = (Button)convertView.findViewById(R.id.btn_connect);
                viewHolder.deviceData=(TextView)convertView.findViewById(R.id.data_value);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            String deviceName = devices.get(position).getName();
            String deviceAddress = addresss.get(position).getAddress();
            int rssi = RSSIs.get(position);
            String connect = connects.get(position);

            viewHolder.deviceName.setText(deviceName != null && deviceName.length() > 0 ?deviceName:"알 수 없는 장치");
            viewHolder.deviceAddress.setText(deviceAddress);
            viewHolder.deviceRssi.setText(String.valueOf(rssi));
            viewHolder.deviceConnect.setText(connect);

            buttonInfo buttonInfo = new buttonInfo();
            buttonInfo.position=position;
            buttonInfo.viewHolder=viewHolder;
            viewHolder.deviceButton.setTag(buttonInfo);
            viewHolder.deviceButton.setOnClickListener(btnClickListener);

            return convertView;
        }
    }
    private View.OnClickListener btnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            buttonInfo =(buttonInfo) v.getTag();
            switch (v.getId()) {

                // 버튼 클릭
                case R.id.btn_connect:
                {

                    Intent gattServiceIntent = new Intent(find_wd.this, BluetoothLeService.class);
                    bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);

                    registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
                    if (mBluetoothLeService != null) {
                        final boolean result = mBluetoothLeService.connect(buttonInfo.viewHolder.deviceAddress.getText().toString());
                        Log.d(TAG, "Connect request result=" + result);
                    }

                }
                break;
            }
        }
    };
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }
            // Automatically connects to the device upon successful start-up initialization.
            mBluetoothLeService.connect(buttonInfo.viewHolder.deviceAddress.getText().toString());
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };

    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                mConnected = true;
                updateConnectionState("연결");

                deviceService=(ExpandableListView)findViewById(R.id.gatt_services_list);
                deviceService.setOnChildClickListener(servicesListClickListner);
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                mConnected = false;
                updateConnectionState("비연결");
                clearUI();
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                // Show all the supported services and characteristics on the user interface.
                displayGattServices(mBluetoothLeService.getSupportedGattServices());
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {

                //170512 추가된 코드 -시작-

                //기간별 데이터 조회를 위해서 year/month , year/month/day hour:min:sec 형태로 저장한다.
                SimpleDateFormat year_month = new SimpleDateFormat("yyyy/M", Locale.KOREAN);
                SimpleDateFormat time = new SimpleDateFormat("yyyy/M/dd HH:mm:ss", Locale.KOREAN);

                //현재 시간을 받아온다.
                Date currentTime = new Date();

                //연결이 되었을 때,
                if(intent.getStringExtra(BluetoothLeService.EXTRA_DATA) != "nodata")
                {
                    //현재 시간을 기준으로 formatting 하여서, 각각 저장한다.
                    String year_month_time = year_month.format(currentTime);
                    String dTime = time.format(currentTime);

                    String deviceaddr = buttonInfo.viewHolder.deviceAddress.getText().toString();
                    String HRData = intent.getStringExtra(BluetoothLeService.EXTRA_DATA);

                    //각각의  값을 DB에 저장을 한다.
                    DeviceInfo hr = new DeviceInfo(deviceaddr,HRData,year_month_time,dTime);

                    //result 는 테이블에 어떻게 저장되는 지를 보여준다.
                    String result;
                    result = deviceaddr +" / " + HRData + " / " + year_month_time + " / " + dTime;

                    Log.d("Result",result);
                    hr.save();
                }
                //연결이 되지 않아서, nodata가 뜰 때,
                else
                {
                    Log.d("HR1","Nodata!");
                }
                displayData(intent.getStringExtra(BluetoothLeService.EXTRA_DATA));

                //170512 추가된 코드 -끝-
            }
        }
    };

    private final ExpandableListView.OnChildClickListener servicesListClickListner =
            new ExpandableListView.OnChildClickListener() {
                @Override
                public boolean onChildClick(ExpandableListView parent, View v, int groupPosition,
                                            int childPosition, long id) {
                    if (mGattCharacteristics != null) {
                        final BluetoothGattCharacteristic characteristic =
                                mGattCharacteristics.get(groupPosition).get(childPosition);
                        final int charaProp = characteristic.getProperties();
                        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
                            // If there is an active notification on a characteristic, clear
                            // it first so it doesn't update the data field on the user interface.
                            if (mNotifyCharacteristic != null) {
                                mBluetoothLeService.setCharacteristicNotification(
                                        mNotifyCharacteristic, false);
                                mNotifyCharacteristic = null;
                            }
                            mBluetoothLeService.readCharacteristic(characteristic);
                        }

                        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
                            mNotifyCharacteristic = characteristic;
                            mBluetoothLeService.setCharacteristicNotification(
                                    characteristic, true);
                        }
                        return true;
                    }
                    return false;
                }
            };

    private void clearUI() {
        deviceService.setAdapter((SimpleExpandableListAdapter) null);
        buttonInfo.viewHolder.deviceData.setText("noData");
    }

    private void updateConnectionState(final String resourceId) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                int cnt = 0;
                List<DeviceAddress> infos = DeviceAddress.listAll(DeviceAddress.class); //DB값을 로드할 때, 이 리스트를 선언해야됨.
                if(resourceId == "연결")
                {

                    if(infos.size() == 0)
                    {
                        DeviceAddress dinfo = new DeviceAddress(String.valueOf(bleList.devices.get(buttonInfo.position).getAddress()));
                        Log.d("HR : ",String.valueOf(bleList.devices.get(buttonInfo.position).getAddress()));
                        dinfo.save();
                    }
                    else
                    {
                        Log.d("kwon","오류남");
                        Log.d("kwon",String.valueOf(infos.size()));
                        for(int i =0 ;i< infos.size();i++)
                        {
                            //기존에 저장된 DB값과, 새로 연결되는 기기의 값이 일치하지 않는 경우 cnt++를 한다.
                            if(String.valueOf(infos.get(i).deviceaddr).equals(String.valueOf(bleList.devices.get(buttonInfo.position).getAddress())))
                            {
                                cnt++;
                            }
                            else
                            {
                                cnt=0;
                                break; // 같은 것이 있을 경우,

                            }

                            //cnt 값이 기존에 저장된 infos.size()와 같으면(같은 addr이 하나도 없음), 아래와 같이 새롭게 기기를 추가해준다.
                            if(cnt == infos.size())
                            {
                                DeviceAddress dinfo = new DeviceAddress(String.valueOf(bleList.devices.get(buttonInfo.position).getAddress()));
                                Log.d("dinfo",String.valueOf(bleList.devices.get(buttonInfo.position).getAddress()));
                                dinfo.save();
                            }
                        }
                    }

                    for(int i=0;i<infos.size();i++)
                    {
                        String result;
                        Log.d("result",infos.get(i).deviceaddr);
                    }
                    // String.valueOf(infos.get(i).deviceaddr)
                }

                bleList.connects.set(buttonInfo.position,resourceId);
                bleList.notifyDataSetChanged();
            }
        });
    }


    private void displayData(String data) {
        if (data != null) {
            buttonInfo.viewHolder.deviceData.setText(data);
        }
    }

    private void displayGattServices(List<BluetoothGattService> gattServices) {
        if (gattServices == null) return;
        String uuid = null;
        String unknownServiceString = "알려지지않은 서비스";
        String unknownCharaString = "알려지지않은 특성";
        ArrayList<HashMap<String, String>> gattServiceData = new ArrayList<HashMap<String, String>>();
        ArrayList<ArrayList<HashMap<String, String>>> gattCharacteristicData
                = new ArrayList<ArrayList<HashMap<String, String>>>();
        mGattCharacteristics = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();

        // Loops through available GATT Services.
        for (BluetoothGattService gattService : gattServices) {
            HashMap<String, String> currentServiceData = new HashMap<String, String>();
            uuid = gattService.getUuid().toString();
            currentServiceData.put(
                    LIST_NAME, SampleGattAttributes.lookup(uuid, unknownServiceString));
            currentServiceData.put(LIST_UUID, uuid);
            gattServiceData.add(currentServiceData);

            ArrayList<HashMap<String, String>> gattCharacteristicGroupData =
                    new ArrayList<HashMap<String, String>>();
            List<BluetoothGattCharacteristic> gattCharacteristics =
                    gattService.getCharacteristics();
            ArrayList<BluetoothGattCharacteristic> charas =
                    new ArrayList<BluetoothGattCharacteristic>();

            // Loops through available Characteristics.
            for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                charas.add(gattCharacteristic);
                HashMap<String, String> currentCharaData = new HashMap<String, String>();
                uuid = gattCharacteristic.getUuid().toString();
                currentCharaData.put(
                        LIST_NAME, SampleGattAttributes.lookup(uuid, unknownCharaString));
                currentCharaData.put(LIST_UUID, uuid);
                gattCharacteristicGroupData.add(currentCharaData);
            }
            mGattCharacteristics.add(charas);
            gattCharacteristicData.add(gattCharacteristicGroupData);
        }

        SimpleExpandableListAdapter gattServiceAdapter = new SimpleExpandableListAdapter(
                this,
                gattServiceData,
                android.R.layout.simple_expandable_list_item_2,
                new String[] {LIST_NAME, LIST_UUID},
                new int[] { android.R.id.text1, android.R.id.text2 },
                gattCharacteristicData,
                android.R.layout.simple_expandable_list_item_2,
                new String[] {LIST_NAME, LIST_UUID},
                new int[] { android.R.id.text1, android.R.id.text2 }
        );
        deviceService.setAdapter(gattServiceAdapter);
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }
    static class ViewHolder {
        TextView deviceName;
        TextView deviceAddress;
        TextView deviceRssi;
        TextView deviceConnect;
        TextView deviceData;
        Button deviceButton;

    }

    private class buttonInfo{
        ViewHolder viewHolder;
        int position;
    }


}