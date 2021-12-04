package com.rsh.tkachenkoni.appbleblutoothutil

/**
 *
 * Created by Nikolay Tkachenko
 * E-Mail: tkachenni@mail.ru
 */
object AppConstants {

    const val ID_PERSON = "id_person"
    const val ACTION_RECIEVER_SEND_RECIEVE_DATA = "com.rsh.tkachenkoni.industrymeteranalizer.reciever.send_recieve_data"
    const val ACTION_RECIEVER_SHOW_MESSAGE = "com.rsh.tkachenkoni.industrymeteranalizer.reciever.show_message"
    const val ACTION_RECIEVER_START_SCAN = "com.rsh.tkachenkoni.industrymeteranalizer.reciever.start_scan"
    const val ACTION_RECIEVER_STOP_SCAN = "com.rsh.tkachenkoni.industrymeteranalizer.reciever.stop_scan"
    const val ACTION_RECIEVER_SWITCH_ON_BLUETOOTH = "com.rsh.tkachenkoni.industrymeteranalizer.reciever.switch_on_bluetooth"
    const val ACTION_RECIEVER_SWITCH_OFF_BLUETOOTH = "com.rsh.tkachenkoni.industrymeteranalizer.reciever.switch_off_bluetooth"
    const val ACTION_RECIEVER_DISCOVER_DEVICE = "com.rsh.tkachenkoni.industrymeteranalizer.reciever.discover_device"
    const val ACTION_RECIEVER_PAIRED_DEVICES_SHOW = "com.rsh.tkachenkoni.industrymeteranalizer.reciever.paired_devices_show"
    const val ACTION_DEVICE_CONNECTED = "com.rsh.tkachenkoni.industrymeteranalizer.reciever.connect_device"
    const val ACTION_DEVICE_DISCONNECTED = "com.rsh.tkachenkoni.industrymeteranalizer.reciever.disconnect_device"

    const val INTENT_MESSAGE_STRING = "MESSAGE_INTENT"
    const val INTENT_ADDRESS_STRING = "ADDRES_INTENT"

    const val STATE_DISCONNECTED = 0
    const val STATE_CONNECTING = 1
    const val STATE_CONNECTED = 2

    const val TAG = "BLESCANNER_WORK"

}