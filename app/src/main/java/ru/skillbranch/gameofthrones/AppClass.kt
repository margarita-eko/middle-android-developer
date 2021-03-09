package ru.skillbranch.gameofthrones

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import ru.skillbranch.gameofthrones.database.AppDatabase


class AppClass: Application() {

    var isNetworkAvailable = false

    companion object {
        var db: AppDatabase? = null

        private var instance:AppClass? = null

        fun getDatabase(): AppDatabase? {
            return db
        }

        fun appliactionContext(): Context {
            return instance!!.applicationContext
        }

    }

    init {
        instance = this
    }

    override fun onCreate() {
        super.onCreate()
        db= AppDatabase.getDatabase(applicationContext)
        isNetworkAvailable = networkAvailableCheck()
    }

    private fun networkAvailableCheck(): Boolean {
        val connectivityManager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connectivityManager != null) {
            val capabilities =
                    connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (capabilities != null) {
                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR")
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_WIFI")
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET")
                    return true
                }
            }
        }
        return false
    }

}