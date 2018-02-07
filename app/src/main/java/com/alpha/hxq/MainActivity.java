package com.alpha.hxq;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.example.ble.BluetoothLeService;

import java.util.List;
import java.util.UUID;

import static com.example.ble.BluetoothLeService.Characteristic_uuid_TX;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static FragmentManager supportFragmentManager;
    private static Toolbar toolbar;
    private String mDeviceAddress;
    public static int lightOn = 0;
    private AlertDialog alertDialog;
    private boolean mConnect;
    private Switch aSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("杀菌烘鞋机");
        setSupportActionBar(toolbar);
        ProgressUtils.createDialog(MainActivity.this,"连接中...");
        downTimer.start();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        aSwitch = headerView.findViewById(R.id.switch_et);
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (!mConnect)
                    return;
                if (b){
                    if (mBluetoothLeService != null) {
                        mBluetoothLeService.txxx(Utils.getCheckSum("7706050000000001"), false);
                    }
                }else {
                    if (mBluetoothLeService != null) {
                        mBluetoothLeService.txxx(Utils.getCheckSum("7706050000000000"), false);

                    }
                }
            }
        });
        navigationView.setNavigationItemSelectedListener(this);
        initView();
    }

    private CountDownTimer downTimer = new CountDownTimer(10 * 1000, 100) {


        @Override
        public void onTick(long l) {

        }

        @Override
        public void onFinish() {
            if (!mConnect){
                ProgressUtils.cancelDialog();
                L.showMessage1(MainActivity.this,"连接超时");
            }

        }
    };
    public static BluetoothLeService mBluetoothLeService;

    private void initView() {
        supportFragmentManager = getSupportFragmentManager();
        toolbar.setTitle("杀菌烘鞋器");
//        FragmentTransaction fragmentTransaction = supportFragmentManager.beginTransaction();
//        fragmentTransaction.replace(R.id.fl_container, new ControlFragment());
//        fragmentTransaction.commit();
    }
    public Fragment getVisibleFragment(){
        FragmentManager fragmentManager = MainActivity.this.getSupportFragmentManager();
        List<Fragment> fragments = fragmentManager.getFragments();
        for(Fragment fragment : fragments){
            if(fragment != null && fragment.isVisible())
                return fragment;
        }
        return null;
    }
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e("hukang", "Unable to initialize Bluetooth");
            }
            Log.i("hukang", "onServiceConnected: ");
            // Automatically connects to the device upon successful start-up initialization.
            mBluetoothLeService.connect(mDeviceAddress);

        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };
    private String stringExtra;
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                Log.i("hukang", "onReceive: 连接上了");
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                Log.i("hukang", "onReceive: 没连接上了");
                mConnect = false;
                Intent intent1 = new Intent(MainActivity.this,SearchActivity.class);
                startActivity(intent1);
                finish();
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                Log.i("hukang", "onReceive: ACTION_GATT_SERVICES_DISCOVERED");
                ProgressUtils.cancelDialog();
                mConnect = true;
                //发现了服务，发送指令获取初始化参数
                displayGattServices(mBluetoothLeService.getSupportedGattServices());
                FragmentTransaction fragmentTransaction = supportFragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fl_container, ControlFragment.newInstance(stringExtra));
                fragmentTransaction.commit();

                // Show all the supported services and characteristics on the user interface.
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                stringExtra = mBluetoothLeService.getStringByBytes(intent.getByteArrayExtra(BluetoothLeService.EXTRA_DATA));
                Log.i("hukang", "onReceive: " + stringExtra);
                //获取初始化参数
                //获取应答（1.正确应答，2.错误应答）
                //获取实时参数

                if (stringExtra.startsWith("77020501")) {//烘干除湿
                    changeFragment(1, stringExtra);
                } else if (stringExtra.startsWith("77020502")) {//除臭杀菌
                    changeFragment(2, stringExtra);
                } else if (stringExtra.startsWith("77020503")) {//

                    FragmentTransaction fragmentTransaction = supportFragmentManager.beginTransaction();
//                    ControlFragment controlFragment = ControlFragment.newInstance(stringExtra);
//                    fragmentTransaction.replace(R.id.fl_container, controlFragment);
//                    fragmentTransaction.commit();
                    if (getVisibleFragment()!=null&&getVisibleFragment() instanceof ControlFragment){
                        ((BaseFragment)getVisibleFragment()).upDateUi(2,stringExtra);
                    }else {

                        ControlFragment mainFragment = ControlFragment.newInstance( stringExtra);
                        fragmentTransaction.replace(R.id.fl_container, mainFragment);
                        fragmentTransaction.commit();
                    }
                } else if (stringExtra.startsWith("770505")) {//定时开机
                    FragmentTransaction fragmentTransaction = supportFragmentManager.beginTransaction();
//                    ControlFragment controlFragment = ControlFragment.newInstance(stringExtra);
//                    fragmentTransaction.replace(R.id.fl_container, controlFragment);
//                    fragmentTransaction.commit();
                    if (getVisibleFragment()!=null&&getVisibleFragment() instanceof ControlFragment){
                        ((BaseFragment)getVisibleFragment()).upDateUi(2,stringExtra);
                    }else {

                        ControlFragment mainFragment = ControlFragment.newInstance( stringExtra);
                        fragmentTransaction.replace(R.id.fl_container, mainFragment);
                        fragmentTransaction.commit();
                    }
                } else if (stringExtra.startsWith("77ee049d")) {
                    L.showMessage1(MainActivity.this, "设置失败");
                } else if (stringExtra.startsWith("77aa04d9")) {
                    L.showMessage1(MainActivity.this, "设置成功");
                }
                if (stringExtra.length()==20){
                    if ("01".equals(stringExtra.substring(16,18))){
                        if (!aSwitch.isChecked())
                        aSwitch.setChecked(true);
                    }else {
                        if (aSwitch.isChecked())
                        aSwitch.setChecked(false);
                    }
                }
//                else if ()

            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE1.equals(action)) {
                String stringExtra = intent.getStringExtra(BluetoothLeService.EXTRA_DATA);
                Log.i("hukang", "onReceive: " + stringExtra);
                //获取初始化参数
                //获取应答（1.正确应答，2.错误应答）
                //获取实时参数

            }
        }
    };

    private void displayGattServices(List<BluetoothGattService> gattServices) {


        if (gattServices == null) return;

        if (gattServices.size() > 0 && mBluetoothLeService.get_connected_status(gattServices) == 2)//表示为JDY-06、JDY-08系列蓝牙模块
        {
            mBluetoothLeService.Delay_ms(100);
            mBluetoothLeService.enable_JDY_ble(0);
            mBluetoothLeService.Delay_ms(100);
            mBluetoothLeService.enable_JDY_ble(1);
            mBluetoothLeService.Delay_ms(100);

            byte[] WriteBytes = new byte[2];
            WriteBytes[0] = (byte) 0xE7;
            WriteBytes[1] = (byte) 0xf6;
            mBluetoothLeService.function_data(WriteBytes);// 发送读取所有IO状态


        } else if (gattServices.size() > 0 && mBluetoothLeService.get_connected_status(gattServices) == 1)//表示为JDY-09、JDY-10系列蓝牙模块
        {


            mBluetoothLeService.Delay_ms(100);
            mBluetoothLeService.enable_JDY_ble(0);

        } else {
            Toast toast = Toast.makeText(this, "提示！此设备不为JDY系列BLE模块", Toast.LENGTH_SHORT);
            toast.show();
        }

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void onResume() {
        super.onResume();
        if (getIntent() != null) {
            mDeviceAddress = getIntent().getStringExtra("address");
            registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
            Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
            bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (downTimer!=null){

            downTimer.cancel();
            downTimer = null;
        }
        unbindService(mServiceConnection);
        unregisterReceiver(mGattUpdateReceiver);
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE1);
        return intentFilter;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            if (mBluetoothLeService != null&&mConnect) {
                mBluetoothLeService.txxx(Utils.getCheckSum("7704050300000000"), false);
                FragmentTransaction fragmentTransaction = supportFragmentManager.beginTransaction();
                ControlFragment controlFragment = ControlFragment.newInstance(stringExtra);
                fragmentTransaction.replace(R.id.fl_container, controlFragment);
                fragmentTransaction.commit();
            }
            return true;
        }
//        else if (id ==R.id.action_reset_name){
//            AlertDialog.Builder builder = new AlertDialog.Builder(this);
//            View inflate = LayoutInflater.from(this).inflate(R.layout.dialog_reset_name, null);
//            final EditText edtName = inflate.findViewById(R.id.edt_name);
//            Button btnOK = inflate.findViewById(R.id.btn_ok);
//            btnOK.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    alertDialog.dismiss();
//                    if (edtName.getText().toString()!=null&&!edtName.getText().toString().equals("")){
//                        mBluetoothLeService.txxx("AT+NAME"+edtName.getText().toString()+"\r\n",false);
//                    }
//                }
//            });
//            builder.setView(inflate);
//            alertDialog = builder.create();
//            alertDialog.show();
//        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        Intent intent = null;
        // Handle navigation view item clicks here.
        int id = item.getItemId();

      if (id == R.id.nav_scan) {
            intent = new Intent(this, SearchActivity.class);
            startActivity(intent);
            unregisterReceiver(mGattUpdateReceiver);
        } else if (id == R.id.nav_sms) {
            intent = new Intent(this, InstructionActivity.class);
            startActivity(intent);
            unregisterReceiver(mGattUpdateReceiver);
        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        }
//        else if(id==R.id.nav_sy){
//            toolbar.setTitle("杀菌烘鞋器");
//            FragmentTransaction fragmentTransaction = supportFragmentManager.beginTransaction();
//            fragmentTransaction.replace(R.id.fl_container,new ControlFragment());
//            fragmentTransaction.commit();
//        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void changeFragment(int type, String data) {
        FragmentTransaction fragmentTransaction = supportFragmentManager.beginTransaction();
        if (type == 1) {
            toolbar.setTitle("烘干除湿");
            if (getVisibleFragment()!=null&&getVisibleFragment() instanceof MainFragment){
                ((BaseFragment)getVisibleFragment()).upDateUi(1,data);
            }else {

            MainFragment mainFragment = MainFragment.newInstance(1, data);
            fragmentTransaction.replace(R.id.fl_container, mainFragment);
                        fragmentTransaction.commit();
            }

        } else {

            toolbar.setTitle("除臭杀菌");
            if (getVisibleFragment()!=null&&getVisibleFragment() instanceof MainFragment){
                ((BaseFragment)getVisibleFragment()).upDateUi(2,data);
            }else {

                MainFragment mainFragment = MainFragment.newInstance(2, data);
                fragmentTransaction.replace(R.id.fl_container, mainFragment);
                fragmentTransaction.commit();
            }
        }

    }

    private long firstTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if (System.currentTimeMillis() - firstTime > 2000) {
                Toast.makeText(MainActivity.this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                firstTime = System.currentTimeMillis();
            } else {
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
