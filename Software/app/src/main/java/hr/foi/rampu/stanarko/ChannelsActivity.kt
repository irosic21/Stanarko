package hr.foi.rampu.stanarko

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import hr.foi.rampu.stanarko.NavigationDrawer.OwnerDrawerActivity
import hr.foi.rampu.stanarko.adapters.ChannelAdapter
import hr.foi.rampu.stanarko.database.ChannelsDAO
import hr.foi.rampu.stanarko.database.TenantsDAO
import hr.foi.rampu.stanarko.databinding.ActivityChannelsBinding
import hr.foi.rampu.stanarko.entities.Channel
import hr.foi.rampu.stanarko.entities.Tenant
import hr.foi.rampu.stanarko.helpers.HelperClass
import kotlinx.coroutines.runBlocking

class ChannelsActivity : OwnerDrawerActivity() {
    override var currentUser = FirebaseAuth.getInstance().currentUser
    private val currentUserMail = currentUser?.email.toString()
    private val tenantsDAO = TenantsDAO()
    private val channelsDAO = ChannelsDAO()
    private val helperClass = HelperClass()

    private lateinit var binding: ActivityChannelsBinding

    private lateinit var db: FirebaseFirestore
    private lateinit var adapter: ChannelAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var btnCreateConversation: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChannelsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        allocateActivityTitle("Chat with tenants")

        db = FirebaseFirestore.getInstance()
        recyclerView = findViewById(R.id.rv_channels)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val query = db.collection("channels").whereArrayContains("participants", currentUserMail)
        adapter = ChannelAdapter(query, this, Channel::class.java)
        recyclerView.adapter = adapter
        adapter.startListening()

        val inflater = LayoutInflater.from(this)
        val view = inflater.inflate(R.layout.create_new_conversation_dialog, null)
        val spinner = view.findViewById<Spinner>(R.id.spn_chat_partner)
        val tenants = runBlocking {tenantsDAO.getUncontactedTenants(currentUserMail)}
        val spinnerAdapter = ArrayAdapter(this,android.R.layout.simple_spinner_item,tenants)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = spinnerAdapter

        btnCreateConversation = findViewById(R.id.fab_create_conversation)
        btnCreateConversation.setOnClickListener{
            AlertDialog.Builder(this)
                .setView(view)
                .setTitle("Create conversation")
                .setNeutralButton("Start conversation") { _, _ ->
                    removeAllViewsFromParent(view)
                    if(spinner.selectedItem != null){
                        val tenantMail = (spinner.selectedItem as Tenant).mail
                        val isCreated = runBlocking { channelsDAO.createNewChannel(tenantMail) }
                        if(isCreated){
                            val channel = runBlocking { channelsDAO.getLatestCreatedChannel() }
                            if (channel != null) {
                                Log.w("CHANNEL", channel.id)
                            }
                            val intent = Intent(this, ChatActivityOwner::class.java)
                            intent.putExtra("channel", channel?.id)
                            intent.putExtra("chatPartner", channel?.let { ch -> channelsDAO.getChatPartner(ch) })
                            startActivity(intent)
                            finish()
                        }else{
                            Toast.makeText(this,"Failed to create new conversation!", Toast.LENGTH_LONG).show()
                        }
                    }else{
                        Toast.makeText(this,"You have to choose person to talk to!", Toast.LENGTH_LONG).show()
                    }
                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    dialog.cancel()
                }
                .setOnCancelListener {
                    removeAllViewsFromParent(view)
                }
                .show()
        }

    }

    private fun removeAllViewsFromParent(view: View?) {
        val currentParent = (view?.parent) as ViewGroup
        currentParent.removeAllViews()
    }

    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }

    override fun onResume() {
        super.onResume()
        adapter.startListening()
        recyclerView.recycledViewPool.clear()
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        helperClass.navigateHomeScreen(this, currentUserMail)
        finish()
    }
}