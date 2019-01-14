package com.wzz.bluetooth.btconnect;

import com.wzz.bluetooth.btbase.BaseBtCmd;

/**
 * 功能：蓝牙连接对象
 * 描述：
 * 作者：WangZezhi
 * 邮箱：wangzezhi528@163.com
 * 创建日期：2019/1/14 15:42
 * 修改日期：2019/1/14 15:42
 */
public class BtRqstCmdConnect extends BaseBtCmd {

    public BtRqstCmdConnect(){
        cmd = "connect".getBytes();
    }


}
