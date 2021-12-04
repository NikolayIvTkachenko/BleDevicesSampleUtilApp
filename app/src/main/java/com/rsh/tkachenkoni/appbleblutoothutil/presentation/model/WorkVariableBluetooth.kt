package com.rsh.tkachenkoni.appbleblutoothutil.presentation.model

import android.bluetooth.BluetoothDevice
import com.rsh.tkachenkoni.appbleblutoothutil.AppConstants
import com.rsh.tkachenkoni.appbleblutoothutil.domain.DeviceBle
import java.util.*

/**
 *
 * Created by Nikolay Tkachenko
 * E-Mail: tkachenni@mail.ru
 */
class WorkVariableBluetooth : Observable() {

    private val scannedDevices: HashSet<BluetoothDevice> = HashSet<BluetoothDevice>()

    private val deviceBleDevices: HashSet<DeviceBle> = HashSet<DeviceBle>()

    private var enableBluetooth: Boolean = true

    private var addressBluetoothDevice: String? = ""

    private var statusDevice: Int = AppConstants.STATE_DISCONNECTED

    fun getStatusDevice(): Int = statusDevice

    fun setStateDisconnected(){
        statusDevice = AppConstants.STATE_DISCONNECTED
        setChanged()
        this.notifyObservers(statusDevice)
    }

    fun setStateConnected(){
        statusDevice = AppConstants.STATE_CONNECTED
        setChanged()
        this.notifyObservers(statusDevice)
    }

    fun setStateConnecting(){
        statusDevice = AppConstants.STATE_CONNECTING
        setChanged()
        this.notifyObservers(statusDevice)
    }

    fun getAddressBluetoothDevice(): String? {
        return addressBluetoothDevice
    }

    fun setAddressBluetoothDevice(address: String?) {
        addressBluetoothDevice = address
        setChanged()
        this.notifyObservers(addressBluetoothDevice)
    }

    fun getEnableBluetooth(): Boolean {
        return enableBluetooth
    }

    fun setBluetooth(value: Boolean) {
        enableBluetooth = value
        setChanged()
        this.notifyObservers(enableBluetooth)
    }


    fun getScannedDevices(): HashSet<BluetoothDevice> {
        return scannedDevices
    }

    fun getDeviceBleDevices(): HashSet<DeviceBle> {
        return deviceBleDevices
    }

    fun addItemScannedDevices(item: BluetoothDevice) {
        scannedDevices.add(item)
        setChanged()
        this.notifyObservers(scannedDevices)
    }

    fun addItemDeviceBleDevices(item: DeviceBle) {
        deviceBleDevices.add(item)
        setChanged()
        this.notifyObservers(deviceBleDevices)
    }

    fun setListScannedDevices(set: HashSet<BluetoothDevice>) {
        scannedDevices.clear()
        scannedDevices.addAll(set)
        setChanged()
        this.notifyObservers(scannedDevices)
    }

    fun setListDeviceBleDevices(set: HashSet<DeviceBle>) {
        deviceBleDevices.clear()
        deviceBleDevices.addAll(set)
        setChanged()
        this.notifyObservers(deviceBleDevices)
    }

    fun clearListScannedDevices() {
        scannedDevices.clear()
        setChanged()
        this.notifyObservers(scannedDevices)
    }

    fun clearListDeviceBleDevices() {
        deviceBleDevices.clear()
        setChanged()
        this.notifyObservers(deviceBleDevices)
    }
}