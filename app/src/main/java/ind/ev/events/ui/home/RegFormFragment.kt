package ind.ev.events.ui.home

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.view.setPadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import com.razorpay.Checkout
import dagger.hilt.android.AndroidEntryPoint
import ind.ev.events.ui.HomeActivity
import ind.ev.events.MainViewModel
import ind.ev.events.R
import ind.ev.events.databinding.RegformFragmentBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class RegFormFragment:Fragment() {

    private var _binding:RegformFragmentBinding?=null
    private val binding get() = _binding!!
    private var numberOfLines=0
    private val formData = mutableListOf<String>()
    private val viewModel: MainViewModel by viewModels()
    private var taskDone=""
    private lateinit var form:List<String>
    private lateinit var email:String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = RegformFragmentBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val eventId = arguments?.getString("eventId").toString()
        val pay = arguments?.getString("PAY").toString()

        // loading registration form of the event
        CoroutineScope(Dispatchers.Main).launch {
            try {
                binding.progressBar2.visibility = View.VISIBLE
                form = viewModel.getRegForm(getString(R.string.form_ref),eventId)
                makeForm(form)
                email = viewModel.getProfileLocal()[0].email
                binding.progressBar2.visibility = View.INVISIBLE
            }catch (e:Exception){
                Toast.makeText(context,"Can't load form: ${e.message}",Toast.LENGTH_SHORT).show()
            }
        }

        // preloading razorpay if the event is paid
        if(pay!="0"){
            Checkout.preload(context?.applicationContext)
        }

        binding.btnSave.setOnClickListener {
            for(i in form){
                val id = form.indexOf(i)+1
                val x = binding.root.findViewById<EditText>(id).text.toString()
                formData.add("${i}#${x}")
            }
            //if event is paid then payment will start and after successful payment form data will be uploaded
            // and user will be added to participation list this process is done in onResume.
            if(pay!="0"){
                startPayment("1000")
            }
            // if its not paid data will be uploaded here.
            if(pay=="0"){
                CoroutineScope(Dispatchers.Main).launch {
                    try {
                        binding.progressBar2.visibility = View.VISIBLE
                        val date = Calendar.getInstance().time
                        val formatter = SimpleDateFormat.getDateTimeInstance()
                        val formattedDate = formatter.format(date)
                        viewModel.uploadFormData(getString(R.string.form_data),id.toString(),formData,email,formattedDate)
                        viewModel.addToParticipantLis(getString(R.string.partiRef),id.toString(),email)
                        binding.progressBar2.visibility = View.INVISIBLE
                    }catch (e:Exception){
                        Toast.makeText(context,"Error: ${e.message}",Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun makeForm(hints:List<String>) {
        val ll = binding.llForm
        for(i in hints){
            val et = EditText(context)
            val p = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            p.setMargins(0,16,0,8)
            et.layoutParams = p
            et.hint = i
            et.setBackgroundResource(R.drawable.custom_edittext)
            et.setPadding(16)
            et.id = numberOfLines + 1
            ll.addView(et)
            numberOfLines++
        }
    }


    private fun startPayment(amount:String) {
        val activity: Activity = (activity as HomeActivity)
        val co = Checkout()
        co.setKeyID(getString(R.string.razor_pay))

        try {
            val options = JSONObject()
            options.put("name","Razorpay Corp")
            options.put("description","Demoing Charges")
            options.put("image","https://s3.amazonaws.com/rzp-mobile/images/rzp.png")
            options.put("theme.color", "#3399cc")
            options.put("currency","INR")
            options.put("amount",amount.toInt()*100)

            val retryObj =  JSONObject()
            retryObj.put("enabled", true);
            retryObj.put("max_count", 4);
            options.put("retry", retryObj);

            val prefill = JSONObject()
            prefill.put("email","rishuparashar7@gmail.com")
            prefill.put("contact","8076861086")
            options.put("prefill",prefill)
            co.open(activity,options)
        }catch (e: Exception){
            Toast.makeText(activity,"Error in payment: "+ e.message, Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }

    override fun onResume() {
        super.onResume()
        if((activity as HomeActivity).successListener=="Y"){
            val id = arguments?.getString("eventId")
            val date = Calendar.getInstance().time
            val formatter = SimpleDateFormat.getDateTimeInstance()
            val formattedDate = formatter.format(date)
            CoroutineScope(Dispatchers.IO).launch {
                withContext(Dispatchers.Main){
                    try {
                        binding.progressBar2.visibility = View.VISIBLE
                        viewModel.addToParticipantLis(getString(R.string.partiRef),id.toString(),email)
                        viewModel.uploadFormData(getString(R.string.form_data),id.toString(),formData,email,formattedDate)
                        binding.progressBar2.visibility = View.INVISIBLE
                        taskDone="Y"
                        (activity as HomeActivity).supportFragmentManager.popBackStack()
                    }catch (e:Exception){
                        Toast.makeText(context,"Error: ${e.message}",Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding=null
        if(taskDone=="Y"){
            val resultBundle = Bundle().apply { putString("RESULT","Success") }
            setFragmentResult("KEY", resultBundle)
        }
    }
}