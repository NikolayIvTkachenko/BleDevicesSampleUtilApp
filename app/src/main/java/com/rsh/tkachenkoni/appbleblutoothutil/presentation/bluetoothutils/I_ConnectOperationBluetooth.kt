package com.rsh.tkachenkoni.appbleblutoothutil.presentation.bluetoothutils

import android.bluetooth.BluetoothDevice

/**
 *
 * Created by Nikolay Tkachenko
 * E-Mail: tkachenni@mail.ru
 */
interface I_ConnectOperationBluetooth {

    fun connectBluetoothDeviceData(device: BluetoothDevice?)

    fun disconnectBluetoothDeviceData(device: BluetoothDevice?)

}