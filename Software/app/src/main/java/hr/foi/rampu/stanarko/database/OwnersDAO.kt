package hr.foi.rampu.stanarko.database

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import hr.foi.rampu.stanarko.entities.Owner
import hr.foi.rampu.stanarko.entities.Tenant
import kotlinx.coroutines.tasks.await
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.QuerySnapshot
import hr.foi.rampu.stanarko.entities.Rent

class OwnersDAO {
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    suspend fun getOwner(ownerMail: String): Owner? {
        val ownerRef = db.collection("tenants")
            .whereEqualTo("mail", ownerMail)
            .get()
            .await()
        val documents = ownerRef.documents
        val result = documents[0].toObject(Tenant::class.java)
        return result?.flat?.owner
    }

    suspend fun getOwnerInfo(ownerMail: String): Owner? {
        val ownerRef = db.collection("owners")
            .whereEqualTo("mail", ownerMail)
            .get()
            .await()
        val documents = ownerRef.documents
        return documents[0].toObject(Owner::class.java)
    }

    fun updateOwnerToken(ownerMail: String, token: String) {
        val ownerSnapshot = db.collection("owners")
            .whereEqualTo("mail", ownerMail)
        ownerSnapshot.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w("Listen failed", e)
                return@addSnapshotListener
            }
            if (snapshot != null) {
                val documents = snapshot.documents
                documents.forEach {
                    val owner = it.toObject(Rent::class.java)
                    if (owner != null) {
                        db.collection("owners").document(it.id).update("token", token)
                    }
                }
            }
        }
    }

    fun getOwnerByEmail(ownerMail: String): Task<QuerySnapshot> {
        return db.collection("owners")
            .whereEqualTo("mail", ownerMail)
            .get()
    }
}