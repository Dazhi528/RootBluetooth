package com.wzz.bluetooth.btbase;

/**
 * 功能：蓝牙任务的基类
 * 描述：
 * 作者：WangZezhi
 * 邮箱：wangzezhi528@163.com
 * 创建日期：2019/1/14 11:57
 * 修改日期：2019/1/14 11:57
 */
public abstract class BaseBtTask<T extends BaseBtCmd> implements Runnable, Comparable<BaseBtTask> {
    protected T btRqstCmd; // 请求命令
    private int intPriority; // 优先级(数值越大，优先级越高)
    private InteBtTaskCall inteBtTaskCall;

    public String getBtRqstCmdStr() {
        if(btRqstCmd==null){
            return "";
        }
        return btRqstCmd.getCmdStr();
    }

    public BaseBtTask(int intPriority, InteBtTaskCall inteBtTaskCall){
        this.intPriority=intPriority;
        this.inteBtTaskCall=inteBtTaskCall;
    }

    @Override
    public int compareTo(BaseBtTask baseBtTask) {
        // 复写此方法进行任务执行优先级排序
        // return priority < o.priority ? -1 : (priority > o.priority ? 1 : 0);
        // intPriority是内部的；baseBtTask.intPriority是外部的
        if (intPriority < baseBtTask.intPriority) {
            return -1;
        }
        if (intPriority > baseBtTask.intPriority) {
            return 1;
        }
        return 0;
    }

    @Override
    public void run() {
        if(inteBtTaskCall!=null && btRqstCmd!=null){
            inteBtTaskCall.call(btRqstCmd.getCmdStr());
        }
    }


}
