package app.jot2.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "jots")
data class Jot(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val text: String,
    val createdAt: Long = System.currentTimeMillis(),
    val isPinned: Boolean = false
)
