package ind.ev.events.ui.profile

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import ind.ev.events.ui.HomeActivity
import ind.ev.events.ui.auth.MainActivity
import ind.ev.events.MainViewModel
import ind.ev.events.R
import ind.ev.events.databinding.ProfileFragmentBinding
import ind.ev.events.models.Profile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class ProfileFragment:Fragment() {

    private var _binding:ProfileFragmentBinding?=null
    private val binding get() = _binding!!
    private lateinit var auth:FirebaseAuth
    private val viewModel: MainViewModel by viewModels()
    private lateinit var profile:Profile

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ProfileFragmentBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()

        val email = arguments?.getString("email")
        val accType = arguments?.getString("accType")
        binding.tvEmail.text = email

        if(accType.equals("Organizer")){
            binding.pnVerify.visibility = View.VISIBLE
        }

        CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.Main){
                try {
                    profile = viewModel.getProfile(email.toString(),getString(R.string.orgRef))
                    if(accType.equals("Organizer")){
                        if(profile.verify){
                            binding.pnVerify.text = profile.number
                            binding.pnVerify.setTextColor(Color.GRAY)
                        }
                    }
                    binding.tvName.text = profile.name
                }catch (e:Exception){
                    Toast.makeText(context,"Error: ${e.message}",Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.signOut.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    withContext(Dispatchers.Main){
                        viewModel.deleteLocalProfile()
                        auth.signOut()
                        checkLoginState()
                    }
                }catch (e:Exception){
                    withContext(Dispatchers.Main){
                        Toast.makeText(context,"Logout failed",Toast.LENGTH_LONG).show()
                    }
                }
            }
        }

        val bundle = Bundle()
        bundle.putString("email",email)

        binding.pnVerify.setOnClickListener {
            val phnVerifyFragment = PhnVerifyFragment()
            phnVerifyFragment.arguments = bundle
            (activity as HomeActivity).setCurrentFragmentBack(phnVerifyFragment)
        }
    }

    private fun checkLoginState(){
        if(auth.currentUser==null){
            startActivity(Intent(context, MainActivity::class.java))
            activity?.finish()
        }
    }
}