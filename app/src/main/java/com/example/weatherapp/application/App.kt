package com.example.weatherapp.application

import android.app.Application
import com.example.weatherapp.database.AndroidDatabase
import com.facebook.stetho.Stetho

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        // Initialize Stetho
        Stetho.initializeWithDefaults(this)

        //Initialize database
        AndroidDatabase.initialize(this)
    }
}