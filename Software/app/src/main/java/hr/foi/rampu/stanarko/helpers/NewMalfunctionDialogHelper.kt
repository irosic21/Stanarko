package hr.foi.rampu.stanarko.helpers

import android.view.View
import android.widget.EditText
import hr.foi.rampu.stanarko.R
import hr.foi.rampu.stanarko.entities.Malfunction
import hr.foi.rampu.stanarko.entities.Tenant
import java.util.*

class NewMalfunctionDialogHelper(private val view: View) {
    fun buildMalfunction(tenant: Tenant) : Malfunction{
        val description = view.findViewById<EditText>(R.id.et_malfunction_description).text.toString()
        return Malfunction(description, tenant.flat, Date(), false, tenant )
    }
}