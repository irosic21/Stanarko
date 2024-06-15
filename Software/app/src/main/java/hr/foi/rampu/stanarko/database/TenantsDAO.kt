package hr.foi.rampu.stanarko.database

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import hr.foi.rampu.stanarko.entities.Flat
import hr.foi.rampu.stanarko.entities.Tenant
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.tasks.await

class TenantsDAO {
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val tenantsRef = db.collection("tenants")

    suspend fun isUserTenant(userMail: String) : Boolean{
        val tenant = tenantsRef
            .whereEqualTo("mail", userMail)
            .get()
            .await()
        return tenant.size() > 0
    }

    suspend fun isUserInFlat(userMail: String) : Boolean{
        val tenant = tenantsRef
            .whereEqualTo("mail", userMail)
            .whereEqualTo("flat", null)
            .get()
            .await()
        return tenant.size() <= 0
    }

    suspend fun getTenant(userMail: String) : Tenant? {
        val tenant = tenantsRef
            .whereEqualTo("mail", userMail)
            .get()
            .await()
        return tenant.documents[0].toObject(Tenant::class.java)
    }

    fun getTenantByID(tenantID : Int): Task<QuerySnapshot> {
        return db.collection("flats")
            .whereEqualTo("id", tenantID)
            .get()
    }

    fun getTenantByMail(tenantMail : String): Task<QuerySnapshot> {
        return db.collection("tenants")
            .whereEqualTo("mail", tenantMail)
            .get()
    }

    fun getAllTenants(): Task<QuerySnapshot> {
        return tenantsRef
            .get()
    }

    fun getTenantsWithFlat(): Task<QuerySnapshot> {
        return tenantsRef
            .whereNotEqualTo("flat", null)
            .get()
    }

    fun changeDateOfMovingIn(email: String, selectedDate: String){
        val db = FirebaseFirestore.getInstance()

        var dateUpdated = false
        val referenceToDatabase = db.collection("tenants").whereEqualTo("mail", email)
        referenceToDatabase.addSnapshotListener{snapshot, e->
            if(e != null){
                Log.d("GRESKA", e.message.toString())
            }
            if(snapshot != null && !dateUpdated){
                val documents = snapshot.documents
                documents.forEach{
                    val helpVariable = it.toObject(Tenant::class.java)
                    if (helpVariable != null) {
                        db.collection("tenants").document(it.id).update("dateOfMovingIn", selectedDate )
                        dateUpdated = true
                    }
                }
            }
        }
    }

    fun changeFlatOfTenant(value: String,value2: Flat){

        val db = FirebaseFirestore.getInstance()
        var flatUpdated = false

        val referenceToDatabase = db.collection("tenants").whereEqualTo("mail", value)
        referenceToDatabase.addSnapshotListener{snapshot, e->
            if(e != null){
                Log.d("GRESKA", e.message.toString())
            }
            if(snapshot != null && !flatUpdated){
                val documents = snapshot.documents
                documents.forEach{
                    val helpVariable = it.toObject(Tenant::class.java)
                    if (helpVariable != null) {
                        db.collection("tenants").document(it.id).update("flat", value2 )
                        flatUpdated = true
                    }
                }
            }
        }
    }

    suspend fun getUncontactedTenants(currentUserMail: String): MutableList<Tenant> {
        val tenants = tenantsRef
            .whereEqualTo("flat.owner.mail", currentUserMail)
            .get()
            .await()
            .toObjects(Tenant::class.java)

        val filteredTenants = ArrayList<Tenant>()
        for (tenant in tenants){
            val result = db.collection("channels").whereArrayContains("participants", tenant.mail).get().await()
            if(result.size()<=0){
                filteredTenants.add(tenant)
            }
        }
        return filteredTenants
    }

    fun createTenant(tenant: Tenant, context: Context){
        tenantsRef.add(tenant).addOnFailureListener { e ->
            Toast.makeText(context,"Error:${e.message}",Toast.LENGTH_SHORT).show()
        }
    }

    fun getTenantsByFlatId(flatID : Int): Task<QuerySnapshot> {
        return tenantsRef
            .whereEqualTo("flat.id", flatID)
            .get()
    }
    fun getTenantsByFlatAddress(flatAddress : String): Task<QuerySnapshot> {
        return tenantsRef
            .whereEqualTo("flat.address", flatAddress)
            .get()
    }
}