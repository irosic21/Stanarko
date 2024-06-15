package hr.foi.rampu.stanarko

import android.os.Bundle
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import hr.foi.rampu.stanarko.NavigationDrawer.TenantDrawerActivity
import hr.foi.rampu.stanarko.adapters.RentManagerAdapter
import hr.foi.rampu.stanarko.databinding.ActivityTenantContractManagerBinding
import hr.foi.rampu.stanarko.fragments.TenantActiveContractsFragment
import hr.foi.rampu.stanarko.fragments.TenantExpiredContractsFragment

class TenantContractManagerActivity : TenantDrawerActivity() {

    lateinit var binding: ActivityTenantContractManagerBinding
    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager2: ViewPager2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTenantContractManagerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        allocatedActivityTitle(getString(R.string.tenant_contracts_title))

        val contractAdapter = RentManagerAdapter(supportFragmentManager,lifecycle)
        contractAdapter.addFragment(
            RentManagerAdapter.FragmentItem(
                R.string.owner_active_contract_fragment,
                TenantActiveContractsFragment::class
            )
        )
        contractAdapter.addFragment(
            RentManagerAdapter.FragmentItem(
                R.string.owner_expired_contract_fragment,
                TenantExpiredContractsFragment::class
            )
        )

        tabLayout = findViewById(R.id.tenant_contract_manager_tab)
        viewPager2 = findViewById(R.id.tenant_contract_manager_viewpager)

        viewPager2.adapter = contractAdapter
        TabLayoutMediator(tabLayout,viewPager2){tab, position ->
            tab.setText(contractAdapter.fragmentItems[position].titleRes)
        }.attach()
    }
}