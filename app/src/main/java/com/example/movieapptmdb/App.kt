package com.example.movieapptmdb

import android.app.Application
import com.example.tmdb_sdk.configuration.ApiKeyConfiguration
import com.example.tmdb_sdk.configuration.TMDbKey

class App: Application() {

    override fun onCreate() {
        super.onCreate()
        initTMDbSdk()
    }

    private fun initTMDbSdk(){
        val configuration = ApiKeyConfiguration()
        configuration.setApiKey("YOUR_KEY_HERE")
        TMDbKey.instance.init(applicationContext, configuration)
    }
}