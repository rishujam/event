package ind.ev.events.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import ind.ev.events.MainViewModel
import ind.ev.events.R
import ind.ev.events.databinding.PhnVerifyFragmentBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class PhnVerifyFragment: Fragment() {

    private var _binding:PhnVerifyFragmentBinding?=null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by viewModels()
    private var sessionId = ""
    private lateinit var no:String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = PhnVerifyFragmentBinding.inflate(inflater,container,false)

        binding.btnSendOtp.setOnClickListener {
            no = binding.etPhone.text.toString()
            if(no.isNotEmpty()){
                if(no.length==10){
                    CoroutineScope(Dispatchers.IO).launch {
                        withContext(Dispatchers.Main){
                            try {
                                val bal = viewModel.checkOtpBalance(getString(R.string.otp_apikey))
                                if(bal.Details.toInt()>0){
                                    val response = viewModel.getOtp(getString(R.string.otp_apikey),no)
                                    if(response.Status=="Success"){
                                        binding.cdOtp.visibility =View.VISIBLE
                                        binding.textView5.visibility = View.VISIBLE
                                        binding.tvResendOtp.visibility = View.VISIBLE
                                        binding.etPhone.visibility = View.INVISIBLE
                                        binding.btnVerify.visibility = View.VISIBLE
                                        binding.btnSendOtp.visibility = View.INVISIBLE
                                        sessionId = response.Details
                                    }
                                }
                            }catch (e:Exception){
                                Toast.makeText(context,"Error: ${e.message}",Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                }else{
                    Toast.makeText(context,"Enter 10 digit number",Toast.LENGTH_LONG).show()
                }
            }
        }

        binding.btnVerify.setOnClickListener {
            val otp1 = binding.otp1.text.toString()
            val otp2 = binding.otp2.text.toString()
            val otp3 = binding.otp3.text.toString()
            val otp4 = binding.otp4.text.toString()
            val otp5 = binding.otp5.text.toString()
            val otp6 = binding.otp6.text.toString()
            if(otp1.isNotEmpty() && otp2.isNotEmpty() && otp3.isNotEmpty() && otp4.isNotEmpty() && otp5.isNotEmpty() && otp6.isNotEmpty()){
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        withContext(Dispatchers.Main){
                            val response = viewModel.verifyOtp(getString(R.string.otp_apikey),sessionId,otp1+otp2+otp3+otp4+otp5+otp6)
                            if(response.Details=="OTP Matched"){
                                val email = arguments?.getString("email")
                                viewModel.updateOrganizerVerified(getString(R.string.orgRef),true,email.toString(),no)
                                Toast.makeText(context,"Verified",Toast.LENGTH_LONG).show()
                            }else{
                                Toast.makeText(context,"Wrong Otp",Toast.LENGTH_LONG).show()
                            }
                        }
                    }catch(e:Exception){
                        withContext(Dispatchers.Main){
                            Toast.makeText(context,"Error:${e.message}",Toast.LENGTH_SHORT).show()
                        }
                    }

                }
            }
        }
        return  binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding=null
    }
}