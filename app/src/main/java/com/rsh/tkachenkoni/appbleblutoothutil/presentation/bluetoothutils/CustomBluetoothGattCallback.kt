package com.rsh.tkachenkoni.appbleblutoothutil.presentation.bluetoothutils

import android.bluetooth.*
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.rsh.tkachenkoni.appbleblutoothutil.AppConstants
import com.rsh.tkachenkoni.appbleblutoothutil.presentation.isReadable
import com.rsh.tkachenkoni.appbleblutoothutil.presentation.toHexString
import java.util.*

/**
 *
 * Created by Nikolay Tkachenko
 * E-Mail: tkachenni@mail.ru
 */
class CustomBluetoothGattCallback(val context: Context) : BluetoothGattCallback()  {

    var bluetoothGatt: BluetoothGatt? = null

    val batteryServiceUuidV2 = UUID.fromString("0000180f-0000-1000-8000-00805f9b34fb")

    var messageRaw = ""

    companion object{
        const val GATT_MAX_MTU_SIZE = 517
        const val keyStart = "START_MESSAGE"
        const val keyEnd = "END_MESSAGE"
    }

    override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
        Log.d(AppConstants.TAG, "onConnectionStateChange")
        val deviceAddress = gatt?.device?.address
        if (status == BluetoothGatt.GATT_SUCCESS){
            if(BluetoothProfile.STATE_CONNECTED == newState){
                Log.d(AppConstants.TAG, "Successfully connected to $deviceAddress")

                bluetoothGatt = gatt
                Handler(Looper.getMainLooper()).post {
                    bluetoothGatt?.discoverServices()
                }

            } else {
                Log.d(AppConstants.TAG, "Successfully disconnected from $deviceAddress")
                broadcastUpdate(AppConstants.ACTION_DEVICE_DISCONNECTED, "Device disconnected",deviceAddress.orEmpty())
                gatt?.close()
            }
        } else {
            Log.d(AppConstants.TAG, "Error $status encountered for $deviceAddress! Disconnecting...")
            broadcastUpdate(AppConstants.ACTION_DEVICE_DISCONNECTED, "Device disconnected",deviceAddress.orEmpty())
            gatt?.close()
        }
    }

    override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
        Log.d(AppConstants.TAG, "onServicesDiscovered")

        Thread{
            for (service in gatt!!.services){
                for(charact in service.characteristics) {
                    Log.d(AppConstants.TAG, "charact.uuid = " +charact.uuid)
                    Log.d(AppConstants.TAG, "zerooo  charact.getStringValue = " + charact.getStringValue(0))
                    //00002a00-0000-1000-8000-00805f9b34fb  --> characteristic?.value = 0x20 72 6F 62 6F 74 5F 74 65 73 74 --> device name
                    //00002a01-0000-1000-8000-00805f9b34fb
                    //00002a02-0000-1000-8000-00805f9b34fb
                    //00002a03-0000-1000-8000-00805f9b34fb
                    //00002a04-0000-1000-8000-00805f9b34fb  --> characteristic?.value = 0x50 00 A0 00 00 00 E8 03
                    //00002a05-0000-1000-8000-00805f9b34fb
                    //0000ffe1-0000-1000-8000-00805f9b34fb  --> characteristic?.value = 0xD4 36 39 BC 21 39  --> Mac address
                    if(charact.uuid.toString().equals("0000ffe1-0000-1000-8000-00805f9b34fb")) {
                        gatt.readCharacteristic(charact)
                        charact.uuid?.let {
                            val descriptor: BluetoothGattDescriptor? =
                                charact.getDescriptor(UUID.fromString(charact.uuid.toString()))
                            descriptor?.setValue(BluetoothGattDescriptor.ENABLE_INDICATION_VALUE)
                            descriptor?.let {
                                gatt.writeDescriptor(descriptor)
                            }
                        }
                        gatt.setCharacteristicNotification(charact, true)
                        broadcastUpdate(AppConstants.ACTION_DEVICE_CONNECTED, "Device connected", gatt.device?.address.orEmpty())
                    }
                }
            }
        }.start()

        gatt?.let {
            with(it){
                printGattTable()
            }
        }
    }

    override fun onCharacteristicRead(
        gatt: BluetoothGatt?,
        characteristic: BluetoothGattCharacteristic?,
        status: Int
    ) {
        Log.d(AppConstants.TAG, "onCharacteristicRead")
        Log.d(AppConstants.TAG, "before characteristic?.value = " + characteristic?.value?.toHexString())
        Log.d(AppConstants.TAG, "status = " + status)
        Log.d(AppConstants.TAG, "onCharacteristicRead")
        Log.d(AppConstants.TAG, "before characteristic!!.uuid = " + characteristic!!.uuid)

        with(characteristic) {
            when (status) {
                BluetoothGatt.GATT_SUCCESS -> {
                    Log.d(AppConstants.TAG, "Read characteristic")
                    Log.d(AppConstants.TAG, "characteristic?.value = " + characteristic?.value?.toHexString())

                }
                BluetoothGatt.GATT_READ_NOT_PERMITTED -> {
                    Log.d(AppConstants.TAG, "Read not permitted for ___ ")
                }
                else -> {
                    Log.d(AppConstants.TAG, "Characteristic read failed for ___ error: $status")
                }
            }
        }
    }

    override fun onCharacteristicWrite(
        gatt: BluetoothGatt?,
        characteristic: BluetoothGattCharacteristic?,
        status: Int
    ) {
        Log.d(AppConstants.TAG, "onCharacteristicWrite")
        with(characteristic) {
            when (status) {
                BluetoothGatt.GATT_SUCCESS -> {
                    Log.d(AppConstants.TAG, "Wrote to characteristic")
                    Log.d(AppConstants.TAG, "characteristic?.value = " + characteristic?.value?.toHexString())
                }
                BluetoothGatt.GATT_INVALID_ATTRIBUTE_LENGTH -> {
                    Log.d(AppConstants.TAG,  "Write exceeded connection ATT MTU!")
                }
                BluetoothGatt.GATT_WRITE_NOT_PERMITTED -> {
                    Log.d(AppConstants.TAG, "Write not permitted for ")
                }
                else -> {
                    Log.d(AppConstants.TAG, "Characteristic write failed for ")
                }
            }
        }
    }

    override fun onCharacteristicChanged(
        gatt: BluetoothGatt?,
        characteristic: BluetoothGattCharacteristic?
    ) {
        Log.d(AppConstants.TAG, "onCharacteristicChanged")


        with(characteristic) {
            Log.d(AppConstants.TAG, "Characteristic  changed | value")
            Log.d(AppConstants.TAG, "characteristic?.value = " + characteristic?.value)
            Log.d(AppConstants.TAG, "characteristic?.value = " + characteristic?.value?.toHexString())
            Log.d(AppConstants.TAG, "String(characteristic?.value) = " + characteristic?.value?.let { String(it)})
            Log.d(AppConstants.TAG, "characteristic?.uuid = "+characteristic?.uuid)
            Log.d(AppConstants.TAG, "characteristic?.descriptors = "+characteristic?.descriptors)
            Log.d(AppConstants.TAG, "characteristic?.instanceId = "+characteristic?.instanceId)
            messageRaw += characteristic?.value?.let { String(it) }
            Log.d(AppConstants.TAG, "messageRaw middle = "+messageRaw)
        }


        if (messageRaw.contains(keyStart) && messageRaw.contains(keyEnd)){
            Log.d(AppConstants.TAG, "messageRaw = "+messageRaw)
            broadcastUpdate(AppConstants.ACTION_RECIEVER_SHOW_MESSAGE, messageRaw, gatt?.device?.address.orEmpty())
            broadcastUpdate(AppConstants.ACTION_RECIEVER_SEND_RECIEVE_DATA, messageRaw, gatt?.device?.address.orEmpty())
            messageRaw = ""
        }
    }

    override fun onDescriptorWrite(
        gatt: BluetoothGatt?,
        descriptor: BluetoothGattDescriptor?,
        status: Int
    ) {
        Log.d(AppConstants.TAG, "onDescriptorWrite")
        Log.d(AppConstants.TAG, "descriptor = " + descriptor.toString())
    }

    override fun onDescriptorRead(
        gatt: BluetoothGatt?,
        descriptor: BluetoothGattDescriptor?,
        status: Int
    ) {
        Log.d(AppConstants.TAG, "onDescriptorRead")
        Log.d(AppConstants.TAG, "descriptor = " + descriptor.toString())

    }

    override fun onMtuChanged(gatt: BluetoothGatt?, mtu: Int, status: Int) {
        Log.d(AppConstants.TAG, "onMtuChanged")
        Log.d(AppConstants.TAG, "ATT MTU changed to $mtu, success: ${status == BluetoothGatt.GATT_SUCCESS}")

    }

    private fun BluetoothGatt.printGattTable() {
        if (services.isEmpty()) {
            Log.d(AppConstants.TAG, "No service and characteristic available, call discoverServices() first?")
            return
        }
        services.forEach { service ->
            val characteristicsTable = service.characteristics.joinToString(
                separator = "\n|--",
                prefix = "|--"
            ) { it.uuid.toString() }
            Log.d(
                AppConstants.TAG, "\nService ${service.uuid}\nCharacteristics:\n$characteristicsTable"
            )
        }
    }

    private fun readBatteryLevel() {
        val batteryServiceUuid = UUID.fromString("0000180f-0000-1000-8000-00805f9b34fb")
        val batteryLevelCharUuid = UUID.fromString("00002a19-0000-1000-8000-00805f9b34fb")
        val batteryLevelChar =  bluetoothGatt?.getService(batteryServiceUuid)?.getCharacteristic(batteryLevelCharUuid)
        if (batteryLevelChar?.isReadable() == true) {
            bluetoothGatt?.readCharacteristic(batteryLevelChar)
        }
    }

    fun writeDescriptor(descriptor: BluetoothGattDescriptor, payload: ByteArray) {
        bluetoothGatt?.let { gatt ->
            descriptor.value = payload
            gatt.writeDescriptor(descriptor)
        } ?: error("Not connected to a BLE device!")
    }

    private fun broadcastUpdate(action: String, message: String, address: String) {
        Log.d(AppConstants.TAG, "broadcastUpdate")
        Log.d(AppConstants.TAG, "message = " +message)
        val intent = Intent(action)
        intent.putExtra(AppConstants.INTENT_MESSAGE_STRING, message)
        intent.putExtra(AppConstants.INTENT_ADDRESS_STRING, address)
        context.sendBroadcast(intent)
    }

    private fun broadcastUpdate(action: String, message: String) {
        val intent = Intent(action)
        intent.putExtra(AppConstants.INTENT_MESSAGE_STRING, message)
        context.sendBroadcast(intent)
    }
}