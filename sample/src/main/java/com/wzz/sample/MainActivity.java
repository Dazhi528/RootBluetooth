package com.wzz.sample;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.wzz.bluetooth.UtBt;

import java.util.List;

/**
 * 功能：
 * 描述：
 * 作者：WangZezhi
 * 邮箱：wangzezhi528@163.com
 * 创建日期：2018/8/30 17:58
 * 修改日期：2018/8/30 17:58
 */
public class MainActivity extends AppCompatActivity {
    private BluetoothAdapter bluetoothAdapter;
    private DeviceListAdapter deviceListAdapter; //配对列表适配器
    private int intCurPosition = -1; // 配对列表当前选择的位置
    //
    private Button btToSetPair;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 获得蓝牙适配器
        bluetoothAdapter=UtBt.getBtDefAdapter();
        if(bluetoothAdapter==null){
            throw new UnsupportedOperationException("Bluetooth operation is not supported");
        }
        if(!bluetoothAdapter.isEnabled()){
            UtBt.openBt(this);
        }
        //
        initView();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == UtBt.LIBINTENT_BT_RQSTENABLE) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "蓝牙已开启", Toast.LENGTH_SHORT).show();
            } else {
                finish();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        List<BluetoothDevice> printerDevices = UtBt.getBtPairedList(bluetoothAdapter);
        deviceListAdapter.clear();
        deviceListAdapter.addAll(printerDevices);
        //
        if (printerDevices.size() > 0) {
            btToSetPair.setText("配对更多设备");
        } else {
            btToSetPair.setText("还未配对打印机，去设置");
        }
    }

    private void initView() {
        // 去配对按钮点击事件
        btToSetPair = findViewById(R.id.btn_goto_setting);
        btToSetPair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                UtBt.FoundBtReceiver aa = UtBt.startFoundBtDefDevice(this, bluetoothAdapter, new UtBt.InteLibBtDevice(){
//                    @Override
//                    public void callBtDevice(BluetoothDevice bluetoothDevice) {
//
//                    }
//                    @Override
//                    public void callBtDiscoveryFinished() {
//
//                    }
//                });
//
//                UtBt.stopFoundBtDefDevice(Context context, BluetoothAdapter bluetoothAdapter, FoundBtReceiver foundBtReceiver);
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
        // 配对设备列表项点击监听
        ListView lvPairedDevice = findViewById(R.id.lv_paired_devices);
        lvPairedDevice.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                intCurPosition = position;
                deviceListAdapter.notifyDataSetChanged();
            }
        });
        // 配对列表设置适配器
        deviceListAdapter=new DeviceListAdapter(this);
        lvPairedDevice.setAdapter(deviceListAdapter);
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
