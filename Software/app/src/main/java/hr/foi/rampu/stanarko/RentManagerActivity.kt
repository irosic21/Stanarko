package hr.foi.rampu.stanarko

import android.os.Bundle
import androidx.viewpager2.widget.ViewPager2
import hr.foi.rampu.stanarko.NavigationDrawer.TenantDrawerActivity
import hr.foi.rampu.stanarko.fragments.PaidRentFragment
import hr.foi.rampu.stanarko.fragments.UnpaidRentFragment
import hr.foi.rampu.stanarko.adapters.RentManagerAdapter
import hr.foi.rampu.stanarko.database.TenantsDAO
import hr.foi.rampu.stanarko.databinding.ActivityRentManagerBinding
import hr.foi.rampu.stanarko.helpers.HelperClass

class RentManagerActivity : TenantDrawerActivity() {
    private val currentUserMail = currentUser?.email.toString()
    private val tenantsDAO = TenantsDAO()
    private val helperClass = HelperClass()

    private lateinit var binding: ActivityRentManagerBinding

    private lateinit var viewPager2: ViewPager2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRentManagerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        allocatedActivityTitle(getString(R.string.rents_title))

        val rentPagerAdapter = RentManagerAdapter (supportFragmentManager, lifecycle)
        rentPagerAdapter.addFragment(
            RentManagerAdapter.FragmentItem(
                R.string.unpaid_rent,
                UnpaidRentFragment::class
            )
        )

        rentPagerAdapter.addFragment(
            RentManagerAdapter.FragmentItem(
                R.string.paid_rent,
                PaidRentFragment::class
            )
        )

        viewPager2 = findViewById(R.id.viewpager)
        viewPager2.adapter = rentPagerAdapter
    }

    override fun onBackPressed() {
        super.onBackPressed()
        helperClass.navigateHomeScreen(this, currentUserMail)
        finish()
    }
}