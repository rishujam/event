package ind.ev.events.repository

import ind.ev.events.db.OtpApi
import ind.ev.events.db.ProfileDao
import ind.ev.events.db.ProfileDatabase
import ind.ev.events.firebase.FirebaseSource
import ind.ev.events.models.Event
import ind.ev.events.models.Profile
import javax.inject.Inject

class Repository @Inject constructor(
    private val source: FirebaseSource,
    private val db: ProfileDao,
    private val api:OtpApi
) {

    suspend fun getProfile(email:String,collection:String)=
        source.getProfile(email, collection)

    suspend fun saveProfile(collection:String, profile: Profile, email:String)=
        source.saveProfile(collection, profile, email)

    suspend fun saveProfileLocal(profile: Profile)=
        db.upsert(profile)

    suspend fun getProfileLocal()=
        db.getData()

    suspend fun deleteLocalProfile()=
        db.deleteData()

    suspend fun sendEventForVerification(collection: String, event: Event)=
        source.sendEventForVerification(collection, event)


    suspend fun checkOtpBalance(key:String)=
        api.getBalance(key)

    suspend fun getOtp(key:String,number:String)=
        api.getOtp(key, number)

    suspend fun verifyOtp(key: String,id:String,otp:String)=
        api.verifyOtp(key, id, otp)

    suspend fun updateOrganizerVerified(collection: String,verify:Boolean,email:String,number: String)=
        source.updateOrganizerVerified(collection, verify, email,number)

    suspend fun updateLastEventId(collection: String,id:String)=
        source.updateLastEventId(collection, id)

    suspend fun getLastEventId(collection: String) =
        source.getLastEventId(collection)

    suspend fun searchEvents(collection: String,key: String)=
        source.searchEvents(collection,key)

    suspend fun getFeaturedEvents(collection: String)=
        source.getFeaturedEvents(collection)

    suspend fun showEvent(collection: String,id:String)=
        source.showEvent(collection, id)

    suspend fun uploadRegForm(collection: String,eventId:String,list:List<String>)=
        source.uploadRegForm(collection, eventId, list)

    suspend fun getRegForm(collection: String,eventId: String)=
        source.getRegForm(collection, eventId)

    suspend fun addToParticipantLis(collection: String,eventId: String,usermail:String)=
        source.addToParticipantLis(collection, eventId, usermail)

    suspend fun uploadFormData(collection: String,eventId: String,data:List<String>,usermail:String,time:String)=
        source.uploadFormData(collection, eventId, data, usermail,time)

    suspend fun getRegisteredUserForEvent(collection: String,eventId: String)=
        source.getRegisteredUserForEvent(collection, eventId)

    suspend fun getEventsOfOrg(collection: String,orgEmail:String)=
        source.getEventsOfOrg(collection, orgEmail)

    suspend fun getEventsForParticipant(collection: String,user:String)=
        source.getEventsForParticipant(collection, user)

}