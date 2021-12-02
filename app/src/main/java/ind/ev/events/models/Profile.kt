package ind.ev.events.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName =  "profile"
)
data class Profile(
    @PrimaryKey
    val email:String="",
    val name:String="",
    val accType:String="",
    val number:String ="",
    val verify:Boolean=false,
//    val myEvents:List<String>
)
