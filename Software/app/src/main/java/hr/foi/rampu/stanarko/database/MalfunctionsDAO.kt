package hr.foi.rampu.stanarko.database

import android.content.Context
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import hr.foi.rampu.stanarko.entities.Malfunction

class MalfunctionsDAO {
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    fun addMalfunction(malfunction: Malfunction, context: Context){
        db.collection("malfunctions").add(malfunction).addOnFailureListener { e ->
            Toast.makeText(context,"Error:${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}