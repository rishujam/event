package ind.ev.events.db

import androidx.room.Database
import androidx.room.RoomDatabase
import ind.ev.events.models.Profile

@Database(
    entities = [Profile::class],
    version = 1
)
abstract class ProfileDatabase : RoomDatabase() {

    abstract fun getProfileDao(): ProfileDao

}