package ru.skillbranch.skillarticles.data

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.zip
import ru.skillbranch.skillarticles.App
import ru.skillbranch.skillarticles.data.delegates.PrefDelegate

/**
 * Type description here....
 *
 * Created by Andrey on 10.05.2021
 */

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class PrefManager(context: Context = App.applicationContext()) {

    val dataStore = context.dataStore

    private val errHandler = CoroutineExceptionHandler { _, throwable ->
        Log.e("PrefManager", "err ${throwable.message}")
    }

    val scope = CoroutineScope(SupervisorJob() + errHandler)

    var isBigText by PrefDelegate(false)
    var isDarkMode by PrefDelegate(false)

    val settings: LiveData<AppSettings>
        get() {
            val isBig =
                dataStore.data.map { it[booleanPreferencesKey(this::isBigText.name)] ?: false }
            val isDark =
                dataStore.data.map { it[booleanPreferencesKey(this::isDarkMode.name)] ?: false }

            return isDark.zip(isBig) { dark, big -> AppSettings(dark, big) }
                .onEach { Log.e("PrefManager", "settings $it") }
                .distinctUntilChanged()
                .asLiveData()

        }

}