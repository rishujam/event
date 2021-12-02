package ind.ev.events.firebase

import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ind.ev.events.models.Event
import ind.ev.events.models.Profile
import kotlinx.coroutines.tasks.await

class FirebaseSource {

    private val ref = Firebase.firestore

    suspend fun saveProfile(collection:String, profile: Profile, email:String){
        ref.collection(collection).document(email).set(profile).await()
    }

    suspend fun getProfile(email:String,collection: String): Profile {
        val query = ref.collection(collection).document(email).get().await()
        return query.toObject<Profile>()!!
    }

    suspend fun sendEventForVerification(collection: String,event:Event){
        ref.collection(collection).document(event.id).set(event).await()
    }

    suspend fun searchEvents(collection: String,keyword:String):List<Event>{
        val list = mutableListOf<Event>()
        val keywordList = keyword.split(" ").toList()
        val query = ref.collection(collection).get().await()
        for(doc in query){
            if(doc["verified"]==true){
                val string = doc["title"].toString()+ doc["description"].toString()
                val list1 = string.split(" ").toList()
                keywordList.forEach {
                    if(list1.contains(it)){
                        val event = doc.toObject<Event>()
                        list.add(event)
                    }
                } //TODO  update this function//
            }
        }
        return list
    }

    suspend fun getFeaturedEvents(collection: String):List<Event>{
        val list = mutableListOf<Event>()
        val query = ref.collection(collection).get().await()
        for (doc in query){
            if (doc["featured"]==true && doc["verified"]==true){
                val event = doc.toObject<Event>()
                list.add(event)
            }
        }
        return list
    }

    suspend fun showEvent(collection: String,id:String):Event{
        val event = ref.collection(collection).document(id).get().await()
        return event.toObject<Event>()!!
    }

    suspend fun updateLastEventId(collection: String,id:String) {
        ref.collection(collection).document("currentId").set(mapOf("id" to id)).await()
    }

    suspend fun getLastEventId(collection: String):String {
        val query = ref.collection(collection).document("currentId").get().await()
        return query["id"].toString()
    }

    suspend fun updateOrganizerVerified(collection: String,verify:Boolean,email:String,number:String){
        ref.collection(collection).document(email).update(mapOf("verify" to verify, "number" to number)).await()
    }

    suspend fun uploadRegForm(collection: String,eventId:String,list:List<String>){
        ref.collection(collection).document(eventId).set(mapOf(eventId to list)).await()
    }

    suspend fun getRegForm(collection: String,eventId: String):List<String>{
        val query = ref.collection(collection).document(eventId).get().await()
        return query[eventId] as List<String>
    }

    suspend fun addToParticipantLis(collection: String,eventId: String,usermail:String){
        ref.collection(collection).document(eventId).set(mapOf(usermail to usermail), SetOptions.merge()).await()
    }

    suspend fun uploadFormData(collection: String,eventId: String,data:List<String>,usermail:String,time:String){
        ref.collection(collection).document(usermail).set(mapOf(eventId to data,"time" to time)).await()
    }

    suspend fun getRegisteredUserForEvent(collection: String,eventId: String):List<String>{
        val query= ref.collection(collection).document(eventId).get().await()
        return query.data?.keys?.toList()!!
    }

    suspend fun getEventsOfOrg(collection: String,orgEmail:String):List<Event>{
        val list = ArrayList<Event>()
        val query = ref.collection(collection).get().await()
        for(doc in query){
            if(doc["organizer"]==orgEmail){
                val obj = doc.toObject<Event>()
                list.add(obj)
            }
        }
        return list
    }

    suspend fun getEventsForParticipant(collection: String,user:String):List<Event>{
        val list = ArrayList<Event>()
        val query = ref.collection(collection).document(user).get().await()
        val eventIds =query.data?.keys?.toList()
        if(eventIds!=null){
            if(eventIds.isEmpty()){
                return list
            }else{
                for(i in eventIds){
                    val event = showEvent("events",i)
                    list.add(event)
                }
            }
        }
        return list
    }

}