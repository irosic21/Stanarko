package hr.foi.rampu.stanarko.NavigationDrawer

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import android.view.View
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import hr.foi.rampu.stanarko.*
import hr.foi.rampu.stanarko.F02_Prijava.Prijava

open class OwnerDrawerActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    open var currentUser = FirebaseAuth.getInstance().currentUser
    lateinit var drawerLayout: DrawerLayout
    override fun setContentView(view: View?) {
        drawerLayout = layoutInflater.inflate(R.layout.activity_owner_drawer,null) as DrawerLayout
        val container: FrameLayout = drawerLayout.findViewById(R.id.activity_container)
        container.addView(view)
        super.setContentView(drawerLayout)
        val toolbar: Toolbar = drawerLayout.findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        val navView: NavigationView = drawerLayout.findViewById(R.id.nav_view_owner)
        navView.setNavigationItemSelectedListener(this)

        val toggle = ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.drawer_open,R.string.drawer_close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.menu_log_out_owner ->{
                if(currentUser!=null){
                    FirebaseAuth.getInstance().signOut()
                    currentUser = FirebaseAuth.getInstance().currentUser
                    if(currentUser== null){
                        val intent = Intent(this,Prijava::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                    }else{
                        Toast.makeText(this,getString(R.string.failed_to_log_out_message),Toast.LENGTH_SHORT).show()
                    }
                }
            }
            R.id.menu_rents_owner -> {
                val intent = Intent(this, RentManagerActivityOwner::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                intent.putExtra("mail", currentUser?.email)
                startActivity(intent)
            }
            R.id.menu_chat_owner -> {
                val intent = Intent(this, ChannelsActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                intent.putExtra("mail", currentUser?.email)
                startActivity(intent)
            }
            R.id.menu_owner_contracts ->{
                val intent = Intent(this,OwnerContractManagerActivity::class.java)
                startActivity(intent)
            }
            R.id.menu_owner_starting_activity ->{
                val intent = Intent(this,MainActivity::class.java)
                startActivity(intent)
            }
            R.id.menu_add_flat_owner ->{
                val intent = Intent(this,AddFlatActivity::class.java)
                startActivity(intent)
            }
        }
        return false
    }

    protected fun allocateActivityTitle(title: String){
        supportActionBar?.let {it.title = title}
    }
}