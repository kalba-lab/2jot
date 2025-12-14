package app.jot2.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface JotDao {
    
    @Query("SELECT * FROM jots ORDER BY createdAt DESC")
    fun getAllJots(): Flow<List<Jot>>
    
    @Query("SELECT * FROM jots WHERE isPinned = 1 LIMIT 1")
    fun getPinnedJot(): Flow<Jot?>
    
    @Insert
    suspend fun insert(jot: Jot): Long
    
    @Update
    suspend fun update(jot: Jot)
    
    @Delete
    suspend fun delete(jot: Jot)
    
    @Query("UPDATE jots SET isPinned = 0")
    suspend fun unpinAll()
    
    @Query("UPDATE jots SET isPinned = 1 WHERE id = :id")
    suspend fun pinJot(id: Long)
    
    @Transaction
    suspend fun setPinned(id: Long) {
        unpinAll()
        pinJot(id)
    }
}
