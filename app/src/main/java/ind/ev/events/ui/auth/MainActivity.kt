package ind.ev.events.ui.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import ind.ev.events.ui.HomeActivity
import ind.ev.events.R
import ind.ev.events.databinding.ActivityMainBinding

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding:ActivityMainBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = "Articuleren"
        auth = FirebaseAuth.getInstance()

        val signupFragment = SignUpFragment()
        setCurrentFragment(signupFragment)
    }

    fun setCurrentFragment(fragment:Fragment)=
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.flFragment,fragment)
            commit()
        }

    override fun onStart() {
        super.onStart()
        checkLoginState()
    }

    private fun checkLoginState(){
        if(auth.currentUser!=null){
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }
    }
    fun startHomeIntent(){
        startActivity(Intent(this, HomeActivity::class.java))
    }
}