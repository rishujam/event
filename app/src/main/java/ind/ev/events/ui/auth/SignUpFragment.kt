package ind.ev.events.ui.auth

import android.accounts.AccountManager
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.gms.common.AccountPicker
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import ind.ev.events.MainViewModel
import ind.ev.events.R
import ind.ev.events.databinding.SignupFragmentBinding
import ind.ev.events.models.Profile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.*

@AndroidEntryPoint
class SignUpFragment:Fragment() {

    private var _binding:SignupFragmentBinding?=null
    private val binding get()= _binding!!
    private lateinit var auth: FirebaseAuth
    private val viewModel: MainViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = SignupFragmentBinding.inflate(inflater,container,false)
        auth = FirebaseAuth.getInstance()
        binding.directToLogin.setOnClickListener {
            val loginFragment = LoginFragment()
            (activity as MainActivity).setCurrentFragment(loginFragment)
        }

        binding.etEmail.setOnClickListener {
            openSomeActivityForResult()
        }
        binding.btnSignup.setOnClickListener {
            val email = binding.etEmail.text
            val pass = binding.etPass.text
            val boolean = binding.org.isChecked || binding.part.isChecked
            if(email.isNotEmpty() && pass.isNotEmpty() && boolean){
                val accType = binding.radioGroup.checkedRadioButtonId
                val accT = binding.root.findViewById<RadioButton>(accType).text.toString()
                val collection = getString(R.string.orgRef)
                registerUser(email.toString(),pass.toString(),accT,collection)
            }
            else{
                Toast.makeText(context,"Enter details properly",Toast.LENGTH_SHORT).show()
            }
        }
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            val result = data?.getStringExtra(AccountManager.KEY_ACCOUNT_NAME).toString()
            binding.etEmail.setText(result)
        }
    }

    private fun registerUser(email: String,pass:String,accType:String,collection:String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                auth.createUserWithEmailAndPassword(email, pass).await()
                withContext(Dispatchers.Main) {
                    viewModel.saveProfile(collection, Profile(email,"",accType,"",false),email)
                    viewModel.saveProfileLocal(Profile(email,"",accType,"",false))
                    checkLoggedInState()
                }
            }catch (e:Exception){
                withContext(Dispatchers.Main){
                    Toast.makeText(context,e.message,Toast.LENGTH_SHORT).show()
                }
            }
        }

    }

    private fun checkLoggedInState(){
        val currentUser = auth.currentUser
        if (currentUser!=null){
            (activity as MainActivity).startHomeIntent()
            (activity as MainActivity).finish()
        }
    }

    private fun openSomeActivityForResult() {
        val intent = AccountPicker.newChooseAccountIntent(
            AccountPicker.AccountChooserOptions.Builder()
                .setAllowableAccountsTypes(Arrays.asList("com.google"))
                .build()
        )
        resultLauncher.launch(intent)
    }
}