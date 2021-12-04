package com.rsh.tkachenkoni.appbleblutoothutil.domain

import java.util.*

/**
 *
 * Created by Nikolay Tkachenko
 * E-Mail: tkachenni@mail.ru
 */
data class DeviceBle(
    val devName: String?,
    val devAddress: String?,
    val devAlias: String?,
    val devType: String?,
    val devBondState: String?,
    val devRSSI: String?
) : Comparable<DeviceBle>{

    override fun compareTo(other: DeviceBle): Int {
        if (devAddress.equals(other.devAddress)) return 0
        else return -1
    }

    override fun equals(other: Any?): Boolean {
        return devAddress?.equals((other as DeviceBle).devAddress) == true
    }

    override fun hashCode(): Int {
        return Objects.hash(devAddress);
    }
}