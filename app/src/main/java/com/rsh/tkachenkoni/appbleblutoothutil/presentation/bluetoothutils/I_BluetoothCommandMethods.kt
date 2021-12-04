package com.rsh.tkachenkoni.appbleblutoothutil.presentation.bluetoothutils

/**
 *
 * Created by Nikolay Tkachenko
 * E-Mail: tkachenni@mail.ru
 */
interface I_BluetoothCommandMethods {

    fun switchOnBluetooth()

    fun switchOffBluetooth()

    fun discoverDeviceBluetooth()

    fun pairedDeviceBluetooth()

    fun startBleScan()

    fun stopBleScan()

}