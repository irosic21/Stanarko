package hr.foi.rampu.stanarko

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import hr.foi.rampu.stanarko.database.FlatsDAO

class DeleteFlatActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_delete_flat)

        var delete = FlatsDAO()
        //delete.removeFlat("address", flat.address, flat.id)

    }
}