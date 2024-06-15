package hr.foi.rampu.stanarko

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import hr.foi.rampu.stanarko.NavigationDrawer.OwnerDrawerActivity
import hr.foi.rampu.stanarko.adapters.FlatsAdapter
import hr.foi.rampu.stanarko.databinding.ActivityMainBinding
import hr.foi.rampu.stanarko.helpers.MockDataLoader
import kotlinx.coroutines.runBlocking


class MainActivity : OwnerDrawerActivity() {

    lateinit var binding: ActivityMainBinding
    private lateinit var recyclerView: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        allocateActivityTitle(getString(R.string.menu_home))

        val mail = FirebaseAuth.getInstance().currentUser?.email
        recyclerView = findViewById(R.id.rv_flat_list)
        recyclerView.adapter =
            runBlocking { FlatsAdapter(MockDataLoader.getFirebaseFlatsByOwner(mail!!)) }
        recyclerView.layoutManager = LinearLayoutManager(this)

    }
}