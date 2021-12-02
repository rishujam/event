package ind.ev.events.models

data class Event(
    val title:String="",
    val description:String="",
    val location:String="",
    val date:String="",
    val time:String="",
    val skillReq:String="",
    val fees:String="",
    val category:String="",
    val timeStamp:String="",
    val verified:Boolean=false,
    val organizer:String="",
    val featured:Boolean=false,
    val id:String=""

)
