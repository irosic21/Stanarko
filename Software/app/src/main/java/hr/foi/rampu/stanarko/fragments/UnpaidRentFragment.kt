package hr.foi.rampu.stanarko.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import hr.foi.rampu.stanarko.R
import hr.foi.rampu.stanarko.adapters.RentsAdapter
import hr.foi.rampu.stanarko.database.RentsDAO
import hr.foi.rampu.stanarko.database.TenantsDAO
import hr.foi.rampu.stanarko.entities.Rent
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await


class UnpaidRentFragment : Fragment() {
    private val currentUser = FirebaseAuth.getInstance().currentUser
    private val currentUserMail = currentUser?.email.toString()

    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_unpaid_rent, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val rentsDAO = RentsDAO()
        val tenantsDAO = TenantsDAO()
        val isTenant = runBlocking {tenantsDAO.isUserTenant(currentUserMail)}

        if(isTenant){
            rentsDAO.getAllRentsByMail(currentUserMail,false)
                .addOnSuccessListener { snapshot ->
                    val rents = snapshot.toObjects(Rent::class.java)
                    val rentsAdapter = RentsAdapter(rents.toMutableList()) { rentId, dueMonth, dueYear ->
                        val bundle = Bundle()
                        val additionalValues = mapOf("rentId" to rentId, "dueMonth" to dueMonth, "dueYear" to dueYear)
                        for ((key, value) in additionalValues) {
                            bundle.putInt(key, value)
                        }
                        parentFragmentManager.setFragmentResult("rent_paid", bundle)
                    }
                    recyclerView.adapter = rentsAdapter
                }
                .addOnFailureListener { exception ->
                    // Handle the exception
                }
        }else{
            rentsDAO.getAllRents(false)
                .addOnSuccessListener { snapshot ->
                    val rents = snapshot.toObjects(Rent::class.java)
                    val rentsAdapter = RentsAdapter(rents.toMutableList()) { rentId, dueMonth, dueYear ->
                        val bundle = Bundle()
                        val additionalValues = mapOf("rentId" to rentId, "dueMonth" to dueMonth, "dueYear" to dueYear)
                        for ((key, value) in additionalValues) {
                            bundle.putInt(key, value)
                        }
                        parentFragmentManager.setFragmentResult("rent_paid", bundle)
                    }
                    recyclerView.adapter = rentsAdapter
                }
                .addOnFailureListener { exception ->
                    // Handle the exception
                }
        }

        recyclerView = view.findViewById(R.id.rv_unpaid_rent)
        recyclerView.layoutManager = LinearLayoutManager(view.context)
    }

    private suspend fun getAllRents(): List<Rent>{
        val rentsDAO = RentsDAO()
        val rents = mutableListOf<Rent>()
        val result = rentsDAO.getAllRents(false).await()
        rents.addAll(result.toObjects(Rent::class.java))
        return rents
    }
}

