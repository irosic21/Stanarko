package hr.foi.rampu.stanarko.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import hr.foi.rampu.stanarko.R
import hr.foi.rampu.stanarko.entities.Tenant

class TenantsAdapter(private val tenantsList : List<Tenant>) : RecyclerView.Adapter<TenantsAdapter.TenantViewHolder>() {
    inner class TenantViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val tenantName: TextView
        private val tenantPhone: TextView
        init {
            tenantName = view.findViewById(R.id.tv_tenant_name)
            tenantPhone = view.findViewById(R.id.tv_tenant_phone)
        }
        fun bind(tenant : Tenant) {
            tenantName.text = tenant.name
            tenantPhone.text = tenant.phoneNumber
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TenantViewHolder {
        val taskView = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.tenant_item, parent, false)
        return TenantViewHolder(taskView)
    }

    override fun onBindViewHolder(holder: TenantViewHolder, position: Int) {
        holder.bind(tenantsList[position])
    }

    override fun getItemCount(): Int {
        return tenantsList.size
    }

}