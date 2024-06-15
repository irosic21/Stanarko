package hr.foi.rampu.stanarko.adapters

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Query
import hr.foi.rampu.stanarko.ChatActivity
import hr.foi.rampu.stanarko.ChatActivityOwner
import hr.foi.rampu.stanarko.R
import hr.foi.rampu.stanarko.database.ChannelsDAO
import hr.foi.rampu.stanarko.database.TenantsDAO
import hr.foi.rampu.stanarko.entities.Channel
import kotlinx.coroutines.runBlocking
import java.text.SimpleDateFormat
import java.util.*

class ChannelAdapter(
    query: Query,
    private val context: Context,
    modelClass: Class<Channel>,
    private val onCreatedConversation: ((taskId: Int, dueMonth: Int, dueYear: Int) -> Unit)? = null) :
    FirestoreRecyclerAdapter<Channel, ChannelAdapter.ViewHolder>(
        FirestoreRecyclerOptions.Builder<Channel>()
            .setQuery(query, modelClass)
            .build()
    ) {

    private val currentUser = FirebaseAuth.getInstance().currentUser
    private val currentUserMail = currentUser?.email.toString()
    private val channelsDAO = ChannelsDAO()
    private val tenantsDAO = TenantsDAO()

    val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    override fun onBindViewHolder(holder: ViewHolder, position: Int, model: Channel) {
        if (position < itemCount) {
            holder.bind(model)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.channel_item, parent, false)
        return ViewHolder(view)
    }

    override fun onDataChanged() {
        super.onDataChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textViewChannelName : TextView
        private val textViewLastMessage : TextView
        private val textViewTimeOfLastMessage : TextView

        init {
            textViewTimeOfLastMessage = itemView.findViewById(R.id.textViewTimeOfLastMessage)
            textViewChannelName = itemView.findViewById(R.id.textViewChannelName)
            textViewLastMessage = itemView.findViewById(R.id.textViewLastMessage)

            itemView.setOnClickListener {
                val channel = snapshots.getSnapshot(adapterPosition)
                val isTenant = runBlocking { tenantsDAO.isUserTenant(currentUserMail) }
                val intent: Intent = if(isTenant){
                    Intent(itemView.context, ChatActivity::class.java)
                }else{
                    Intent(itemView.context, ChatActivityOwner::class.java)
                }

                intent.putExtra("channel", channel.id)
                intent.putExtra("chatPartner",
                    channel.toObject(Channel::class.java)?.let { ch -> channelsDAO.getChatPartner(ch) })

                itemView.context.startActivity(intent)
            }
        }

        fun bind(channel: Channel) {
            val lastMessage = runBlocking{channelsDAO.getLastChannelMessage(channel)}
            if(lastMessage?.timestamp != null){
                val timestamp = dateFormat.format(lastMessage?.timestamp)
                textViewTimeOfLastMessage.text = timestamp.toString()
                textViewTimeOfLastMessage.visibility = View.VISIBLE
            }else{
                textViewTimeOfLastMessage.text = ""
                textViewTimeOfLastMessage.visibility = View.INVISIBLE
                textViewTimeOfLastMessage.height = 0
            }

            textViewChannelName.text = channelsDAO.getChatPartner(channel)

            if(lastMessage?.message != null){
                textViewLastMessage.text = lastMessage?.message
                textViewLastMessage.visibility = View.VISIBLE
            }else{
                textViewLastMessage.text = ""
                textViewLastMessage.visibility = View.INVISIBLE
                textViewLastMessage.height = 0
            }

            if(lastMessage?.username != currentUserMail){
                textViewLastMessage.typeface = Typeface.defaultFromStyle(Typeface.BOLD_ITALIC)
            }else{
                textViewLastMessage.typeface = Typeface.defaultFromStyle(Typeface.ITALIC)
            }
        }
    }
}