package hr.foi.rampu.stanarko.database

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import hr.foi.rampu.stanarko.entities.Rent
import hr.foi.rampu.stanarko.entities.Tenant
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*

class RentsDAO {
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val rentsRef = db.collection("rents")

    private fun getAllRents(): Task<QuerySnapshot> {
        return rentsRef.get()
    }

    fun getAllRents(paid:Boolean): Task<QuerySnapshot> {
        return rentsRef
            .whereEqualTo("rent_paid", paid)
            .get()
    }

    fun getAllRentsByTenantID(tenantID: Int): Task<QuerySnapshot> {
        return rentsRef
            .whereEqualTo("tenant.id", tenantID)
            .get()
    }
    suspend fun getAllRentsByTenantMail(tenantMail: String, paid:Boolean): QuerySnapshot? {
        Log.e("DATA", "Mail: " + tenantMail)
        Log.e("DATA", "Paid: " + paid)
        return db.collection("rents")
            .whereEqualTo("tenant.mail", tenantMail)
            .get().await()
    }

    fun getAllRentsByMail(mail: String, paid:Boolean): Task<QuerySnapshot> {
        return rentsRef
            .whereEqualTo("tenant.mail", mail)
            .whereEqualTo("rent_paid", paid)
            .get()
    }

    fun getAllRentsByOwnerID(ownerID: Int): Task<QuerySnapshot> {
        return rentsRef
            .whereEqualTo("tenant.flat.owner.id", ownerID)
            .get()
    }

    fun getAllRentsByOwnerID(ownerID: Int, paid:Boolean): Task<QuerySnapshot> {
        return rentsRef
            .whereEqualTo("tenant.flat.owner.id", ownerID)
            .whereEqualTo("rent_paid", paid)
            .get()
    }

    fun checkForRents() {
        val tenantsDAO = TenantsDAO()
        tenantsDAO.getTenantsWithFlat()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val tenant = document.toObject(Tenant::class.java)
                    if(tenant.dateOfMovingIn != null){
                        runBlocking { checkForMissingRents(tenant) }
                    }
                }
            }
    }

    fun getAllRentByIDDueMonthYear(addedRentId: Int, addedRentMonthDue: Int, addedRentYearDue: Int): Query {
        return rentsRef
            .whereEqualTo("id", addedRentId)
            .whereEqualTo("month_to_be_paid", addedRentMonthDue)
            .whereEqualTo("year_to_be_paid", addedRentYearDue)
    }

    private suspend fun getAllRentByIDDueMonthYearTest(mailRent: String, addedRentMonthDue: Int, addedRentYearDue: Int): QuerySnapshot? {
        return rentsRef
            .whereEqualTo("tenant.mail", mailRent)
            .whereEqualTo("month_to_be_paid", addedRentMonthDue)
            .whereEqualTo("year_to_be_paid", addedRentYearDue)
            .get().await()
    }

    private suspend fun checkForMissingRents(tenant: Tenant) {
        val dateFormat = SimpleDateFormat("MM/yyyy", Locale.getDefault())
        val currentDate = Date()
        val currentMonthAndYear = dateFormat.format(currentDate).split("/")

        val currentMonth = currentMonthAndYear[0].toInt()
        val currentYear = currentMonthAndYear[1].toInt()

        val userDate: String? = tenant.dateOfMovingIn

        val startMonth : Int = userDate?.substring(5, 7)!!.toInt()
        val startYear : Int = userDate?.substring(0, 4)!!.toInt()

        for (year in startYear..currentYear) {

            val monthStart = if (year == startYear) startMonth else 1
            val monthEnd = if (year == currentYear) currentMonth else 12

            for (month in monthStart..monthEnd) {
                val size = runBlocking { getAllRentByIDDueMonthYearTest(tenant.mail, month, year)?.size()}
                if (size == 0) {
                    runBlocking { createRent(tenant, month, year) }
                } else {
                    Log.w("Error", "Error while creating rent.")
                }
            }
        }
    }

    private suspend fun createRent(tenant: Tenant, month: Int, year: Int) {
        val rents = mutableListOf<Rent>()
        val result = getAllRents().await()
        rents.addAll(result.toObjects(Rent::class.java))

        val rentsSize = rents.size

        rentsRef.add(Rent(rentsSize + 1, tenant, month, year, false))
    }

    suspend fun payRentByDocumentID(value: Any, value2: Any, value3: Any) {
        val rentsRef = db.collection("rents").whereEqualTo("id", value).whereEqualTo("month_to_be_paid", value2).whereEqualTo("year_to_be_paid", value3)
        rentsRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w("Listen failed", e)
                return@addSnapshotListener
            }
            if (snapshot != null) {
                val documents = snapshot.documents
                documents.forEach {
                    val rent = it.toObject(Rent::class.java)
                    if (rent != null) {
                        runBlocking {updateRentPaidStatus(it.id)}
                    }
                }
            }
        }
    }

    private suspend fun updateRentPaidStatus(documentId: String) {
        val documentReference = rentsRef.document(documentId)
        documentReference.update("rent_paid", true).await()
    }
}

