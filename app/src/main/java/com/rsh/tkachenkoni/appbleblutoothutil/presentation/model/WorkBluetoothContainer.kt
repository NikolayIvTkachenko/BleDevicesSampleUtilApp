package com.rsh.tkachenkoni.appbleblutoothutil.presentation.model

/**
 *
 * Created by Nikolay Tkachenko
 * E-Mail: tkachenni@mail.ru
 */
object WorkBluetoothContainer {

    private val wvb: WorkVariableBluetooth

    init {
        wvb = WorkVariableBluetooth()
    }

    fun getWorkVariableBluetooth(): WorkVariableBluetooth{
        return wvb
    }
}