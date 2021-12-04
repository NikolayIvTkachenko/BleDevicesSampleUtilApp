package com.rsh.tkachenkoni.appbleblutoothutil.presentation

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.*
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.rsh.tkachenkoni.appbleblutoothutil.AppConstants
import com.rsh.tkachenkoni.appbleblutoothutil.R
import com.rsh.tkachenkoni.appbleblutoothutil.presentation.bluetoothutils.BluetoothBleService
import com.rsh.tkachenkoni.appbleblutoothutil.presentation.bluetoothutils.I_BluetoothCommandMethods
import com.rsh.tkachenkoni.appbleblutoothutil.presentation.bluetoothutils.I_ConnectOperationBluetooth
import com.rsh.tkachenkoni.appbleblutoothutil.presentation.model.WorkBluetoothContainer
import com.rsh.tkachenkoni.appbleblutoothutil.presentation.utils.hasPermission
import com.rsh.tkachenkoni.appbleblutoothutil.presentation.utils.requestPermission

class MainActivity : AppCompatActivity(), I_BluetoothCommandMethods, I_ConnectOperationBluetooth {

    var bleService: BluetoothBleService? = null
    var isBound = false

    val isLocationPermissionGranted
        get() = hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)

    protected val serverConn = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, binder: IBinder) {
            val binderService = binder as BluetoothBleService.BleServiceBinder
            bleService = binderService.getService()
            isBound = true


            scriptActionStep01()
        }

        override fun onServiceDisconnected(name: ComponentName) {
            isBound = false
        }
    }

    protected val appBroadcasrReciever = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val action = intent?.action
            when (action) {
                AppConstants.ACTION_RECIEVER_SHOW_MESSAGE -> getMessage(
                    intent.getStringExtra(
                        AppConstants.INTENT_MESSAGE_STRING
                    )
                )
                AppConstants.ACTION_RECIEVER_START_SCAN -> {
                    getMessage(
                        intent.getStringExtra(
                            AppConstants.INTENT_MESSAGE_STRING
                        )
                    )
                }
                AppConstants.ACTION_RECIEVER_STOP_SCAN -> {
                    getMessage(
                        intent.getStringExtra(
                            AppConstants.INTENT_MESSAGE_STRING
                        )
                    )
                    //вызываем скрипт подключения устроства с device  =  robot_test
                    scriptActionStep02()
                }
                AppConstants.ACTION_RECIEVER_SWITCH_ON_BLUETOOTH -> {
                    requestBluetoothEnable()
                    getMessage(
                        intent.getStringExtra(
                            AppConstants.INTENT_MESSAGE_STRING
                        )
                    )
                }
                AppConstants.ACTION_RECIEVER_SWITCH_OFF_BLUETOOTH -> {
                    getMessage(
                        intent.getStringExtra(
                            AppConstants.INTENT_MESSAGE_STRING
                        )
                    )
                }
                AppConstants.ACTION_RECIEVER_DISCOVER_DEVICE -> {
                    actionDiscoverDeviceBluetooth()
                    getMessage(intent.getStringExtra(AppConstants.INTENT_MESSAGE_STRING))
                }
                AppConstants.ACTION_RECIEVER_PAIRED_DEVICES_SHOW -> ""
                AppConstants.ACTION_DEVICE_CONNECTED -> {
                    Log.d(
                        AppConstants.TAG,
                        "MainActivivty AppConstants.ACTION_DEVICE_DISCONNECTED"
                    )
                    WorkBluetoothContainer.getWorkVariableBluetooth().setStateConnected()
                    WorkBluetoothContainer.getWorkVariableBluetooth()
                        .setAddressBluetoothDevice(intent.getStringExtra(AppConstants.INTENT_ADDRESS_STRING))
                    getMessage(intent.getStringExtra(AppConstants.INTENT_MESSAGE_STRING))
                }
                AppConstants.ACTION_DEVICE_DISCONNECTED -> {
                    Log.d(
                        AppConstants.TAG,
                        "MainActivivty AppConstants.ACTION_DEVICE_DISCONNECTED"
                    )
                    WorkBluetoothContainer.getWorkVariableBluetooth().setStateDisconnected()
                    WorkBluetoothContainer.getWorkVariableBluetooth().setAddressBluetoothDevice("")
                    getMessage(intent.getStringExtra(AppConstants.INTENT_MESSAGE_STRING))
                }
                AppConstants.ACTION_RECIEVER_SEND_RECIEVE_DATA -> {
                    Log.d(
                        AppConstants.TAG,
                        "MainActivivty AppConstants.ACTION_RECIEVER_SEND_RECIEVE_DATA"
                    )
                    val messageFromDevice =
                        intent.getStringExtra(AppConstants.INTENT_MESSAGE_STRING)
                    Log.d(
                        AppConstants.TAG,
                        "MainActivivty messageFromDevice = " + messageFromDevice
                    )
                    prepareStringSaveDB(messageFromDevice)
                }
            }
        }
    }

    private fun prepareStringSaveDB(messageFromDevice: String?) {
        Log.d(AppConstants.TAG, "MainActivity  prepareStringSaveDB")
        Log.d(
            AppConstants.TAG,
            "MainActivivty  messageFromDevice = " + messageFromDevice
        )
        messageFromDevice?.let {message ->
            Log.d(
                AppConstants.TAG,
                "MainActivivty  Do something with text message = " + message
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(AppConstants.TAG, "MainActivity onCreate()")
        setContentView(R.layout.activity_main)

        //Start BroadcasrReciever
        startBroadcastReciever()

        //Start Service BLE after ger permission
        requestLocationPermission()
    }

    override fun onStart() {
        super.onStart()
        Log.d(AppConstants.TAG, "MainActivity onStart()")
    }

    override fun onResume() {
        super.onResume()
        Log.d(AppConstants.TAG, "MainActivityonResume()")

    }

    override fun onPause() {
        super.onPause()
        Log.d(AppConstants.TAG, "MainActivity onPause()")
    }

    override fun onStop() {
        super.onStop()
        Log.d(AppConstants.TAG, "MainActivity onStop() ")
    }

    override fun onDestroy() {
        if (isBound) {
            val intent = Intent(this, BluetoothBleService::class.java)
            bleService?.onUnbind(intent)
        }
        unregisterReceiver(appBroadcasrReciever)
        Log.d(AppConstants.TAG, "MainActivity onDestroy() ")
        super.onDestroy()
    }

    fun startBroadcastReciever() {
        val intentFilter = IntentFilter()
        intentFilter.addAction(AppConstants.ACTION_RECIEVER_SHOW_MESSAGE)
        intentFilter.addAction(AppConstants.ACTION_RECIEVER_START_SCAN)
        intentFilter.addAction(AppConstants.ACTION_RECIEVER_STOP_SCAN)
        intentFilter.addAction(AppConstants.ACTION_RECIEVER_SWITCH_ON_BLUETOOTH)
        intentFilter.addAction(AppConstants.ACTION_RECIEVER_SWITCH_OFF_BLUETOOTH)
        intentFilter.addAction(AppConstants.ACTION_RECIEVER_DISCOVER_DEVICE)
        intentFilter.addAction(AppConstants.ACTION_RECIEVER_PAIRED_DEVICES_SHOW)
        intentFilter.addAction(AppConstants.ACTION_DEVICE_CONNECTED)
        intentFilter.addAction(AppConstants.ACTION_DEVICE_DISCONNECTED)
        intentFilter.addAction(AppConstants.ACTION_RECIEVER_SEND_RECIEVE_DATA)
        registerReceiver(appBroadcasrReciever, intentFilter)
    }

    fun requestBluetoothEnable() {
        Log.d(AppConstants.TAG, "MainActivity requestBluetoothEnable() ")
        val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        startActivityForResult(enableBtIntent, ENABLE_BLUETOOTH_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            ENABLE_BLUETOOTH_REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK) {
                    //Сообщение что прибор подключить нельзя
                    Log.d(
                        AppConstants.TAG,
                        "MainActivity onActivityResult() ENABLE_BLUETOOTH_REQUEST_CODE and RESULT_OK"
                    )
                    bleService?.initialize()
                } else {
                    Log.d(
                        AppConstants.TAG,
                        "MainActivity onActivityResult() ENABLE_BLUETOOTH_REQUEST_CODE else RESULT_OK"
                    )
                }
            }
            ACTION_REQUEST_DISCOVERABLE_BLUETOOTH -> {
                if (resultCode == Activity.RESULT_OK) {
                    //Сообщение что прибор подключить нельзя
                    Log.d(
                        AppConstants.TAG,
                        "MainActivity onActivityResult() ACTION_REQUEST_DISCOVERABLE_BLUETOOTH and RESULT_OK"
                    )
                } else {
                    Log.d(
                        AppConstants.TAG,
                        "MainActivity onActivityResult() ACTION_REQUEST_DISCOVERABLE_BLUETOOTH else RESULT_OK"
                    )
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            LOCATION_PERMISSION_REQUEST_CODE -> {
                if (grantResults.firstOrNull() == PackageManager.PERMISSION_DENIED) {
                    finish()
                } else {
                    bindStartService()
                }
            }
        }
    }

    fun getMessage(str: String?) {
        Toast.makeText(this, str, Toast.LENGTH_LONG).show()
    }


    private fun bindStartService() {
        Log.d(AppConstants.TAG, "MainActivity bindStartService()")
        val intent = Intent(this, BluetoothBleService::class.java)
        bindService(intent, serverConn, Context.BIND_AUTO_CREATE)
    }

    private fun requestLocationPermission() {
        Log.d(AppConstants.TAG, "MainActivity requestLocationPermission()")
        if (isLocationPermissionGranted) {
            bindStartService()
            return
        }
        requestPermission(
            Manifest.permission.ACCESS_FINE_LOCATION,
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    protected fun actionDiscoverDeviceBluetooth() {
        var intent = Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE)
        startActivityForResult(intent, ACTION_REQUEST_DISCOVERABLE_BLUETOOTH)
    }


    override fun switchOnBluetooth() {
        Log.d(AppConstants.TAG, "MainActivity switchOnBluetooth()")
        bleService?.switchOnBluetooth()
    }

    override fun switchOffBluetooth() {
        Log.d(AppConstants.TAG, "MainActivity switchOffBluetooth()")
        bleService?.switchOffBluetooth()
    }

    override fun discoverDeviceBluetooth() {
        Log.d(AppConstants.TAG, "MainActivity discoverDeviceBluetooth()")
        bleService?.discoverDeviceBluetooth()
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun pairedDeviceBluetooth() {
        Log.d(AppConstants.TAG, "MainActivity pairedDeviceBluetooth()")
        bleService?.pairedDeviceBluetooth()
    }

    override fun startBleScan() {
        Log.d(AppConstants.TAG, "MainActivity startBleScan()")
        bleService?.startBleScan()
    }

    override fun stopBleScan() {
        Log.d(AppConstants.TAG, "MainActivity stopBleScan()")
        Log.d(AppConstants.TAG, "stopBleScan")
        bleService?.stopBleScan()
    }

    override fun connectBluetoothDeviceData(device: BluetoothDevice?) {
        Log.d(AppConstants.TAG, "connectBluetoothDeviceData")
        if (device == null) {
            Log.d(AppConstants.TAG, "device == null")
        }
        if (WorkBluetoothContainer.getWorkVariableBluetooth()
                .getStatusDevice() == AppConstants.STATE_DISCONNECTED
        ) {
            Log.d(
                AppConstants.TAG,
                "WorkBluetoothContainer.getWorkVariableBluetooth().getStatusDevice() == AppConstants.STATE_DISCONNECTED"
            )
            bleService?.connectBluetoothDeviceData(device)
        } else {
            Log.d(AppConstants.TAG, "else")
            //Проверяем адресс устройства
            if (device?.address?.equals(
                    WorkBluetoothContainer.getWorkVariableBluetooth().getAddressBluetoothDevice()
                ) == false
            ) {
                Log.d(
                    AppConstants.TAG,
                    "device?.address?.equals(WorkBluetoothContainer.getWorkVariableBluetooth().getAddressBluetoothDevice())==false"
                )
                bleService?.connectBluetoothDeviceData(device)
            }
        }
    }

    override fun disconnectBluetoothDeviceData(device: BluetoothDevice?) {
        if (WorkBluetoothContainer.getWorkVariableBluetooth()
                .getStatusDevice() == AppConstants.STATE_CONNECTED
        ) {
            bleService?.disconnect()
        }
    }


    companion object {
        private const val ENABLE_BLUETOOTH_REQUEST_CODE = 1
        private const val ACTION_REQUEST_DISCOVERABLE_BLUETOOTH: Int = 2
        private const val LOCATION_PERMISSION_REQUEST_CODE = 5
    }

    //===================
    //SCRIPT BLE
    fun scriptActionStep01(){
        Log.d(AppConstants.TAG, "scriptActionStep01()")
        switchOnBluetooth()
        //discoverDeviceBluetooth()
        startBleScan()
    }

    fun scriptActionStep02(){
        Log.d(AppConstants.TAG, "scriptActionStep02()")
        var listDevices = WorkBluetoothContainer.getWorkVariableBluetooth().getDeviceBleDevices()
        for (item in listDevices){
            Log.d(AppConstants.TAG, "item device name = " + item.devName)
            if (item.devName == " robot_test") {
                Log.d(AppConstants.TAG, "item.devName == robot_test")
                Log.d(AppConstants.TAG, "device found")
                val bluetoothDeviceList = WorkBluetoothContainer.getWorkVariableBluetooth().getScannedDevices()
                val deviceBlue = bluetoothDeviceList.filter {it ->
                    it.address.equals(item.devAddress)}.first()

                connectBluetoothDeviceData(deviceBlue)
            }
        }
    }
}