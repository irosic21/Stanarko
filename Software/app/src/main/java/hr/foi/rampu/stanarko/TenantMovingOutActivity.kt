package hr.foi.rampu.stanarko

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CalendarView
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import hr.foi.rampu.stanarko.NavigationDrawer.TenantDrawerActivity
import hr.foi.rampu.stanarko.database.TenantsDAO
import hr.foi.rampu.stanarko.databinding.ActivityTenantMovingOutBinding
import hr.foi.rampu.stanarko.entities.Tenant
import java.text.SimpleDateFormat
import java.util.*
import kotlin.time.Duration.Companion.milliseconds

class TenantMovingOutActivity : TenantDrawerActivity() {

    private  lateinit var binding: ActivityTenantMovingOutBinding
    private lateinit var btnSaveDate: Button
    private lateinit var tvMovingDate: TextView
    private lateinit var tvDaysUntilMovingDate: TextView
    private lateinit var cvMovingOut: CalendarView
    private lateinit var dateFormat: SimpleDateFormat
    private var today = Calendar.getInstance().time
    private var tenant = TenantsDAO()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTenantMovingOutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        allocatedActivityTitle(getString(R.string.moving_out_title))

        btnSaveDate = findViewById(R.id.btn_save_moving_out)
        tvMovingDate = findViewById(R.id.tv_moving_out)
        cvMovingOut = findViewById(R.id.cv_tenant_moving_out)
        tvDaysUntilMovingDate = findViewById(R.id.tv_days_until_moving_out)
        dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.US)
        val userMail = FirebaseAuth.getInstance().currentUser!!.email

        saveDateToDataBase(userMail)
        loadDataToTextView(userMail)
    }

    private fun loadDataToTextView(userMail: String?) {
        tenant.getTenantByMail(userMail!!)
            .addOnSuccessListener {
                if(!it.isEmpty){
                    val document = it.documents.first().toObject(Tenant::class.java)
                    if (document!!.dateOfMovingOut!=null){
                        loadDataToView(document.dateOfMovingOut)
                    }else{
                        tvMovingDate.text = getString(R.string.date_of_moving_out_doesnt_exist_message)
                    }
                }
            }
            .addOnFailureListener{ e ->
                Log.e("Error message: ","Couldn't retrieve user")
            }
    }

    private fun saveDateToDataBase(userMail: String?) {

        var selectedDate = Calendar.getInstance().time
        cvMovingOut.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val calendar = Calendar.getInstance()
            calendar.set(year, month, dayOfMonth)
            selectedDate = calendar.time
        }

        val calendar = Calendar.getInstance()
        calendar.time = today
        calendar.add(Calendar.DATE,30)
        val future30 = calendar.time

        btnSaveDate.setOnClickListener {
            tenant.getTenantByMail(userMail!!)
                .addOnSuccessListener {
                    if (selectedDate.after(future30)){
                        if(!it.isEmpty){
                            val document = it.documents.first()
                            document.reference.update("dateOfMovingOut",selectedDate)
                                .addOnSuccessListener {
                                    loadDataToView(selectedDate)
                                    //moveOutFromFlat(userMail)
                                    Log.e("DATA","Uspijesno dodan datum: "+dateFormat.format(selectedDate))
                                }
                                .addOnFailureListener { e->
                                    Toast.makeText(this.baseContext, "Error: "+e.message, Toast.LENGTH_SHORT).show()
                                }
                        }
                    }else{
                        Toast.makeText(baseContext, "Chosen date must be 30 days from now", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e->
                    Toast.makeText(this.baseContext, "Error: "+e.message, Toast.LENGTH_SHORT).show()
                }
        }
    }


    private fun loadDataToView(selectedDate: Date?) {
        tvMovingDate.text = dateFormat.format(selectedDate!!)
        val difference = selectedDate.time.milliseconds - today.time.milliseconds
        tvDaysUntilMovingDate.text = difference.inWholeDays.toString()
    }
}