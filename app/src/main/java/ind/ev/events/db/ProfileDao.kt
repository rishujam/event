package ind.ev.events.db

import androidx.room.*
import ind.ev.events.models.Profile

@Dao
interface ProfileDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(userInfo: Profile):Long

    @Query("SELECT * FROM profile")
    suspend fun getData():List<Profile>

    @Query("DELETE FROM profile")
    suspend fun deleteData()
}