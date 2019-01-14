package com.wzz.bluetooth.btbase;

/**
 * 功能：构建蓝牙命令的基类
 * 描述：
 * 作者：WangZezhi
 * 邮箱：wangzezhi528@163.com
 * 创建日期：2019/1/14 14:59
 * 修改日期：2019/1/14 14:59
 */
public class BaseBtCmd {
    protected byte[] cmd;

    public byte[] getCmd() {
        return cmd;
    }

    public String getCmdStr() {
        if(cmd==null){
            return "";
        }
        return new String(cmd);
    }


}
