package com.wzz.bluetooth;

import android.bluetooth.BluetoothDevice;

import com.wzz.bluetooth.btbase.BaseBtTask;
import com.wzz.bluetooth.btbase.InteBtTaskCall;
import com.wzz.bluetooth.btconnect.BtTaskConnect;

import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 功能：蓝牙设备对象，用于区分不同的蓝牙设备
 * 描述：
 * 作者：WangZezhi
 * 邮箱：wangzezhi528@163.com
 * 创建日期：2019/1/14 11:35
 * 修改日期：2019/1/14 11:35
 */
public class BtDevice {
    // 蓝牙标准串口通信通用UUID
    private static final UUID UUID_BLUETOOTH_SERIAL = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    // 最大任务数
    private final int MAX_TASK_COUNT = 300;
    // 1）最大线程数等于核心线程数等于1，说明本线程池只有一个核心线程
    // 2）保持时间是0说明没任务可执行的时候也不会销毁本线程
    // 3）指定优先级队列中的最大任务数为：MAX_TASK_COUNT
    private final ExecutorService EXECUTOR_SERVICE = new ThreadPoolExecutor(
            1, 1,
            0L, TimeUnit.MILLISECONDS,
            new PriorityBlockingQueue<Runnable>(MAX_TASK_COUNT));
    // 用于存储当前任务数量
    private final AtomicInteger AINT_CUR_TASK_COUNT = new AtomicInteger();
    private final ArrayList<String> lsTaskTag = new ArrayList<>();
    //
    private final BtIo btIo; // 用于管理设备io操作


    // 构造方法(booBle：true时，开ble功能)
    public BtDevice(BluetoothDevice bluetoothDevice, boolean booBle){
        this(bluetoothDevice, UUID_BLUETOOTH_SERIAL, booBle);
    }
    public BtDevice(BluetoothDevice bluetoothDevice, UUID uuidServer, boolean booBle) {
        if(bluetoothDevice==null || uuidServer==null){
            throw new UnsupportedOperationException("null can't instantiate me");
        }
        btIo=new BtIo(bluetoothDevice, uuidServer, booBle);
    }


    // ======外部控制部分=========
    // 连接设备方法
    public void connect(){
        BtTaskConnect btTaskConnect=new BtTaskConnect(btIo, new InteBtTaskCall() {
            @Override
            public void call(String strCmd) {
                taskCountDecrement(strCmd);
            }
        });
        exec(btTaskConnect);
    }


    // ========任务计数控制部分========
    // 任务计数加控制;
    private synchronized <T extends BaseBtTask> void exec(T btTask){
        if (AINT_CUR_TASK_COUNT.get() > MAX_TASK_COUNT) {
            return;
        }
        String tag = btTask.getBtRqstCmdStr();
        if(tag==null || tag.trim().length()==0){
            return;
        }
        //存在相同的任务,不执行该任务
        if (lsTaskTag.contains(tag)) {
            return;
        }
        lsTaskTag.add(tag);
        AINT_CUR_TASK_COUNT.incrementAndGet();
        //
        EXECUTOR_SERVICE.execute(btTask);
    }

    // 任务计数减控制
    private synchronized void taskCountDecrement(String strCmd){
        lsTaskTag.remove(strCmd);
        AINT_CUR_TASK_COUNT.decrementAndGet();
    }


}
