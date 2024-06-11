package led.server.ledcontroller.backend

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class Settings(private val context: Context) {

    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("userToken")
        val URL_KEY = stringPreferencesKey("URL_KEY")
        val NUM_LEDS = intPreferencesKey("NUM_LEDS_KEY")
    }

    fun <T> getAccessToken(key: Preferences.Key<T>): Flow<T?> {
        return context.dataStore.data.map { preferences ->
            preferences[key]
        }
    }

    suspend fun <T> saveToken(key: Preferences.Key<T>, token: T) {
        context.dataStore.edit { preferences ->
            preferences[key] = token
        }
    }

}