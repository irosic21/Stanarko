package hr.foi.rampu.stanarko.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.Query
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import hr.foi.rampu.stanarko.R
import hr.foi.rampu.stanarko.entities.Chat
import java.text.SimpleDateFormat
import java.util.*


class ChatAdapter(query: Query, private val context: Context, modelClass: Class<Chat>) :
    FirestoreRecyclerAdapter<Chat, ChatAdapter.ViewHolder>(
        FirestoreRecyclerOptions.Builder<Chat>()
            .setQuery(query, modelClass)
            .build()
    ) {

    private val currentUser = FirebaseAuth.getInstance().currentUser
    private val currentUserMail = currentUser?.email.toString()

    val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var layout = R.layout.chat_item_left
        if (viewType == 0) {
            layout = R.layout.chat_item_right
        }
        val view = LayoutInflater.from(parent.context)
            .inflate(layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, model: Chat) {
        holder.bind(model)
    }

    override fun onDataChanged() {
        super.onDataChanged()
    }

    override fun getItemViewType(position: Int): Int {
        val chat = getItem(position)
        return if (chat.username == currentUserMail) 0 else 1
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(chat: Chat) {
            itemView.findViewById<TextView>(R.id.textViewMessage).text = chat.message
            val timestamp = dateFormat.format(chat.timestamp)
            itemView.findViewById<TextView>(R.id.textViewTime).text = timestamp
        }
    }
}