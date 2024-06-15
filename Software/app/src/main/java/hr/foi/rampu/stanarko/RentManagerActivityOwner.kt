package hr.foi.rampu.stanarko

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.viewpager2.widget.ViewPager2
import hr.foi.rampu.stanarko.NavigationDrawer.OwnerDrawerActivity
import hr.foi.rampu.stanarko.adapters.RentManagerAdapter
import hr.foi.rampu.stanarko.database.RentsDAO
import hr.foi.rampu.stanarko.databinding.ActivityRentManagerOwnerBinding
import hr.foi.rampu.stanarko.fragments.PaidRentFragment
import hr.foi.rampu.stanarko.fragments.UnpaidRentFragment
import hr.foi.rampu.stanarko.helpers.HelperClass

class RentManagerActivityOwner : OwnerDrawerActivity() {
    private val currentUserMail = currentUser?.email.toString()
    private var rentsDAO = RentsDAO()
    private val helperClass = HelperClass()

    private lateinit var binding: ActivityRentManagerOwnerBinding

    private lateinit var viewPager2: ViewPager2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRentManagerOwnerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        allocateActivityTitle(getString(R.string.rent_main_title))

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


        val btnCheckForRents = findViewById<Button>(R.id.btn_check_for_rents)
        btnCheckForRents.setOnClickListener{
            rentsDAO.checkForRents()
            Toast.makeText(this,"Check for rents is over!", Toast.LENGTH_LONG).show()
        }

        viewPager2 = findViewById(R.id.viewpager)
        viewPager2.adapter = rentPagerAdapter
    }

    override fun onBackPressed() {
        super.onBackPressed()
        helperClass.navigateHomeScreen(this, currentUserMail)
        finish()
    }
}