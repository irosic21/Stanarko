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
import com.google.firebase.firestore.QuerySnapshot
import hr.foi.rampu.stanarko.R
import hr.foi.rampu.stanarko.adapters.RentsAdapter
import hr.foi.rampu.stanarko.database.RentsDAO
import hr.foi.rampu.stanarko.database.TenantsDAO
import hr.foi.rampu.stanarko.entities.Rent
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await


class PaidRentFragment : Fragment() {
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
        return inflater.inflate(R.layout.fragment_paid_rent, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val rentsDAO = RentsDAO()
        val tenantsDAO = TenantsDAO()
        val isTenant = runBlocking {tenantsDAO.isUserTenant(currentUserMail)}

        recyclerView = view.findViewById(R.id.rv_paid_rent)

        if(isTenant){
            recyclerView.adapter = runBlocking { RentsAdapter(getRents(currentUserMail) as MutableList<Rent>) }

            parentFragmentManager.setFragmentResultListener("rent_paid", viewLifecycleOwner) { _, bundle ->
                val addedRentId = bundle.getInt("rentId")
                val addedRentMonthDue = bundle.getInt("dueMonth")
                val addedRentYearDue = bundle.getInt("dueYear")

                val rentsAdapter = recyclerView.adapter as RentsAdapter

                suspend fun getSnapshot(): QuerySnapshot {
                    val rentsRef = rentsDAO.getAllRentByIDDueMonthYear(addedRentId, addedRentMonthDue, addedRentYearDue).get()
                    return rentsRef.await()
                }

                suspend fun getRent(): Rent? {
                    val snapshot = getSnapshot()
                    val documents = snapshot.documents
                    return documents[0].toObject(Rent::class.java)
                }

                val rent = runBlocking {getRent()}
                if (rent != null) {
                    rentsAdapter.addRentToList(rent)
                }
            }
        }else{
            recyclerView.adapter = runBlocking { RentsAdapter(getAllRents() as MutableList<Rent>) }

            parentFragmentManager.setFragmentResultListener("rent_paid", viewLifecycleOwner) { _, bundle ->
                val addedRentId = bundle.getInt("rentId")
                val addedRentMonthDue = bundle.getInt("dueMonth")
                val addedRentYearDue = bundle.getInt("dueYear")

                val rentsAdapter = recyclerView.adapter as RentsAdapter

                suspend fun getSnapshot(): QuerySnapshot {
                    val rentsRef = rentsDAO.getAllRentByIDDueMonthYear(addedRentId, addedRentMonthDue, addedRentYearDue).get()
                    return rentsRef.await()
                }

                suspend fun getRent(): Rent? {
                    val snapshot = getSnapshot()
                    val documents = snapshot.documents
                    return documents[0].toObject(Rent::class.java)
                }

                val rent = runBlocking {getRent()}
                if (rent != null) {
                    rentsAdapter.addRentToList(rent)
                }
            }
        }
        recyclerView.layoutManager = LinearLayoutManager(view.context)

    }
    private suspend fun getRents(currentUserMail: String): List<Rent>{
        val rentsDAO = RentsDAO()
        val rents = mutableListOf<Rent>()
        val result = rentsDAO.getAllRentsByMail(currentUserMail,true).await()
        rents.addAll(result.toObjects(Rent::class.java))
        return rents
    }

    private suspend fun getAllRents(): List<Rent>{
        val rentsDAO = RentsDAO()
        val rents = mutableListOf<Rent>()
        val result = rentsDAO.getAllRents(true).await()
        rents.addAll(result.toObjects(Rent::class.java))
        return rents
    }
}

