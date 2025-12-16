package app.jot2.ui.jot

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import app.jot2.data.Jot
import app.jot2.data.JotDao
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
data class JotBackup(
    val id: Long,
    val text: String,
    val createdAt: Long,
    val isPinned: Boolean
)

class JotViewModel(private val dao: JotDao) : ViewModel() {

    val jots = dao.getAllJots()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addJot(text: String) {
        if (text.isBlank()) return
        viewModelScope.launch {
            dao.insert(Jot(text = text.trim()))
        }
    }

    fun deleteJot(jot: Jot) {
        viewModelScope.launch {
            dao.delete(jot)
        }
    }

    fun pinJot(jot: Jot) {
        viewModelScope.launch {
            dao.setPinned(jot.id)
        }
    }

    fun unpinJot(jot: Jot) {
        viewModelScope.launch {
            dao.update(jot.copy(isPinned = false))
        }
    }

    fun updateJot(jot: Jot, newText: String) {
        viewModelScope.launch {
            val currentJot = jots.value.find { it.id == jot.id }
            if (currentJot != null) {
                dao.update(currentJot.copy(text = newText.trim()))
            }
        }
    }

    fun exportToJson(): String {
        val backupList = jots.value.map { jot ->
            JotBackup(jot.id, jot.text, jot.createdAt, jot.isPinned)
        }
        return Json.encodeToString(backupList)
    }

    fun importFromJson(jsonString: String): Boolean {
        return try {
            val backupList = Json.decodeFromString<List<JotBackup>>(jsonString)
            viewModelScope.launch {
                dao.deleteAll()
                backupList.forEach { backup ->
                    dao.insert(Jot(
                        id = backup.id,
                        text = backup.text,
                        createdAt = backup.createdAt,
                        isPinned = backup.isPinned
                    ))
                }
            }
            true
        } catch (e: Exception) {
            false
        }
    }
}

class JotViewModelFactory(private val dao: JotDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(JotViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return JotViewModel(dao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}