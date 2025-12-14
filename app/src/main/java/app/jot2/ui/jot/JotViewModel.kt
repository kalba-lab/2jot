package app.jot2.ui.jot

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import app.jot2.data.Jot
import app.jot2.data.JotDao
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

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
            dao.update(jot.copy(text = newText.trim()))
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
