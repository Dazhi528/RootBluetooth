# meLibBluetooth
用途：封装了Android蓝牙所有方法，使蓝牙开发变的简单

### 项目结构及使用说明
library 是库的源码 <br/>
sample 是用此库的实例

### 蓝牙库方法说明

##### UtLibBt 静态工具类

**获得蓝牙适配器** <br/>
// 获得经典(传统)蓝牙适配器 <br/>
UtLibBt.getBtDefAdapter()

// 获得BLE(低功耗)蓝牙适配器 <br/>
UtLibBt.getBtDefAdapter()


**打开设备的蓝牙** <br/>
// 本常量用于在onActivityResult方法中判断打开结果 <br/>
UtLibBt.LIBINTENT_BT_RQSTENABLE

// 打开设备的蓝牙 <br/>
UtLibBt.openBt(Activity activity)


**获得已配对的设备List集合** <br/>
// 本方法用于获得list集合类型的已配对蓝牙设备 <br/>
UtLibBt.getBtPairedList(BluetoothAdapter bluetoothAdapter)


**配对/取消配对** <br/>
// 配对 <br/>
UtLibBt.btCreateBond()

// 取消配对 <br/>
UtLibBt.btRemoveBond()


**启用可检测性** <br/>
// 开此可以让其他设备发现自己; discoverTime范围0~3600秒，设置0为设备始终可检测到 <br/>
UtLibBt.openDiscoverable(Context context, int discoverTime)


**搜索其他可配对设备** <br/>
// 经典蓝牙 开始/停止 查找设备 <br/>
FoundBtReceiver startFoundBtDefDevice(Context context, BluetoothAdapter bluetoothAdapter, InteLibBtDevice inteLibBtDevice) <br/>

UtLibBt.stopFoundBtDefDevice(Context context, BluetoothAdapter bluetoothAdapter, FoundBtReceiver foundBtReceiver) <br/>

// BLE蓝牙 开始/停止 查找设备 <br/>
BtBleScanCall startFoundBtBleDevice(final BluetoothAdapter bluetoothAdapter, final InteLibBtDevice inteLibBtDevice) <br/>

UtLibBt.stopFoundBtBleDevice(final BluetoothAdapter bluetoothAdapter, final InteLibBtDevice inteLibBtDevice, final BtBleScanCall btBleScanCall) <br/>


**UtLibBt.InteLibBtDevice** <br/>
// 查找到设备后，会回调到本方法中 <br/>
void callBtDevice(BluetoothDevice bluetoothDevice); <br/>

// 停止搜索设备时，会回调此方法 <br/>
void callBtDiscoveryFinished();



