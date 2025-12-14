package app.jot2

import android.app.Application
import app.jot2.data.JotDatabase
import app.jot2.data.SettingsManager

class JotApplication : Application() {
    
    val database: JotDatabase by lazy {
        JotDatabase.getDatabase(this)
    }
    
    val settingsManager: SettingsManager by lazy {
        SettingsManager(this)
    }
}
