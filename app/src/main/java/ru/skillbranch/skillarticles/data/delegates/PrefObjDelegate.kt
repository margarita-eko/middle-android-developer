package ru.skillbranch.skillarticles.data.delegates

import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import ru.skillbranch.skillarticles.data.PrefManager
import ru.skillbranch.skillarticles.data.adapters.JsonAdapter
import ru.skillbranch.skillarticles.data.adapters.UserJsonAdapter
import ru.skillbranch.skillarticles.data.local.User
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class PrefObjDelegate<T>(private val adapter: JsonAdapter<T>, private val customKey: String? = null) {

    operator fun provideDelegate(
        thisRef: PrefManager,  prop: KProperty<*>
    ) : ReadWriteProperty<PrefManager, T?>{

        val key = createKey(customKey ?: prop.name, adapter)

        return object : ReadWriteProperty<PrefManager,T?> {
            private var _storedValue: T? = null
            override fun getValue(thisRef: PrefManager, property: KProperty<*>): T? {
                if (_storedValue == null){
                    //async flow
                    val flowValue = thisRef.dataStore.data
                        .map {
                                prefs -> prefs[key]
                        }
                    //sync read (on IO Dispatchers.IO and return on call thread
                    _storedValue = runBlocking(Dispatchers.IO) {
                        flowValue.first() ?: flowValue.first()
                        adapter.fromJson(flowValue.first() as String)
                    }

                }
                return _storedValue!!
            }

            override fun setValue(thisRef: PrefManager, property: KProperty<*>, value: T?) {
                _storedValue = value
                //set non blocking on coroutine
                thisRef.scope.launch {
                    thisRef.dataStore.edit { pref ->
                        pref[key] = adapter.toJson(value)
                    }
                }
            }

        }
    }

    private fun createKey(name: String, adapter: JsonAdapter<T>): Preferences.Key<String> {
        return when (adapter) {
            is UserJsonAdapter -> stringPreferencesKey(name)
            else -> error("This type can not be stored into Preferences")
        }.run { this as Preferences.Key<String> }
    }

}