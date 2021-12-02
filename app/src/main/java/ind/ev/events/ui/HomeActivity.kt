package ind.ev.events.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.razorpay.PaymentResultListener
import dagger.hilt.android.AndroidEntryPoint
import ind.ev.events.MainViewModel
import ind.ev.events.R
import ind.ev.events.databinding.ActivityHomeBinding
import ind.ev.events.ui.fav.FavEventFragment
import ind.ev.events.ui.history.PartiHistoryFragment
import ind.ev.events.ui.home.SearchEventFragment
import ind.ev.events.ui.profile.ProfileFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class HomeActivity : AppCompatActivity(),PaymentResultListener {

    private lateinit var binding:ActivityHomeBinding
    private lateinit var auth:FirebaseAuth
    private val viewModel: MainViewModel by viewModels()
    var successListener=" "

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        auth = FirebaseAuth.getInstance()
        setContentView(binding.root)
        val bundle = Bundle()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                withContext(Dispatchers.Main){
                    val profile = viewModel.getProfileLocal()[0]
                    bundle.putString("email",profile.email)
                    bundle.putString("accType",profile.accType)
                }
            }catch (e:Exception){
            }
        }
        val profileFragment = ProfileFragment()
        profileFragment.arguments = bundle

        val searchEventFragment = SearchEventFragment()
        setCurrentFragment(searchEventFragment)

        val partiHistoryFragment=  PartiHistoryFragment()
        partiHistoryFragment.arguments = bundle

        val favEventFragment = FavEventFragment()
        favEventFragment.arguments = bundle

        binding.bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId){
                R.id.imSearch -> setCurrentFragment(searchEventFragment)
                R.id.imProfile ->setCurrentFragment(profileFragment)
                R.id.imHistory -> setCurrentFragment(partiHistoryFragment)
                R.id.imFav -> setCurrentFragment(favEventFragment)
            }
            true
        }
    }

    private fun setCurrentFragment(fragment:Fragment)=
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.flHome,fragment)
            commit()
        }

    fun setCurrentFragmentBack(fragment:Fragment)=
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.flHome,fragment)
            addToBackStack("b")
            commit()
        }

    override fun onPaymentError(p0: Int, p1: String?) {
        Toast.makeText(this,"Error:${p1}",Toast.LENGTH_SHORT).show()
        successListener ="N"
    }

    override fun onPaymentSuccess(p0: String?) {
        Toast.makeText(this,"Success",Toast.LENGTH_LONG).show()
        successListener ="Y"
    }


}