package hr.foi.rampu.stanarko

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import com.google.firebase.auth.FirebaseAuth
import hr.foi.rampu.stanarko.NavigationDrawer.OwnerDrawerActivity
import hr.foi.rampu.stanarko.adapters.FlatsAdapter
import hr.foi.rampu.stanarko.database.FlatsDAO
import hr.foi.rampu.stanarko.database.OwnersDAO
import hr.foi.rampu.stanarko.databinding.ActivityAddFlatBinding
import hr.foi.rampu.stanarko.entities.Flat
import hr.foi.rampu.stanarko.entities.Owner

class AddFlatActivity : OwnerDrawerActivity() {

    lateinit var binding: ActivityAddFlatBinding
    var maxID: Int = 1;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddFlatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        allocateActivityTitle(getString(R.string.add_new_flat_title))

        val myButton = findViewById<Button>(R.id.btnAddFlat)
        Load()
        myButton.setOnClickListener{
            AddFunction()
        }
    }

    fun Load(){
        var spinnerChoices = findViewById<Spinner>(R.id.spinnerOccupied)

        val choices = arrayOf("Yes", "No")
        val spinnerAdapter2 = ArrayAdapter(this, android.R.layout.simple_spinner_item, choices)
        spinnerAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerChoices.adapter = spinnerAdapter2
    }

    fun AddFunction() {

        val addressValue = findViewById<EditText>(R.id.etAddress).text.toString()
        val city = findViewById<EditText>(R.id.etCity).text.toString()
        val postalNumber = findViewById<EditText>(R.id.etPostalCode).text.toString().toInt()
        var cmbOkupiran = findViewById<Spinner>(R.id.spinnerOccupied)
        val amount = findViewById<EditText>(R.id.etAmount).text.toString().toDouble()

        val flat1 = FlatsDAO();

        flat1.getAllFlats().addOnSuccessListener { snapshot ->
            var temp = snapshot.toObjects(Flat::class.java)

            var flatOccupied = true
            if(cmbOkupiran.selectedItem.toString() == "No"){
                flatOccupied=false
            }


            var loggedEmail = FirebaseAuth.getInstance().currentUser?.email
            if (loggedEmail != null) {

                var help = OwnersDAO()

                help.getOwnerByEmail(loggedEmail).addOnSuccessListener { snapshot ->

                    var currentSigned = snapshot.toObjects(Owner::class.java)[0]
                    val flatToAdd = Flat(1,addressValue,city,currentSigned,flatOccupied, amount, postalNumber)
                    val dodavanje = FlatsDAO()
                    dodavanje.AddFlat(flatToAdd)

                }

            } else {
                Log.d("DADA", "NULL JE")
            }

            var help = FlatsDAO()
            help.getAllFlats().addOnSuccessListener { snapshot ->
                var a =  snapshot.toObjects(Flat::class.java)
                var b = FlatsAdapter(a)

                val intent = Intent(this, MainActivity::class.java);
                startActivity(intent)
            }

        }

    }

}