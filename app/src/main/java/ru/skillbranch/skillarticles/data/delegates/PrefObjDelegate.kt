package ru.skillbranch.skillarticles.data.delegates

import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import ru.skillbranch.skillarticles.data.PrefManager
import ru.skillbranch.skillarticles.data.adapters.JsonAdapter
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * Type description here....
 *
 * Created by Andrey on 20.05.2021
 */
class PrefObjDelegate<T>(
    private val adapter: JsonAdapter<T>,
    private val customKey: String? = null
) :
    ReadWriteProperty<PrefManager, T?> {

    private var _storedValue: T? = null

    override fun setValue(thisRef: PrefManager, property: KProperty<*>, value: T?) {
        _storedValue = value
        val key = stringPreferencesKey(property.name)
        thisRef.scope.launch {
            thisRef.dataStore.edit { prefs ->
                prefs[key] = adapter.toJson(value)
            }
        }
    }

    override fun getValue(thisRef: PrefManager, property: KProperty<*>): T? {
        val key = stringPreferencesKey(property.name)
        if (_storedValue == null) {
            val flowValue = thisRef.dataStore.data
                .map { prefs ->
                    prefs[key]
                }
            _storedValue = runBlocking(Dispatchers.IO) {
                adapter.fromJson(flowValue.first() ?: "")
            }
        }
        return _storedValue
    }
}
