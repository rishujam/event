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
import ind.ev.events.databinding.LoginFragmentBinding
import ind.ev.events.models.Profile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.*

@AndroidEntryPoint
class LoginFragment:Fragment() {

    private var _binding:LoginFragmentBinding?=null
    private val binding get() = _binding!!
    private lateinit var auth:FirebaseAuth
    private val viewModel: MainViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = LoginFragmentBinding.inflate(inflater,container,false)
        auth = FirebaseAuth.getInstance()

        binding.directToSignup.setOnClickListener {
            val signupFragment = SignUpFragment()
            (activity as MainActivity).setCurrentFragment(signupFragment)
        }

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text
            val pass = binding.etPass.text
            val boolean = binding.org.isChecked || binding.part.isChecked
            if(email.isNotEmpty() && pass.isNotEmpty() && boolean){
                val accType = binding.radioGroup.checkedRadioButtonId
                val accT = binding.root.findViewById<RadioButton>(accType).text.toString()
                loginUser(email.toString(),pass.toString(),accT)
                checkLoginState()
            }
        }

        binding.etEmail.setOnClickListener {
            openSomeActivityForResult()
        }
        return binding.root
    }

    private fun loginUser(email:String, pass:String,accType:String) {
        CoroutineScope(Dispatchers.IO).launch {
            try { //TODO Remove acc type selection form here it will get from firebase when we signin
                auth.signInWithEmailAndPassword(email, pass).await()
                withContext(Dispatchers.Main) {
                    viewModel.saveProfileLocal(Profile(email,"",accType,"",false))
                    checkLoginState()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }
    private fun checkLoginState(){
        val currentUser = auth.currentUser
        if (currentUser!=null){
            (activity as MainActivity).startHomeIntent()
            (activity as MainActivity).finish()
        }
    }

    private var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            val result = data?.getStringExtra(AccountManager.KEY_ACCOUNT_NAME).toString()
            binding.etEmail.setText(result)
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

    override fun onDestroy() {
        super.onDestroy()
        _binding=null
    }
}