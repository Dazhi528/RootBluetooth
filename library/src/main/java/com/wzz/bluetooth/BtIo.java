package com.wzz.bluetooth;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Build;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.UUID;

/**
 * 功能：蓝牙IO读写工具类
 * 描述：
 * 作者：WangZezhi
 * 邮箱：wangzezhi528@163.com
 * 创建日期：2019/1/4 10:44
 * 修改日期：2019/1/4 10:44
 */
public class BtIo {
    // 外部传参
    private BluetoothDevice bluetoothDevice;
    private UUID uuidServer;
    // 经典蓝牙
    private volatile BluetoothSocket bluetoothSocket; // 经典蓝牙连接
    private volatile InputStream inputStream;
    private volatile OutputStream outputStream;
    // BLE蓝牙  GATT(Generic Attribute 通用属性;  Profile 配置文件)
    private volatile BluetoothGatt bluetoothGatt; // ble蓝牙连接
    private BluetoothGattCallback bluetoothGattCallback;

    // 构造函数； booBle：true时，开ble功能
    BtIo(BluetoothDevice bluetoothDevice, UUID uuidServer, boolean booBle) {
        if (bluetoothDevice == null || uuidServer == null) {
            throw new UnsupportedOperationException("null can't instantiate me");
        }
        this.bluetoothDevice = bluetoothDevice;
        this.uuidServer = uuidServer;
        if (booBle) {
            initBleCallListener();
        }
    }


    // 将取消正在进行的连接，并关闭套接字
    public void close() {
        try {
            if (inputStream != null) {
                inputStream.close();
                inputStream = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            if (outputStream != null) {
                outputStream.close();
                outputStream = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            if (bluetoothSocket != null) {
                bluetoothSocket.close();
                bluetoothSocket = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        // ble
        if (bluetoothGatt != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            bluetoothGatt.disconnect();
            bluetoothGatt.close();
            bluetoothGatt = null;
        }
    }

    /**
     * =======================================
     * 作者：WangZezhi  (2019/1/15  11:45)
     * 功能： 蓝牙连接
     * 描述：
     * =======================================
     */
    // 经典蓝牙连接方法
    private boolean connectDef() {
        if (bluetoothDevice == null || uuidServer == null) {
            return false;
        }
        try {
            bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(uuidServer);
            if (bluetoothSocket != null) {
                bluetoothSocket.connect();
                inputStream = bluetoothSocket.getInputStream();
                outputStream = bluetoothSocket.getOutputStream();
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    // BLE蓝牙连接方法(传输数据量较小，最大 512 个字节，超过 20 个字节需要分包处理)
    // 分包处理：即当数据超过20个字节时，我们一般把一个包设为15个字节长度；然后将数据
    // 长度除以一个包长(15),得到发送次数，然后for循环发送这么多次的不同包，从而最终把
    // 数据发送出去(需要做丢包处理，以为一但包丢了摸个包，过去的数据会不完整)
    // profile(类似服务器)
    //     -> service+serviceUUID(服务 类似服务器上的应用)
    //         -> characteristic+characteristicUUID(特征值 类似应用端口)
    //             -> descriptor 描述符
    // BLE蓝牙连接方法比较复杂，步骤如下：
    // 1）调用连接方法bluetoothDevice.connectGatt()获得bluetoothGatt蓝牙通用属性配置类
    // 2）调用bluetoothGatt.connect();建立连接
    // 3）找到服务对象
    // 4）找到特征值
    private boolean connectBle(Context context) {
        if (bluetoothDevice == null || uuidServer == null) {
            return false;
        }
        // api 18 及 以上
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            // 创建bluetoothGatt对象
            // Context context, boolean autoConnect(建议设false), BluetoothGattCallback callback
            bluetoothGatt = bluetoothDevice.connectGatt(context, false, bluetoothGattCallback);
            // 连接
            if (bluetoothGatt != null) {
                // 返回连接是否成功
                return bluetoothGatt.connect();
            }
        }
        return false;
    }

    // 蓝牙通用属性配置文件类回调，用于监听低功耗蓝牙通讯各个时期的不同状态
    private void initBleCallListener() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            bluetoothGattCallback = new BluetoothGattCallback() {
                @Override
                public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                    // 当连接状态改变时，会自动回调此方法
                    if (newState == BluetoothProfile.STATE_CONNECTED) {
                        // 连接成功
                    } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                        // 连接断开
                        close();
                    }
                }
                // 调用开启发现服务的方法；发现服务后会在onServicesDiscovered中收到
                // bluetoothGatt.discoverServices();
//                @Override
//                public void onServicesDiscovered(BluetoothGatt gatt, int status) {
//                    // 当发现服务时，自动回调
//                    if (status == BluetoothGatt.GATT_SUCCESS) {
//                        // 连接GATT成功; 获取服务列表
//                        List<BluetoothGattService> servicesList = gatt.getServices();
//                    }
//                }
            };
        }
    }


    // 经典蓝牙重连
    public synchronized boolean reConnectDef() {
        close();
        try {
            Thread.sleep(1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return connectDef();
    }

    // 校验是否连接
    private boolean booConnectDef() {
        if (bluetoothSocket != null && inputStream != null && outputStream != null) {
            return bluetoothSocket.isConnected();
        }
        return false;
    }

    /**
     * =======================================
     * 作者：WangZezhi  (2019/1/15  11:46)
     * 功能：蓝牙写
     * 描述：
     * =======================================
     */
    // 经典蓝牙写
    public synchronized void writeDef(byte[] bytes) {
        try {
            if (booConnectDef()) {
                while (inputStream.available() > 0) {
                    inputStream.read();
                }
            } else {
                reConnectDef();
            }
            outputStream.write(bytes);
        } catch (Exception e) {
            reConnectDef();
            e.printStackTrace();
        }
    }

    // 低功耗蓝牙写
    // 单个包长15个字节； 总发送数据不能大于512个字节； 512/15=34余数是2
    public void writeBle(byte[] bytes, UUID uuidCharacteristic) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
            // 获得BluetoothGattService
            BluetoothGattService service = bluetoothGatt.getService(uuidServer);

            if (service != null) {
                // 获得特征类
                BluetoothGattCharacteristic characteristic = service.getCharacteristic(uuidCharacteristic);
                // bluetoothGatt.setCharacteristicNotification(characteristic, true);
                // todo 分包处理

                byte[] valueByte = bytes;
                characteristic.setValue(valueByte);

                // 发送数据
                bluetoothGatt.writeCharacteristic(characteristic);
            }
        }
    }

    /**
     * =======================================
     * 作者：WangZezhi  (2019/1/15  11:46)
     * 功能：蓝牙读
     * 描述：
     * =======================================
     */
    // 经典蓝牙读
    public synchronized byte[] readDef() {
        ByteArrayOutputStream arrayOut = null;
        try {
            if (inputStream.available() > 0) {
                byte[] temp = new byte[1024];
                arrayOut = new ByteArrayOutputStream();
                while (inputStream.available() > 0) {
                    int dataSize = inputStream.read(temp);
                    arrayOut.write(temp, 0, dataSize);
                }
                return arrayOut.toByteArray();
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            reConnectDef();
            return null;
        } finally {
            if (arrayOut != null) {
                try {
                    arrayOut.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


}
