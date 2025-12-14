package app.jot2.data

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SettingsManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("2jot_settings", Context.MODE_PRIVATE)

    private val _colorIndex = MutableStateFlow(prefs.getInt(KEY_COLOR_INDEX, 0))
    val colorIndex: StateFlow<Int> = _colorIndex

    private val _pinColorIndex = MutableStateFlow(prefs.getInt(KEY_PIN_COLOR_INDEX, 0))
    val pinColorIndex: StateFlow<Int> = _pinColorIndex

    fun setColorIndex(index: Int) {
        prefs.edit().putInt(KEY_COLOR_INDEX, index).apply()
        _colorIndex.value = index
    }

    fun setPinColorIndex(index: Int) {
        prefs.edit().putInt(KEY_PIN_COLOR_INDEX, index).apply()
        _pinColorIndex.value = index
    }

    companion object {
        private const val KEY_COLOR_INDEX = "color_index"
        private const val KEY_PIN_COLOR_INDEX = "pin_color_index"
    }
}