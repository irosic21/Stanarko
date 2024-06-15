package hr.foi.rampu.stanarko

import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import hr.foi.rampu.stanarko.NavigationDrawer.TenantDrawerActivity
import hr.foi.rampu.stanarko.adapters.ChatAdapter
import hr.foi.rampu.stanarko.database.ChannelsDAO
import hr.foi.rampu.stanarko.databinding.ActivityChatBinding
import hr.foi.rampu.stanarko.entities.Chat
import hr.foi.rampu.stanarko.helpers.HelperClass
import kotlinx.coroutines.runBlocking
import java.util.*

class ChatActivity : TenantDrawerActivity() {
    private val currentUserMail = currentUser?.email.toString()
    private val channelsDAO = ChannelsDAO()
    private val helperClass = HelperClass()

    private lateinit var binding: ActivityChatBinding

    private lateinit var db: FirebaseFirestore
    private lateinit var query: Query
    private lateinit var adapter: ChatAdapter
    private lateinit var recyclerView: RecyclerView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = FirebaseFirestore.getInstance()
        val channelId = intent.getStringExtra("channel")
        val currentUserMail = currentUser?.email.toString()

        allocatedActivityTitle("Owner")

        if(channelId!=null){
            query = channelsDAO.getMessageQuery(channelId)
        }

        recyclerView = findViewById(R.id.rv_chats)
        val layoutManager = LinearLayoutManager(this)
        layoutManager.stackFromEnd = true
        recyclerView.layoutManager = layoutManager

        adapter = ChatAdapter(query, this, Chat::class.java)
        recyclerView.adapter = adapter

        val sendMessageButton = findViewById<ImageButton>(R.id.buttonSend)
        val messageEditText = findViewById<EditText>(R.id.editTextMessage)

        sendMessageButton.setOnClickListener {
            val messageText = messageEditText.text.toString()
            val chat = Chat(currentUserMail, messageText, Date())
            runBlocking { channelsDAO.addNewMessage(channelId.toString(), chat) }
            messageEditText.text.clear()
        }
    }

    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        helperClass.navigateToNextScreen(this, currentUserMail, ChannelsActivity::class.java)
        finish()
    }
}