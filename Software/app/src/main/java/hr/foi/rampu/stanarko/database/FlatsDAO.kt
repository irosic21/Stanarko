package hr.foi.rampu.stanarko.database

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import hr.foi.rampu.stanarko.entities.Flat
import hr.foi.rampu.stanarko.entities.Tenant

class FlatsDAO {
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val flatsRef = db.collection("flats")

    fun getFlatByID(flatID : Int): Task<QuerySnapshot> {
        return flatsRef
            .whereEqualTo("id", flatID)
            .get()
    }
    fun getAllFlats(): Task<QuerySnapshot> {
        return flatsRef.get()
    }


    fun getFlatsByOwnerMail(mail: String): Task<QuerySnapshot> {
        return flatsRef
            .whereEqualTo("owner.mail", mail)
            .get()
    }
    fun AddFlat(referencedFlat: Flat){
        db.collection("flats").add(referencedFlat)
    }

    fun removeFlat(attribute: String, value: Any, attribute2 : Int , callback: (Int) -> Unit){
        val db = FirebaseFirestore.getInstance()
        var uspjeh = 0


        val referenceToDatabase = db.collection("flats").whereEqualTo(attribute, value)
        referenceToDatabase.addSnapshotListener{snapshot, e->
            if(e != null){
                Log.d("DADA", "GRESKA")
            }
            if(snapshot != null){
                val documents = snapshot.documents

                documents.forEach{
                    val helpVariable = it.toObject(Flat::class.java)
                    if(helpVariable != null){
                        var tenant = TenantsDAO()
                        tenant.getTenantsByFlatAddress(value as String).addOnSuccessListener { snapshot ->
                            var allTenants = snapshot.toObjects(Tenant::class.java)
                            if(allTenants.isEmpty()){
                                db.collection("flats").document(it.id).delete()
                                callback(1)
                            }
                            else{
                                callback(0)

                            }

                        }

                    }
                }
            }
        }

    }

    private fun callback(i: Int): Int {
        return i;
    }


}