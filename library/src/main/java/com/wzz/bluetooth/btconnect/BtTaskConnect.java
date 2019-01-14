package com.wzz.bluetooth.btconnect;

import android.bluetooth.BluetoothSocket;
import com.wzz.bluetooth.BtIo;
import com.wzz.bluetooth.btbase.BaseBtTask;
import com.wzz.bluetooth.btbase.InteBtTaskCall;
import java.io.IOException;

/**
 * 功能：蓝牙连接任务
 * 描述：
 * 作者：WangZezhi
 * 邮箱：wangzezhi528@163.com
 * 创建日期：2019/1/14 14:30
 * 修改日期：2019/1/14 14:30
 */
public class BtTaskConnect extends BaseBtTask<BtRqstCmdConnect> {
    private BtIo btIo;

    // 连接任务优先级设为：8； 此项目中8为最高优先级
    public BtTaskConnect(BtIo btIo, InteBtTaskCall inteBtTaskCall) {
        super(8, inteBtTaskCall);
        btRqstCmd=new BtRqstCmdConnect();
        //
        this.btIo=btIo;
    }

    @Override
    public void run() {
        super.run();
        //
        btIo.reConnect();
        btIo=null;
    }


}
