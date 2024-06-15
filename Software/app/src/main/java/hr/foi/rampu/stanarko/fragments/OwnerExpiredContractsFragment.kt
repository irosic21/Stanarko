package hr.foi.rampu.stanarko.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import hr.foi.rampu.stanarko.R
import hr.foi.rampu.stanarko.adapters.ContractRecycleViewAdapter
import hr.foi.rampu.stanarko.entities.Contract
import java.util.Calendar

class OwnerExpiredContractsFragment : Fragment() {

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
        return inflater.inflate(R.layout.fragment_owner_expired_contracts, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recyclerView = view.findViewById(R.id.rv_owner_expired_contracts)
        recyclerView.layoutManager = LinearLayoutManager(this.context)

        contractList = arrayListOf()
        db = FirebaseFirestore.getInstance()
        val userMail = FirebaseAuth.getInstance().currentUser?.email

        db.collection("contracts").whereEqualTo("tenant.flat.owner.mail",userMail).get()
            .addOnSuccessListener {
                if(!it.isEmpty){
                    for (data in it.documents){
                        val contract: Contract? = data.toObject(Contract::class.java)
                        if(contract!=null){
                            contractList.add(contract)
                        }
                    }
                    val today = Calendar.getInstance().time
                    val iterator = contractList.iterator()
                    while (iterator.hasNext()){
                        val contract = iterator.next()
                        if(contract.expiringDate!!.after(today)){
                            iterator.remove()
                        }
                    }
                    recyclerView.adapter = ContractRecycleViewAdapter(contractList)
                }
            }
            .addOnFailureListener {
                Toast.makeText(context, "Failed to load recycleView items", Toast.LENGTH_SHORT).show()
            }
    }
}