package hr.foi.rampu.stanarko.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import hr.foi.rampu.stanarko.R
import hr.foi.rampu.stanarko.adapters.ContractRecycleViewAdapter
import hr.foi.rampu.stanarko.entities.Contract
import hr.foi.rampu.stanarko.entities.Tenant
import java.util.*
import kotlin.collections.ArrayList

class OwnerActiveContractsFragment : Fragment() {

    private lateinit var btnCreateContract: FloatingActionButton
    private lateinit var recyclerView: RecyclerView
    private lateinit var contractList: ArrayList<Contract>
    private var db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_owner_active_contracts, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recyclerView = view.findViewById(R.id.rv_owner_active_contracts)
        recyclerView.layoutManager = LinearLayoutManager(this.context)

        contractList = arrayListOf()
        db = FirebaseFirestore.getInstance()
        val userMail = FirebaseAuth.getInstance().currentUser?.email

        db.collection("contracts").whereEqualTo("tenant.flat.owner.mail",userMail).get()
            .addOnSuccessListener {
                if(!it.isEmpty){
                    for(data in it.documents){
                        val contract: Contract? =data.toObject(Contract::class.java)
                        if (contract!=null){
                            contractList.add(contract)
                        }
                    }
                    val today = Calendar.getInstance().time
                    val iterator = contractList.iterator()
                    while (iterator.hasNext()) {
                        val contract = iterator.next()
                        if (contract.expiringDate!!.before(today)) {
                            iterator.remove()
                        }
                    }
                    recyclerView.adapter = ContractRecycleViewAdapter(contractList)
                }
            }
            .addOnFailureListener {
                Toast.makeText(context, "Failed to do so", Toast.LENGTH_SHORT).show()
            }

        btnCreateContract = view.findViewById(R.id.create_new_contract_floating_button)
        btnCreateContract.setOnClickListener {
            showDialog()
        }
    }

    private fun showDialog() {
        val db = FirebaseFirestore.getInstance()
        val newContractDialogView = LayoutInflater.from(context).inflate(R.layout.new_contract_dialog,null)
        val etTenantMail = newContractDialogView.findViewById<EditText>(R.id.et_contract_tenant_email)
        val etExpiredDate = newContractDialogView.findViewById<CalendarView>(R.id.et_contract_expiring_date)

        var selectedDate = Calendar.getInstance().time

        etExpiredDate.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val calendar = Calendar.getInstance()
            calendar.set(year, month, dayOfMonth)
            selectedDate = calendar.time
        }

        Log.e("DATA","prije dijaloga")
        AlertDialog.Builder(context).setView(newContractDialogView).setTitle(getString(R.string.generate_new_contract_dialog))
            .setPositiveButton(getString(R.string.generate_new_contract_dialog)){ _, _ ->
                Log.e("DATA","unutar dijaloga")
                if (!TextUtils.isEmpty(etTenantMail.text.toString().trim())&&etExpiredDate.date!=0L){
                    Log.e("DATA","unutar nekog if-a")
                    db.collection("tenants").whereEqualTo("mail",etTenantMail.text.toString()).get()
                        .addOnSuccessListener { documents ->
                                Log.e("DATA","Tu sam opet")
                            if (documents.size()!=0){
                                for (document in documents){
                                    Log.e("DATA","Tu sam u foru")
                                    val tenant = document.toObject(Tenant::class.java)
                                    Log.e("DATA","Tu sam opet sa mailom "+tenant.mail)
                                    val contractsRef = db.collection("contracts")
                                    contractsRef.add(Contract(Calendar.getInstance().time, selectedDate,tenant))
                                }
                            }else{
                                Toast.makeText(context, getString(R.string.unable_to_generate_contract_message), Toast.LENGTH_SHORT).show()
                            }
                        }
                }
            }
            .show()
    }
}