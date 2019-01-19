package com.wzz.sample;

import android.arch.lifecycle.ViewModelProviders;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import com.dazhi.libroot.base.activity.RootVmActivity;
import com.dazhi.libroot.util.UtRoot;
import com.dazhi.libroot.util.UtThread;
import com.wzz.bluetooth.UtBt;
import java.util.ArrayList;
import java.util.List;

/**
 * 功能：
 * 描述：
 * 作者：WangZezhi
 * 邮箱：wangzezhi528@163.com
 * 创建日期：2018/8/30 17:58
 * 修改日期：2018/8/30 17:58
 */
public class MainActivity extends RootVmActivity<VmMain> {
    private BluetoothAdapter bluetoothAdapter;
    //
    private DeviceListAdapter adapterPaired; //已配对列表适配器
    private int intCurPosition = -1; // 配对列表当前选择的位置
    //
    private DeviceListAdapter adapterWaitPair; //待配对列表适配器
    private final List<BluetoothDevice> lsPair=new ArrayList<>(); //待配对的设备列表
    UtBt.FoundBtReceiver foundBtReceiver; //搜索蓝牙设备
    //
    private Button btToSetPair;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initConfig() {
        // 初始化vm
        vm= ViewModelProviders.of(this).get(VmMain.class);
        // 获得蓝牙适配器
        bluetoothAdapter=UtBt.getBtDefAdapter();
        if(bluetoothAdapter==null){
            throw new UnsupportedOperationException("Bluetooth operation is not supported");
        }
        if(!bluetoothAdapter.isEnabled()){
            UtBt.openBt(this);
        }
    }

    @Override
    protected void initViewAndDataAndEvent() {
        super.initViewAndDataAndEvent();
        // 初始化视图
        initView();
        // 位置动态权限
        permissionLocation();
        // 更新UI
        updateUI();
    }

    private void updateUI(){
        List<BluetoothDevice> printerDevices = UtBt.getBtPairedList(bluetoothAdapter);
        adapterPaired.clear();
        adapterPaired.addAll(printerDevices);
        adapterPaired.notifyDataSetChanged();
        //
        if (printerDevices.size() > 0) {
            btToSetPair.setText("配对更多设备");
        } else {
            btToSetPair.setText("还未配对打印机，去设置");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == UtBt.LIBINTENT_BT_RQSTENABLE) {
            if (resultCode == RESULT_OK) {
                UtRoot.toastShort("蓝牙已开启");
            } else {
                finish();
            }
        }
    }

    private void initView() {
        // 去配对按钮点击事件
        btToSetPair = findViewById(R.id.btn_goto_setting);
        btToSetPair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(foundBtReceiver!=null){
                    return;
                }
                //
                UtThread.runSingleThread(new Runnable() {
                    @Override
                    public void run() {
                        foundBtReceiver = UtBt.startFoundBtDefDevice(MainActivity.this, bluetoothAdapter, new UtBt.InteLibBtDevice(){
                            @Override
                            public void callBtDevice(BluetoothDevice bluetoothDevice) {
                                // 没绑定的添加
                                if(lsPair.contains(bluetoothDevice)){
                                    return;
                                }
                                lsPair.add(bluetoothDevice);
                                adapterPaired.clear();
                                adapterPaired.addAll(lsPair);
                                adapterWaitPair.notifyDataSetChanged();
                            }
                            @Override
                            public void callBtDiscoveryFinished() {
                                UtRoot.toastShort("扫描设备结束");
                                foundBtReceiver=null;
                            }
                        });
                    }
                });

                // 定时关闭
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(foundBtReceiver!=null){
                            UtBt.stopFoundBtDefDevice(MainActivity.this, bluetoothAdapter, foundBtReceiver);
                        }
                        foundBtReceiver=null;
                    }
                }, 30000);
            }
        });

        // 测试连接按钮点击事件
        Button btTestConnect = findViewById(R.id.btn_test_conntect);
        btTestConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        // 打印按钮点击事件
        Button btPrint = findViewById(R.id.btn_print);
        btPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        // 已配对设备列表项点击监听
        ListView lvPairedDevice = findViewById(R.id.lv_paired_devices);
        lvPairedDevice.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                intCurPosition = position;
                adapterPaired.notifyDataSetChanged();
                // 停止搜索
                if(foundBtReceiver!=null){
                    UtBt.stopFoundBtDefDevice(MainActivity.this, bluetoothAdapter, foundBtReceiver);
                }
            }
        });

        // 配对列表设置适配器
        adapterPaired=new DeviceListAdapter(this);
        lvPairedDevice.setAdapter(adapterPaired);
        // 为配对设备
        ListView lvPairDevice = findViewById(R.id.lv_pair_devices);
        lvPairDevice.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 停止搜索
                if(foundBtReceiver!=null){
                    UtBt.stopFoundBtDefDevice(MainActivity.this, bluetoothAdapter, foundBtReceiver);
                }
                // 去配对
                UtBt.btCreateBond(lsPair.get(position));
            }
        });
        adapterWaitPair=new DeviceListAdapter(this);
        lvPairDevice.setAdapter(adapterWaitPair);
    }

    // 配对列表适配器实现类
    private class DeviceListAdapter extends ArrayAdapter<BluetoothDevice> {
        public DeviceListAdapter(Context context) {
            super(context, 0);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            BluetoothDevice device = getItem(position);
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_bluetooth_device, parent, false);
            }
            // 设置文本
            TextView tvDeviceName = convertView.findViewById(R.id.tv_device_name);
            tvDeviceName.setText("名称："+device.getName() + " 地址：" + device.getAddress());
            // 设置位置
            CheckBox cbDevice = convertView.findViewById(R.id.cb_device);
            cbDevice.setChecked(position == intCurPosition);
            return convertView;
        }
    }


}
