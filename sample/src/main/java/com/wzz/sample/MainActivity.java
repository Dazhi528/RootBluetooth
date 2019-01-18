package com.wzz.sample;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
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

import com.wzz.bluetooth.UtBt;

/**
 * 功能：
 * 描述：
 * 作者：WangZezhi
 * 邮箱：wangzezhi528@163.com
 * 创建日期：2018/8/30 17:58
 * 修改日期：2018/8/30 17:58
 */
public class MainActivity extends AppCompatActivity {
    private DeviceListAdapter deviceListAdapter; //配对列表适配器
    private int intCurPosition = -1; // 配对列表当前选择的位置

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 获得蓝牙适配器
        UtBt.getBtDefAdapter();
        //
        initView();
    }

    private void initView() {
        // 去配对按钮点击事件
        Button btToSetPair = findViewById(R.id.btn_goto_setting);
        btToSetPair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
