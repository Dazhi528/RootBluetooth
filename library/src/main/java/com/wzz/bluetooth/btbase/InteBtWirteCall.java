package com.wzz.bluetooth.btbase;

/**
 * 功能：用于任务执行回调
 * 描述：
 * 作者：WangZezhi
 * 邮箱：wangzezhi528@163.com
 * 创建日期：2019/1/14 16:25
 * 修改日期：2019/1/14 16:25
 */
public interface InteBtWirteCall {
    void call(byte[] bytes);
}
