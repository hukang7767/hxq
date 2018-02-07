package com.alpha.hxq;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {

    private BluetoothAdapter mBluetoothAdapter;
    private static final int REQUEST_CODE_ACCESS_COARSE_LOCATION = 1;
    private int REQUEST_CODE_LOCATION_SETTINGS = 2;
    private boolean mScanning;
    private Handler handler = new Handler(){
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
        @Override
        public void handleMessage(Message msg) {
            if (msg.what==0){
                scanLeDevice(false);

            }else {
                ProgressUtils.createDialog(SearchActivity.this,"搜索中...");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        scanLeDevice(true);
                    }
                }).start();

            }
        }
    };
    private ListView listView;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        listView = (ListView)findViewById(R.id.listView);
        initBluetoothAdapter();
        checkBLEFeature();
        if (!mBluetoothAdapter.isEnabled()) {
            if (mBluetoothAdapter.enable()) {
                checkLoactionPermission();

            } else {
                L.showMessage1(this, "蓝牙打开失败");
            }
        } else {
            checkLoactionPermission();
        }
        bindAdapter();
        scanLeDevice(true);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(SearchActivity.this,MainActivity.class);
                intent.putExtra("address",mLeDeviceListAdapter.mLeDevices.get(i).getMac());
                startActivity(intent);
                finish();
            }
        });
    }

    private void bindAdapter() {
        mLeDeviceListAdapter = new LeDeviceListAdapter();
        listView.setAdapter(mLeDeviceListAdapter);
    }
    private LeDeviceListAdapter mLeDeviceListAdapter;
    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {

        @Override
        public void onLeScan(final BluetoothDevice device, final int rssi, final byte[] scanRecord) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    mLeDeviceListAdapter
                            .addDevice(new LeDevice(device.getName(), device.getAddress(), rssi, scanRecord,false));
                    mLeDeviceListAdapter.notifyDataSetChanged();
                }
            });
        }

    };
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void scanLeDevice(boolean enable) {
        if (enable) {
            if (mBluetoothAdapter.isEnabled()) {
                if (mScanning)
                    return;
                mScanning = true;
                mBluetoothAdapter.startLeScan(mLeScanCallback);
                handler.sendEmptyMessageDelayed(0,3000);
            } else {
            }
        } else {
            ProgressUtils.cancelDialog();
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
            mScanning = false;
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void initBluetoothAdapter() {
        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

    }
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void checkBLEFeature() {
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            finish();
        }

        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        if (mBluetoothAdapter == null) {
            Toast.makeText(this, R.string.error_bluetooth_not_supported, Toast.LENGTH_SHORT).show();
            return;
        }
        if (!mBluetoothAdapter.isEnabled()) {
            mBluetoothAdapter.enable();
        }

    }


    private void checkLoactionPermission(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //判断是否需要向用户解释为什么需要申请该权限
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)) {
                Toast.makeText(this, "自Android 6.0开始需要打开位置权限才可以搜索到Ble设备", Toast.LENGTH_SHORT).show();
            }
            //请求权限
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_CODE_ACCESS_COARSE_LOCATION);

        }else {
            if (LocationUtils.isLocationEnabled()){

            }else {
                LocationUtils.openGpsSettings(this);
            }
        }

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_ACCESS_COARSE_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //用户允许改权限，0表示允许，-1表示拒绝 PERMISSION_GRANTED = 0， PERMISSION_DENIED = -1
                //permission was granted, yay! Do the contacts-related task you need to do.
                //这里进行授权被允许的处理
                if (LocationUtils.isLocationEnabled()){

                }else {
                    LocationUtils.openGpsSettings(this);
                }
            } else {
                //permission denied, boo! Disable the functionality that depends on this permission.
                //这里进行权限被拒绝的处理
                Toast.makeText(this, "禁止位置权限将无法搜索到Ble设备", Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_LOCATION_SETTINGS) {
            if (LocationUtils.isLocationEnabled()) {

                //定位已打开的处理
            } else {
                //定位依然没有打开的处理
                Toast.makeText(this, "定位未打开将无法搜索到Ble设备", Toast.LENGTH_SHORT).show();
            }
        } else super.onActivityResult(requestCode, resultCode, data);
    }
    private class LeDeviceListAdapter extends BaseAdapter {
        private ArrayList<LeDevice> mLeDevices;
        private LayoutInflater mInflator;

        public LeDeviceListAdapter() {
            mLeDevices = new ArrayList<LeDevice>();
            mInflator = getLayoutInflater();
        }

        public void addDevice(LeDevice device) {
            if (!mLeDevices.contains(device)) {
                mLeDevices.add(device);
            }
        }

        public void clear() {
            mLeDevices.clear();
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mLeDevices.size();
        }

        @Override
        public LeDevice getItem(int i) {
            return mLeDevices.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder viewHolder;

            if (view == null) {
                view = mInflator.inflate(R.layout.item_device_list, null);
                AbsListView.LayoutParams param = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 120);
                view.setLayoutParams(param);
                viewHolder = new ViewHolder();
                viewHolder.tvIsConnect = (TextView) view.findViewById(R.id.tv_isConnect);
                viewHolder.deviceName = (TextView) view.findViewById(R.id.device_name);
//                viewHolder.deviceRssi = (TextView) view.findViewById(R.id.txt_rssi);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }

            LeDevice device = mLeDevices.get(i);
            final String deviceName = device.getName();
            if (deviceName != null && deviceName.length() > 0)
                viewHolder.deviceName.setText(deviceName);
            else
                viewHolder.deviceName.setText("未知设备");
            viewHolder.tvIsConnect.setText(device.isConnect()?"已连接":"未连接");
//            viewHolder.deviceRssi.setText("rssi: " + device.getRssi() + "dbm");

            return view;
        }
    }
    private static class ViewHolder {
        TextView deviceName;
        TextView tvIsConnect;
//        TextView deviceRssi;
    }
}
