package hr.foi.rampu.stanarko

import android.os.Bundle
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import hr.foi.rampu.stanarko.NavigationDrawer.OwnerDrawerActivity
import hr.foi.rampu.stanarko.adapters.RentManagerAdapter
import hr.foi.rampu.stanarko.databinding.ActivityOwnerContractManagerBinding
import hr.foi.rampu.stanarko.fragments.OwnerActiveContractsFragment
import hr.foi.rampu.stanarko.fragments.OwnerExpiredContractsFragment

class OwnerContractManagerActivity : OwnerDrawerActivity() {

    lateinit var binding: ActivityOwnerContractManagerBinding
    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager2: ViewPager2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOwnerContractManagerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        allocateActivityTitle(getString(R.string.owner_contracts_title))

        val contractAdapter = RentManagerAdapter (supportFragmentManager, lifecycle)
        contractAdapter.addFragment(
            RentManagerAdapter.FragmentItem(
                R.string.owner_active_contract_fragment,
                OwnerActiveContractsFragment::class
            )
        )

        contractAdapter.addFragment(
            RentManagerAdapter.FragmentItem(
                R.string.owner_expired_contract_fragment,
                OwnerExpiredContractsFragment::class
            )
        )

        tabLayout = findViewById(R.id.owner_contract_manager_tab)
        viewPager2 = findViewById(R.id.owner_contract_manager_viewpager)

        viewPager2.adapter = contractAdapter
        TabLayoutMediator(tabLayout,viewPager2){tab,position ->
            tab.setText(contractAdapter.fragmentItems[position].titleRes)
        }.attach()
    }
}