package com.rsh.tkachenkoni.appbleblutoothutil.presentation.bluetoothutils

import android.app.Service
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothManager
import android.bluetooth.le.*
import android.content.Context
import android.content.Intent
import android.os.*
import android.util.Log
import androidx.annotation.RequiresApi
import com.rsh.tkachenkoni.appbleblutoothutil.AppConstants
import com.rsh.tkachenkoni.appbleblutoothutil.domain.DeviceBle
import com.rsh.tkachenkoni.appbleblutoothutil.presentation.model.WorkBluetoothContainer

/**
 *
 * Created by Nikolay Tkachenko
 * E-Mail: tkachenni@mail.ru
 */
class BluetoothBleService : Service()  {

    private val bluetoothManager: BluetoothManager? = null
    private var bluetoothAdapter: BluetoothAdapter? = null
    private var bluetoothGatt: BluetoothGatt? = null

    private var bluetoothScanner: BluetoothLeScanner? = null

    private val serviceBinder = BleServiceBinder()

    private var isScanning = false
    private val scanDurations = 10000L

    private val bluetoothGattCallback = CustomBluetoothGattCallback(this)

    val context: Context = this

    inner class BleServiceBinder : Binder() {
        fun getService() : BluetoothBleService {
            initialize()
            return this@BluetoothBleService
        }
    }

    override fun onBind(intent: Intent): IBinder = serviceBinder

    override fun onUnbind(intent: Intent?): Boolean {
        close()
        return super.onUnbind(intent)
    }

    override fun onRebind(intent: Intent?) {
        super.onRebind(intent)
        Log.d(AppConstants.TAG, "BluetoothBleService onRebind() ")
    }

    override fun onDestroy() {
        Log.d(AppConstants.TAG, "BluetoothBleService onDestroy() ")
        super.onDestroy()
    }

    fun initialize(){
        Log.d(AppConstants.TAG, "BluetoothBleService initialize()")
        if (bluetoothManager == null) {
            bluetoothAdapter = (context.getSystemService(BLUETOOTH_SERVICE) as BluetoothManager).adapter
            Log.d(AppConstants.TAG, "Unable to initialize BluetoothManager.")

            if (bluetoothAdapter == null) {
                Log.d(AppConstants.TAG, "Unable to get Bluetooth Adapter.")
                WorkBluetoothContainer.getWorkVariableBluetooth().setBluetooth(false)
                WorkBluetoothContainer.getWorkVariableBluetooth().setStateDisconnected()
                WorkBluetoothContainer.getWorkVariableBluetooth().setAddressBluetoothDevice("")
            }else{
                WorkBluetoothContainer.getWorkVariableBluetooth().setBluetooth(true)
                if(bluetoothAdapter?.isEnabled!!){
                    bluetoothScanner = bluetoothAdapter?.bluetoothLeScanner!!
                }else{
                    broadcastUpdate(AppConstants.ACTION_RECIEVER_SWITCH_ON_BLUETOOTH, "Request On Bluetooth")
                }
            }
        }
        Log.d(AppConstants.TAG, "BluetoothBleService initialize completed")
    }

    fun startBleScan() {
        if(bluetoothScanner != null) {
            Log.d(AppConstants.TAG, "BluetoothBleService startBleScan")
            val scanSettings =
                ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).build()
            val scanFilter = ScanFilter.Builder()
                .build() //ParcelUuid.fromString(CharacteristicValues.BATTERY_LEVEL.toString())
            val scanFilters: MutableList<ScanFilter> = mutableListOf()
            scanFilters.add(scanFilter)
            Log.d(AppConstants.TAG, "Start Scan")
            //scanFilters - Describe relevant peripherals //Описание необходимых перефирийных устрйост (коды устройств)
            //scanSettings - Specify power profile//Настройки
            //bleScanCallback - Process scan results //обратный вызов для возврата результатов
            bluetoothScanner?.startScan(scanFilters, scanSettings, bleScanCallback)
            isScanning = true
            delayStopScan()
            broadcastUpdate(AppConstants.ACTION_RECIEVER_START_SCAN, "Start Scan")
        }
    }

    fun stopBleScan() {
        Log.d(AppConstants.TAG, "BluetoothBleService stopBleScan")
        if (bluetoothScanner != null && bleScanCallback != null) {
            bluetoothScanner?.stopScan(bleScanCallback)
            isScanning = false
            broadcastUpdate(
                AppConstants.ACTION_RECIEVER_STOP_SCAN,
                "BluetoothBleService stopBleScan"
            )
        }
    }

    fun switchOnBluetooth() {
        Log.d(AppConstants.TAG, "BluetoothBleService switchOnBluetooth")
        if (bluetoothAdapter?.isEnabled!!) {
            WorkBluetoothContainer.getWorkVariableBluetooth().setBluetooth(true)
            broadcastUpdate(AppConstants.ACTION_RECIEVER_SHOW_MESSAGE, "Already on")
            WorkBluetoothContainer.getWorkVariableBluetooth().setBluetooth(true)
        } else {
            //send to requestBluetoothEnable
            broadcastUpdate(AppConstants.ACTION_RECIEVER_SWITCH_ON_BLUETOOTH, "Request On Bluetooth")
            WorkBluetoothContainer.getWorkVariableBluetooth().setBluetooth(true)
        }
    }

    fun switchOffBluetooth() {
        Log.d(AppConstants.TAG, "BluetoothBleService switchOffBluetooth")
        if (!bluetoothAdapter?.isEnabled!!) {
            broadcastUpdate(AppConstants.ACTION_RECIEVER_SWITCH_OFF_BLUETOOTH, "Already off")
        } else {
            bluetoothAdapter?.disable()
            broadcastUpdate(AppConstants.ACTION_RECIEVER_SWITCH_OFF_BLUETOOTH, "Bluetooth turned off")
        }
        WorkBluetoothContainer.getWorkVariableBluetooth().setBluetooth(false)
        WorkBluetoothContainer.getWorkVariableBluetooth().setStateDisconnected()
        WorkBluetoothContainer.getWorkVariableBluetooth().setAddressBluetoothDevice("")
    }

    fun discoverDeviceBluetooth() {
        Log.d(AppConstants.TAG, "BluetoothBleService discoverDeviceBluetooth")
        if (!bluetoothAdapter?.isDiscovering!!) {
            broadcastUpdate(AppConstants.ACTION_RECIEVER_DISCOVER_DEVICE, "Making Your device discoverable")
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    fun pairedDeviceBluetooth() {
        Log.d(AppConstants.TAG, "BluetoothBleService pairedDeviceBluetooth()")
        if (bluetoothAdapter?.isEnabled!!) {
            WorkBluetoothContainer.getWorkVariableBluetooth().clearListDeviceBleDevices()
            WorkBluetoothContainer.getWorkVariableBluetooth().clearListScannedDevices()
            val devices = bluetoothAdapter?.bondedDevices
            Log.d(AppConstants.TAG, "devices  = " + devices?.size)
            for(device in devices!!){
                WorkBluetoothContainer.getWorkVariableBluetooth().addItemScannedDevices(device)
                val dev = DeviceBle(
                    devName = device.name,
                    devAddress = device.address,
                    devAlias = device.alias.orEmpty(),
                    devType = device.type.toString(),
                    devBondState = device.bondState.toString(),
                    devRSSI = ""
                )
                Log.d(AppConstants.TAG, "device  = "+device .name)
                WorkBluetoothContainer.getWorkVariableBluetooth().addItemDeviceBleDevices(dev)
            }
        } else {
            broadcastUpdate(AppConstants.ACTION_RECIEVER_SHOW_MESSAGE, "Turn on bluetooth first")
        }
    }

    fun connectBluetoothDeviceData(device: BluetoothDevice?) {
        Log.d(AppConstants.TAG, "BluetoothBleService connectBluetoothDeviceData")

        //Проверка для повторного подключения
        if(!WorkBluetoothContainer.getWorkVariableBluetooth().getAddressBluetoothDevice().equals("") && device?.address.toString().equals(WorkBluetoothContainer.getWorkVariableBluetooth().getAddressBluetoothDevice())){
            Log.d(AppConstants.TAG, "Device is connected!")
            broadcastUpdate(AppConstants.ACTION_RECIEVER_SHOW_MESSAGE, "Device is connected!")
            WorkBluetoothContainer.getWorkVariableBluetooth().setStateConnected()
            WorkBluetoothContainer.getWorkVariableBluetooth().setAddressBluetoothDevice(device?.address)
        }else{
            Log.d(AppConstants.TAG, "else BluetoothBleService connectBluetoothDeviceData")
            WorkBluetoothContainer.getWorkVariableBluetooth().setStateConnecting()
            val serverCallback = bluetoothGattCallback
            val serverBluetoothGatt : BluetoothGatt? = device?.connectGatt(this, false, serverCallback)
            serverBluetoothGatt?.requestMtu(CustomBluetoothGattCallback.GATT_MAX_MTU_SIZE)
        }
    }

    private fun delayStopScan(){
        Log.d(AppConstants.TAG, "BluetoothBleService delayStopScan")
        Handler(Looper.getMainLooper()).postDelayed({
            Log.d(AppConstants.TAG, "Handler Stop Scan")
            stopBleScan()
        }, scanDurations)
    }

    private fun broadcastUpdate(action: String, message: String) {
        val intent = Intent(action)
        intent.putExtra(AppConstants.INTENT_MESSAGE_STRING, message)
        sendBroadcast(intent)
    }

    private val bleScanCallback : ScanCallback by lazy {
        WorkBluetoothContainer.getWorkVariableBluetooth().clearListDeviceBleDevices()
        WorkBluetoothContainer.getWorkVariableBluetooth().clearListScannedDevices()
        object : ScanCallback() {
            @RequiresApi(Build.VERSION_CODES.R)
            override fun onScanResult(callbackType: Int, result: ScanResult?) {
                Log.d(AppConstants.TAG, "onScanResult")

                val bluetoothDevice = result?.device
                Log.d(AppConstants.TAG, "bluetoothDevice.exist = " + (bluetoothDevice != null))
                if(bluetoothDevice != null){
                    Log.d(AppConstants.TAG, "bluetoothDevice.uuids.orEmpty() = " + bluetoothDevice.uuids.orEmpty())
                    WorkBluetoothContainer.getWorkVariableBluetooth().addItemScannedDevices(bluetoothDevice)
                    val dev = DeviceBle(
                        devName = bluetoothDevice.name,
                        devAddress = bluetoothDevice.address,
                        devAlias = bluetoothDevice.alias.orEmpty(),
                        devType = bluetoothDevice.type.toString(),
                        devBondState = bluetoothDevice.bondState.toString(),
                        devRSSI = "" //bluetoothDevice.uuids.orEmpty()
                    )
                    Log.d(AppConstants.TAG, "device  = "+bluetoothDevice.name)
                    WorkBluetoothContainer.getWorkVariableBluetooth().addItemDeviceBleDevices(dev)
                }
            }
            override fun onBatchScanResults(results: List<ScanResult?>?) {
                // Ignore for now
                Log.d(AppConstants.TAG, "onBatchScanResults")
                Log.d(AppConstants.TAG, "results?.size = " + results?.size)
            }

            override fun onScanFailed(errorCode: Int) {
                // Ignore for now
                Log.d(AppConstants.TAG, "onScanFailed")
                Log.d(AppConstants.TAG, "errorCode = " + errorCode)
            }
        }
    }

    fun disconnect() {
        if (bluetoothAdapter == null || bluetoothGatt == null) {
            Log.d(AppConstants.TAG, "BluetoothAdapter not initialized")
            return
        }
        bluetoothGatt?.disconnect()
    }

    fun close() {
        if (bluetoothGatt == null) {
            return
        }
        bluetoothGatt?.close()
        bluetoothGatt = null
    }
}