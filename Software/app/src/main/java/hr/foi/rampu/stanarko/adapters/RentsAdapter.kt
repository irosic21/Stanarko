package hr.foi.rampu.stanarko.adapters

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import hr.foi.rampu.stanarko.R
import hr.foi.rampu.stanarko.database.RentsDAO
import hr.foi.rampu.stanarko.entities.Rent
import kotlinx.coroutines.runBlocking
import java.text.DateFormatSymbols
import java.util.*

class RentsAdapter(
    private val rentLists: MutableList<Rent>,
    private val onPaidRent: ((taskId: Int, dueMonth: Int, dueYear: Int) -> Unit)? = null
) : RecyclerView.Adapter<RentsAdapter.RentViewHolder>() {
    @SuppressLint("MissingInflatedId")
    inner class RentViewHolder(view: View) : RecyclerView.ViewHolder(view)  {
        private val rentsDAO = RentsDAO()

        private val rentPersonName : TextView
        private val rentPersonAddress : TextView
        private val rentPersonDateOfMovingIn : TextView
        private val rentDueMonth : TextView
        private val rentDueYear : TextView
        private val rentDueMonthYear : LinearLayout
        private val rentAmount : TextView

        private val rentInfoTenantName : TextView
        private val rentInfoPhoneNumber : TextView
        private val rentInfoAddress : TextView
        private val rentInfoMonthYear : TextView
        init {
            rentPersonName = view.findViewById(R.id.tv_rent_person_name)
            rentPersonAddress = view.findViewById(R.id.tv_rent_person_address)
            rentPersonDateOfMovingIn = view.findViewById(R.id.tv_rent_date_of_moving_in)
            rentDueMonth = view.findViewById(R.id.tv_rent_due_month)
            rentDueYear = view.findViewById(R.id.tv_rent_due_year)
            rentDueMonthYear = view.findViewById(R.id.ll_month_year)
            rentAmount = view.findViewById(R.id.tv_rent_amount)

            val rentInfoDialogView = LayoutInflater
                .from(view.context)
                .inflate(R.layout.rent_more_info_dialog, null, false)
            rentInfoTenantName = rentInfoDialogView.findViewById(R.id.tv_rent_info_tenant_name)
            rentInfoPhoneNumber = rentInfoDialogView.findViewById(R.id.tv_rent_info_phone_number)
            rentInfoAddress = rentInfoDialogView.findViewById(R.id.tv_rent_info_flat_address)
            rentInfoMonthYear = rentInfoDialogView.findViewById(R.id.tv_rent_info_month_year)

            view.setOnClickListener {
                val currentParent = (view.parent) as ViewGroup
                currentParent.removeAllViews()

                val currentRent = rentLists[adapterPosition]

                if(currentRent.rent_paid){
                    AlertDialog.Builder(view.context)
                        .setView(rentInfoDialogView)
                        .setTitle("Rent details")
                        .setNegativeButton("Cancel") { dialog, _ ->
                            dialog.cancel()
                        }
                        .show()
                }else{
                    AlertDialog.Builder(view.context)
                        .setView(rentInfoDialogView)
                        .setTitle("Rent details")
                        .setNeutralButton("Pay rent") { _, _ ->
                            val paidRent = rentLists[adapterPosition]
                            runBlocking {rentsDAO.payRentByDocumentID(paidRent.id, paidRent.month_to_be_paid, paidRent.year_to_be_paid)}
                            removeRentFromList()
                            onPaidRent?.invoke(paidRent.id, paidRent.month_to_be_paid, paidRent.year_to_be_paid)
                        }
                        .setNegativeButton("Cancel") { dialog, _ ->
                            dialog.cancel()
                        }
                        .show()
                }
                return@setOnClickListener
            }
        }

        private fun removeRentFromList() {
            rentLists.removeAt(adapterPosition)
            notifyItemRemoved(adapterPosition)
        }

        @SuppressLint("SetTextI18n")
        fun bind(rent: Rent) {
            rentPersonName.text = "${rent.tenant?.name} ${rent.tenant?.surname}"
            rentPersonAddress.text = rent.tenant?.flat?.address.toString()
            rentPersonDateOfMovingIn.text = "since. ${rent.tenant?.dateOfMovingIn}"
            rentDueMonth.text = formatMonth(rent.month_to_be_paid).substring(0,3).uppercase()
            rentDueYear.text = rent.year_to_be_paid.toString()
            rentAmount.text = "${rent.tenant?.flat?.amount.toString()} €"

            rentInfoTenantName.text = "${rent.tenant?.name} ${rent.tenant?.surname}"
            rentInfoPhoneNumber.text = rent.tenant?.phoneNumber.toString()
            rentInfoAddress.text = rent.tenant?.flat?.address.toString()
            rentInfoMonthYear.text = formatMonth(rent.month_to_be_paid).substring(0,3).uppercase() + " " + rent.year_to_be_paid.toString()
        }

        private fun formatMonth(month: Int): String {
            val symbols = DateFormatSymbols(Locale.ENGLISH)
            val monthNames: Array<String> = symbols.months
            return monthNames[month - 1]
        }
    }


    fun addRentToList(newRent: Rent) {
        var newIndexInList = rentLists.indexOfFirst { rent ->
            rent.month_to_be_paid > newRent.month_to_be_paid
        }
        if (newIndexInList == -1) {
            newIndexInList = rentLists.size
        }
        rentLists.add(newIndexInList, newRent)
        notifyItemInserted(newIndexInList)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RentViewHolder {
        val rentView = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.rent_list_item, parent, false)
        return RentViewHolder(rentView)
    }

    override fun onBindViewHolder(holder: RentViewHolder, position: Int) {
        holder.bind(rentLists[position])
    }

    override fun getItemCount() = rentLists.size
}