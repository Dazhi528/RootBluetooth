package com.wzz.bluetooth.bttask;

import com.wzz.bluetooth.btbase.BaseBtCmd;

/**
 * 功能：
 * 描述：
 * 作者：WangZezhi
 * 邮箱：wangzezhi528@163.com
 * 创建日期：2019/1/16 08:47
 * 修改日期：2019/1/16 08:47
 */
public class BtRqstCmdWrite extends BaseBtCmd {

    public BtRqstCmdWrite(byte[] byteArr) {
        cmd=byteArr;
    }


}
