package ind.ev.events

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import ind.ev.events.db.ProfileDao
import ind.ev.events.db.ProfileDatabase
import ind.ev.events.firebase.FirebaseSource
import ind.ev.events.models.Event
import ind.ev.events.models.Profile
import ind.ev.events.repository.Repository
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: Repository
) :ViewModel(){

    suspend fun saveProfile(collection:String, profile: Profile, email:String)=
        repository.saveProfile(collection, profile, email)

    suspend fun getProfile(email:String, collection:String)=
        repository.getProfile(email, collection)

    suspend fun saveProfileLocal(profile: Profile)=
        repository.saveProfileLocal(profile)

    suspend fun getProfileLocal()=
        repository.getProfileLocal()

    suspend fun deleteLocalProfile()=
        repository.deleteLocalProfile()

    suspend fun sendEventForVerification(collection: String, event: Event)=
        repository.sendEventForVerification(collection, event)

    suspend fun checkOtpBalance(key:String)=
        repository.checkOtpBalance(key)

    suspend fun getOtp(key:String,number:String)=
        repository.getOtp(key, number)

    suspend fun verifyOtp(key: String,id:String,otp:String)=
        repository.verifyOtp(key, id, otp)

    suspend fun updateOrganizerVerified(collection: String,verify:Boolean,email:String,number: String)=
        repository.updateOrganizerVerified(collection,verify, email,number)

    suspend fun updateLastEventId(collection: String,id:String)=
        repository.updateLastEventId(collection, id)

    suspend fun getLastEventId(collection: String) =
        repository.getLastEventId(collection)

    suspend fun searchEvents(collection: String,key: String)=
        repository.searchEvents(collection,key)

    suspend fun getFeaturedEvents(collection: String)=
        repository.getFeaturedEvents(collection)

    suspend fun showEvent(collection: String,id:String)=
        repository.showEvent(collection, id)

    suspend fun getRegForm(collection: String,eventId: String)=
        repository.getRegForm(collection,eventId)

    suspend fun uploadRegForm(collection: String,eventId:String,list:List<String>)=
        repository.uploadRegForm(collection, eventId, list)

    suspend fun addToParticipantLis(collection: String,eventId: String,usermail:String)=
        repository.addToParticipantLis(collection, eventId, usermail)

    suspend fun uploadFormData(collection: String,eventId: String,data:List<String>,usermail:String,time:String)=
        repository.uploadFormData(collection, eventId, data, usermail,time)

    suspend fun getRegisteredUserForEvent(collection: String,eventId: String)=
        repository.getRegisteredUserForEvent(collection, eventId)

    suspend fun getEventsOfOrg(collection: String,orgEmail:String)=
        repository.getEventsOfOrg(collection, orgEmail)

    suspend fun getEventsForParticipant(collection: String,user:String)=
        repository.getEventsForParticipant(collection, user)
}

