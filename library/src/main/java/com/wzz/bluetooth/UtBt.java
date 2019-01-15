package com.wzz.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 功能：蓝牙工具类
 * 描述：
 * 作者：WangZezhi
 * 邮箱：wangzezhi528@163.com
 * 创建日期：2019/1/4 09:17
 * 修改日期：2019/1/4 09:17
 */
public class UtBt {

    // 用于回调蓝牙设备接口
    public interface InteLibBtDevice {
        void callBtDevice(BluetoothDevice bluetoothDevice);

        void callBtDiscoveryFinished();
    }


    /**
     * =======================================
     * 作者：WangZezhi  (2019/1/4  11:10)
     * 功能：   获得蓝牙适配器
     * 描述：如果获得蓝牙适配器失败，说明不支持对应类型的蓝牙
     * =======================================
     */
    // 获得经典(传统)蓝牙适配器
    public static BluetoothAdapter getBtDefAdapter() {
        // 获取 BluetoothAdapter,判断是否支持蓝牙
        // 如果 getDefaultAdapter() 返回 null，则该设备不支持蓝牙
        return BluetoothAdapter.getDefaultAdapter();
    }

    // 获得BLE(低功耗)蓝牙适配器
    public static BluetoothAdapter getBtBleAdapter(Context context) {
        if (context == null) {
            return null;
        }
        //使用此检查确定设备是否支持BLE。然后，您可以有选择地禁用与BLE相关的功能
        if (!context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            //不支持BLE
            return null;
        }
        // api小于18处理部分
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
            return null;
        }
        //在您的应用程序可以通过BLE进行通信之前，您需要验证设备是否支持BLE
        BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        if (bluetoothManager == null) {
            return null;
        }
        // 获得蓝牙适配器
        return bluetoothManager.getAdapter();
    }


    /**
     * =======================================
     * 作者：WangZezhi  (2019/1/4  11:11)
     * 功能：   打开设备的蓝牙
     * 描述：
     * =======================================
     */
    //打开蓝牙的意图请求码
    public static final int LIBINTENT_BT_RQSTENABLE = 2001;

    //打开经典蓝牙; 调用前需检查蓝牙是否打开：bluetoothAdapter.isEnabled()
    //即：if(!bluetoothAdapter.isEnabled()) { UtLibBt.openBt(activity) }
    public static void openBt(Activity activity) {
        if (activity == null) {
            return;
        }
        Intent intentBtEnable = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        activity.startActivityForResult(intentBtEnable, LIBINTENT_BT_RQSTENABLE);
    }


    /**
     * =======================================
     * 作者：WangZezhi  (2019/1/4  11:38)
     * 功能：  获得已配对的设备List集合
     * 描述：
     * =======================================
     */
    // device.getName(); device.getAddress();
    public static List<BluetoothDevice> getBtPairedList(BluetoothAdapter bluetoothAdapter) {
        if (bluetoothAdapter == null) {
            return null;
        }
        // 显示已配对的设备
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        return new ArrayList<>(pairedDevices);
    }


    /**
     * =======================================
     * 作者：WangZezhi  (2019/1/5  10:52)
     * 功能：    配对/取消配对
     * 描述：无法直接调用配对/取消配对方法，需要利用反射
     * =======================================
     */
    // 配对
    public static void btCreateBond(BluetoothDevice device) {
        try {
            Method method = BluetoothDevice.class.getMethod("createBond");
            method.invoke(device);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //取消配对
    public static void btRemoveBond(BluetoothDevice device) {
        try {
            Method method = BluetoothDevice.class.getMethod("removeBond");
            method.invoke(device);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * =======================================
     * 作者：WangZezhi  (2019/1/5  09:00)
     * 功能： 启用可检测性,开此可以让其他设备发现自己
     * 描述：
     * 要发起连接，BluetoothDevice对象仅仅需要提供MAC地址
     * 客户端使用服务端设备的MAC地址发起连接，因此被连接的始终是服务端，主动连接的是客户端
     * 通用唯一标识符 (UUID) 是用于唯一标识信息的字符串 ID 的 128 位标准化格式。
     * 总结：
     * MAC地址相当于http请求的IP地址； UUID想到与HTTP请求的端口号；
     * =======================================
     */
    public static void openDiscoverable(Context context, int discoverTime) {
        // 启用可检测性
        Intent intentDiscoverable = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        // 默认可检测到并持续 120 秒钟，可通过下述定制，范围0~3600，其中0为设备始终可检测到
        intentDiscoverable.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, discoverTime);
        context.startActivity(intentDiscoverable);
    }


    /**
     * =======================================
     * 作者：WangZezhi  (2019/1/4  14:07)
     * 功能：   搜索其他可配对设备
     * 注意：搜索设备是耗时操作，此方法需要放到工作线程
     * =======================================
     */
    // 经典蓝牙 开始/停止 查找设备
    public static FoundBtReceiver startFoundBtDefDevice(Context context, BluetoothAdapter bluetoothAdapter, InteLibBtDevice inteLibBtDevice) {
        // 已配对的设备中没有我们需要的设备，需要去配对
        // Register the BroadcastReceiver
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        //intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        // 注册广播接收器，不要忘记在onDestroy中注销
        FoundBtReceiver foundBtReceiver = new FoundBtReceiver(inteLibBtDevice);
        context.registerReceiver(foundBtReceiver, intentFilter);
        // 开启发现蓝牙设备
        bluetoothAdapter.startDiscovery();
        //
        return foundBtReceiver;
    }

    public static void stopFoundBtDefDevice(Context context, BluetoothAdapter bluetoothAdapter, FoundBtReceiver foundBtReceiver) {
        // 关闭发现蓝牙设备
        bluetoothAdapter.cancelDiscovery();
        // 注销查找蓝牙设备的广播接收器
        context.unregisterReceiver(foundBtReceiver);
    }

    // 发现设备时，需注册广播
    private static final class FoundBtReceiver extends BroadcastReceiver {
        private InteLibBtDevice inteLibBtDevice;

        FoundBtReceiver(InteLibBtDevice inteLibBtDevice) {
            this.inteLibBtDevice = inteLibBtDevice;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            String strAction = intent.getAction();
            // 当搜索结束时
            if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(strAction)) {
                inteLibBtDevice.callBtDiscoveryFinished();
                return;
            }
            // 当发现了一个设备时
            if (!BluetoothDevice.ACTION_FOUND.equals(strAction)) {
                return;
            }
            // Get the BluetoothDevice object from the Intent
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            // 将名称和地址添加到数组适配器中，以便在ListVIEW中显示
            //mArrayAdapter.add(device.getName() + "\n" + device.getAddress());
            inteLibBtDevice.callBtDevice(device);
        }
    }

    // BLE蓝牙 开始/停止 查找设备
    public static BtBleScanCall startFoundBtBleDevice(final BluetoothAdapter bluetoothAdapter, final InteLibBtDevice inteLibBtDevice) {
        if (bluetoothAdapter == null || inteLibBtDevice == null) {
            return null;
        }
        // api 21 及以上
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            final BluetoothLeScanner bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
            if (bluetoothLeScanner == null) {
                return null;
            }
            BtBleScanCall btBleScanCall = new BtBleScanCall(inteLibBtDevice);
            // 开始扫描
            ScanCallback scanCallback = btBleScanCall.getScanCallback();
            if (scanCallback == null) {
                return null;
            } else {
                bluetoothLeScanner.startScan(scanCallback);
                return btBleScanCall;
            }
        }
        // api 18~20
        if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
            BtBleScanCall btBleScanCall = new BtBleScanCall(inteLibBtDevice);
            BluetoothAdapter.LeScanCallback leScanCallback = btBleScanCall.getLeScanCallback();
            if (leScanCallback == null) {
                return null;
            } else {
                // 开始扫描
                bluetoothAdapter.startLeScan(leScanCallback);
                return btBleScanCall;
            }
        }
        // api小于18处理部分
        return null;
    }

    public static void stopFoundBtBleDevice(final BluetoothAdapter bluetoothAdapter, final InteLibBtDevice inteLibBtDevice, final BtBleScanCall btBleScanCall) {
        if (bluetoothAdapter == null || btBleScanCall == null) {
            return;
        }
        // api 21 及以上
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            final BluetoothLeScanner bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
            if (bluetoothLeScanner == null) {
                return;
            }
            //结束扫描
            ScanCallback scanCallback = btBleScanCall.getScanCallback();
            if (scanCallback != null) {
                inteLibBtDevice.callBtDiscoveryFinished();
                bluetoothLeScanner.stopScan(scanCallback);
            }
            return;
        }
        // api 18~20
        if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
            BluetoothAdapter.LeScanCallback leScanCallback = btBleScanCall.getLeScanCallback();
            if (leScanCallback != null) {
                inteLibBtDevice.callBtDiscoveryFinished();
                bluetoothAdapter.stopLeScan(leScanCallback);
            }
            return;
        }
        // api小于18处理部分
        return;
    }

    // 蓝牙BLE发现设备用回调类
    private static final class BtBleScanCall {
        private ScanCallback scanCallback;
        private BluetoothAdapter.LeScanCallback leScanCallback;

        // 外部获取方法
        ScanCallback getScanCallback() {
            return scanCallback;
        }

        BluetoothAdapter.LeScanCallback getLeScanCallback() {
            return leScanCallback;
        }

        // 构造方法
        BtBleScanCall(final InteLibBtDevice inteLibBtDevice) {
            if (inteLibBtDevice == null) {
                return;
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                scanCallback = new ScanCallback() {
                    @Override
                    public void onScanResult(int callbackType, ScanResult result) {
                        BluetoothDevice device = result.getDevice();
                        inteLibBtDevice.callBtDevice(device);
                    }
                };
                return;
            }
            if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
                leScanCallback = new BluetoothAdapter.LeScanCallback() {
                    @Override
                    public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
                        // 发现一个设备就回调一下
                        inteLibBtDevice.callBtDevice(device);
                    }
                };
            }
        }
    }


}
