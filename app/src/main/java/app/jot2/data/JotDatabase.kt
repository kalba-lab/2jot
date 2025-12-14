package app.jot2.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Jot::class], version = 1, exportSchema = false)
abstract class JotDatabase : RoomDatabase() {
    
    abstract fun jotDao(): JotDao
    
    companion object {
        @Volatile
        private var INSTANCE: JotDatabase? = null
        
        fun getDatabase(context: Context): JotDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    JotDatabase::class.java,
                    "jot_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
