package hr.foi.rampu.stanarko.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import hr.foi.rampu.stanarko.R
import hr.foi.rampu.stanarko.entities.Contract
import java.text.SimpleDateFormat

class ContractRecycleViewAdapter(private val contractList:MutableList<Contract>) :
    RecyclerView.Adapter<ContractRecycleViewAdapter.ContractViewHolder>() {
    class ContractViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        val tvTenant : TextView = itemView.findViewById(R.id.contract_tenant)
        val tvDescription: TextView = itemView.findViewById(R.id.contract_description)
        val tvDateStart: TextView = itemView.findViewById(R.id.contract_start_date)
        val tvDateEnd: TextView = itemView.findViewById(R.id.contract_end_date)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContractViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.contract_list_item,parent,false)
        return ContractViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ContractViewHolder, position: Int) {
        val dateFormat = SimpleDateFormat("dd.MM.yyyy")
        holder.tvTenant.text = contractList[position].tenant?.name + " " + contractList[position].tenant?.surname
        holder.tvDescription.text = descriptionText(position)
        holder.tvDateStart.text = dateFormat.format(contractList[position].todayDate)
        holder.tvDateEnd.text = dateFormat.format(contractList[position].expiringDate)
    }

    override fun getItemCount(): Int = contractList.size

    private fun descriptionText(position: Int):String{
        val dateFormat = SimpleDateFormat("dd.MM.yyyy")
        val text = contractList[position].tenant
        return text?.flat?.owner?.name + " " + text?.flat?.owner?.surname + " " + "from" + " " +
                text?.flat?.city+", street"+ " "+ text?.flat?.address + ", as owner and" + " " +
                text?.name + " " + text?.surname + " " + "from"+ " " +
                text?.flat?.city+", street"+ " "+ text?.flat?.address + ", as tenant they signed on" +
                " " + dateFormat.format(contractList[position].todayDate) + " "+ "in" + " " +
                text?.flat?.city
    }
}