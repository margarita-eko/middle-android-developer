package ru.skillbranch.gameofthrones

import android.app.Application
import android.content.Context
import ru.skillbranch.gameofthrones.database.AppDatabase

class AppClass: Application() {

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
    }
}