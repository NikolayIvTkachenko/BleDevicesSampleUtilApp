package com.rsh.tkachenkoni.appbleblutoothutil

import android.app.Application
import android.content.Context

/**
 *
 * Created by Nikolay Tkachenko
 * E-Mail: tkachenni@mail.ru
 */
class App: Application() {

    companion object{
        lateinit var instance: App
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    val Context.app: App
        get() = applicationContext as App

}