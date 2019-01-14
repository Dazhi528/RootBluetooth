package com.wzz.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
    //
    private volatile BluetoothSocket bluetoothSocket;
    private volatile InputStream inputStream;
    private volatile OutputStream outputStream;


    BtIo(BluetoothDevice bluetoothDevice, UUID uuidServer) {
        if(bluetoothDevice==null || uuidServer==null){
            throw new UnsupportedOperationException("null can't instantiate me");
        }
        this.bluetoothDevice=bluetoothDevice;
        this.uuidServer=uuidServer;
    }


    // 将取消正在进行的连接，并关闭套接字
    public void close() {
        try {
            if(inputStream!=null){
                inputStream.close();
                inputStream=null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            if(outputStream!=null){
                outputStream.close();
                outputStream=null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            if(bluetoothSocket!=null){
                bluetoothSocket.close();
                bluetoothSocket=null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 连接方法
    private boolean connect() {
        if(bluetoothDevice==null || uuidServer==null){
            return false;
        }
        try {
            bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(uuidServer);
            if(bluetoothSocket!=null){
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

    // 重连
    public synchronized boolean reConnect() {
        close();
        try {
            Thread.sleep(1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return connect();
    }

    // 校验是否连接
    private boolean booConnect() {
        if (bluetoothSocket != null && inputStream != null && outputStream != null) {
            return bluetoothSocket.isConnected();
        }
        return false;
    }

    public synchronized void write(byte[] bytes) {
        try {
            if (booConnect()) {
                while (inputStream.available() > 0) {
                    inputStream.read();
                }
            } else {
                reConnect();
            }
            outputStream.write(bytes);
        }catch (Exception e){
            reConnect();
            e.printStackTrace();
        }
    }

    public synchronized byte[] read(){
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
        }catch (Exception e){
            e.printStackTrace();
            reConnect();
            return null;
        }finally {
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
