package com.wzz.bluetooth.bttask;

import com.wzz.bluetooth.BtIo;
import com.wzz.bluetooth.btbase.BaseBtTask;
import com.wzz.bluetooth.btbase.InteBtTaskCall;

/**
 * 功能：蓝牙连接任务
 * 描述：
 * 作者：WangZezhi
 * 邮箱：wangzezhi528@163.com
 * 创建日期：2019/1/14 14:30
 * 修改日期：2019/1/14 14:30
 */
public class BtTaskWrite extends BaseBtTask<BtRqstCmdWrite> {
    private BtIo btIo;
    private long startTime;

    // 连接任务优先级设为：1；
    public BtTaskWrite(byte[] byteArr, int intPriority, BtIo btIo, InteBtTaskCall inteBtTaskCall) {
        super(intPriority, inteBtTaskCall);
        btRqstCmd=new BtRqstCmdWrite(byteArr);
        //
        this.btIo=btIo;
    }

    @Override
    public void run() {
        // 发送数据
        startTime = System.currentTimeMillis();
        btIo.writeDef(btRqstCmd.getCmd());
        // 接收数据
        while (true) {
            if (System.currentTimeMillis() - startTime > 6000) {
                // 最多等待6秒
                break;
            }
            if (btIo.booConnectDef()) {
                try {
                    Thread.sleep(200);
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
                // 连接则接收
                byte[] bytes = btIo.readDef();
                if(bytes==null){
                    continue;
                }
                // 回传接收到的数据
                inteBtTaskCall.callRead(bytes);
                // 只有接收到回复后，任务才丢
                //super.run();
                // 接收到数据，跳出循环
                break;
            } else {
                // 不连接则重连
                btIo.reConnectDef();
            }
        }
        // 执行完上面的逻辑，再调用父逻辑
        btIo=null;
        super.run(); //不管是否成功，任务直接丢
    }


}
