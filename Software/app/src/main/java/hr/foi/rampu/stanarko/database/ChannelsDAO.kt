package hr.foi.rampu.stanarko.database

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import hr.foi.rampu.stanarko.entities.Channel
import hr.foi.rampu.stanarko.entities.Chat
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import java.util.*

class ChannelsDAO {
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    var currentUser = FirebaseAuth.getInstance().currentUser
    private val currentUserMail = currentUser?.email.toString()
    private val ownersDAO = OwnersDAO()
    private val channelRef = db.collection("channels")

    suspend fun getChannel(id: String?) : Channel? {
        val channel = channelRef
            .whereArrayContains("id", id.toString())
            .get().await()
        val documents = channel.documents
        return documents[0].toObject(Channel::class.java)
    }

    fun getMessageQuery(channelId: String): Query {
        return channelRef.document(channelId).collection("messages")
            .whereNotEqualTo("timestamp", "")
            .orderBy("timestamp")
    }

    suspend fun getChannelID(mail: String?) : String {
        val channel = channelRef
            .whereArrayContains("participants", mail.toString())
            .get()
            .await()
        val documents = channel.documents
        return documents[0].id
    }

    private suspend fun updateChannelID(channelID: String){
        channelRef.document(channelID).update("id", channelID).await()
        channelRef.document(channelID).update("messages", FieldValue.delete()).await()
    }

    private suspend fun addNewMessage(channelID: String){
        val emptyMessage = hashMapOf<String, Any>()
        channelRef
            .document(channelID).collection("messages")
            .add(emptyMessage).await()
    }

    suspend fun addNewMessage(channelID: String, message: Chat){
        channelRef
            .document(channelID)
            .collection("messages")
            .add(message).await()
    }

    suspend fun isThereChannelWithOwner(mail: String?) : Boolean{
        val tenants = channelRef
            .whereArrayContains("participants", mail.toString())
            .get().await()
        return tenants.size() > 0
    }

    suspend fun createNewChannel(tenantMail: String?) : Boolean{
        if(!isThereChannelWithOwner(tenantMail)){
            val landlordMail = ownersDAO.getOwner(tenantMail.toString())?.mail
            if(landlordMail != null && landlordMail != ""){
                val participants = listOf(landlordMail, tenantMail.toString())
                val db = FirebaseFirestore.getInstance()
                val channel = channelRef
                val newChannel = Channel("", participants, Date(), emptyList())
                channel.add(newChannel)

                val channelID = runBlocking { getChannelID(tenantMail) }
                runBlocking { updateChannelID(channelID) }
                runBlocking { addNewMessage(channelID) }
                return true
            }
        }
        return false
    }

    suspend fun getLatestCreatedChannel() : Channel?{
        val channel = channelRef
            .orderBy("dateCreated", Query.Direction.DESCENDING)
            .limit(1)
            .get()
            .await()
        val documents = channel.documents
        return documents[0].toObject(Channel::class.java)
    }

    private fun getChatPartner(channel: Channel, currentUserMail: String): String {
        val participants = runBlocking { participantsNameSurname(channel) }

        return if (channel.participants[0] == currentUserMail) {
            participants[1]
        } else {
            participants[0]
        }
    }

    suspend fun participantsNameSurname(channel: Channel): List<String> {
        val participants = channel.participants
        val list = ArrayList<String>()
        val tenantsDAO = TenantsDAO()
        val ownersDAO = OwnersDAO()
        for (participant in participants){
            if(tenantsDAO.isUserTenant(participant)){
                val tenant = runBlocking {tenantsDAO.getTenant(participant)}
                list.add("${tenant?.name} ${tenant?.surname}")
            }else{
                val owner = runBlocking {ownersDAO.getOwnerInfo(participant)}
                list.add("${owner?.name} ${owner?.surname}")
            }
        }
        return list
    }

    fun getChatPartner(channel: Channel): String{
        val participants = runBlocking { participantsNameSurname(channel) }

        return if(channel.participants[0] == currentUserMail){
            participants[1]
        }else{
            participants[0]
        }
    }

    suspend fun getLastChannelMessage(channel: Channel): Chat?{
        val channel = channelRef.document(channel.id).collection("messages")
            .whereNotEqualTo("timestamp", null)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .limit(1)
            .get()
            .await()
        val documents = channel.documents
        return if(documents.size > 0){
            documents[0].toObject(Chat::class.java)
        }else{
            null
        }
    }
}