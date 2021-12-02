package ind.ev.events.ui.home

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TimePicker
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import ind.ev.events.ui.HomeActivity
import ind.ev.events.MainViewModel
import ind.ev.events.R
import ind.ev.events.databinding.PosteventFragmentBinding
import ind.ev.events.models.Event
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class PostEventFragment:Fragment() {

    private var _binding:PosteventFragmentBinding?=null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by viewModels()
    private var cal = Calendar.getInstance()
    private var currFile: Uri? = null
    private lateinit var hintArray:List<String>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding= PosteventFragmentBinding.inflate(inflater,container,false)

        val email = arguments?.getString("email")
        val dateSetListener =
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                val myFormat = "dd/MM/yyyy"
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                binding.selectDate.text = sdf.format(cal.time)
            }
        val mTimePicker: TimePickerDialog
        val mcurrentTime = Calendar.getInstance()
        val hour = mcurrentTime.get(Calendar.HOUR_OF_DAY)
        val minute = mcurrentTime.get(Calendar.MINUTE)

        mTimePicker = TimePickerDialog(context, object : TimePickerDialog.OnTimeSetListener {
            override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
                binding.selectTime.text = String.format("%d : %d", hourOfDay, minute)
            }
        }, hour, minute, false)

        binding.selectDate.setOnClickListener {
            context?.let {
                DatePickerDialog(
                    it,
                    dateSetListener,
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)
                ).show()
            }
        }
        binding.selectTime.setOnClickListener {
            mTimePicker.show()
        }

        binding.rbFree.setOnClickListener {
            binding.etFees.visibility = View.INVISIBLE
            binding.textView2.visibility = View.INVISIBLE
        }

        binding.rbPaid.setOnClickListener {
            binding.etFees.visibility = View.VISIBLE
            binding.textView2.visibility = View.VISIBLE
        }

        val getContent = registerForActivityResult(ActivityResultContracts.GetContent())  { uri: Uri? ->
            currFile = uri
            binding.ivEvIcon.setImageURI(uri)
        }
        binding.imageButton.setOnClickListener {
            getContent.launch("image/*")
        }

        binding.btnSendToVerify.setOnClickListener {

            val title = binding.etTitle.text.toString()
            val location = binding.etLocation.text.toString()
            val skills = binding.etSkills.text.toString()
            val description= binding.etDescription.text.toString()
            val category = binding.spCategory.selectedItem.toString()
            val date = binding.selectDate.text.toString()
            val time = binding.selectTime.text.toString()
            var fee = "0"
            val feeCheck = binding.rbFree.isChecked || binding.rbPaid.isChecked
            if(binding.rbPaid.isChecked){
                fee = binding.etFees.text.toString()
            }
            if(currFile!=null){
                if(title.isNotEmpty() && location.isNotEmpty() && skills.isNotEmpty() && description.isNotEmpty() && category!="Select Category"){
                    if(date!="Select date" && time!="Select time"){
                        if(feeCheck){
                            CoroutineScope(Dispatchers.IO).launch {
                                try {
                                    withContext(Dispatchers.Main){
                                        binding.pbPostEvent.visibility = View.VISIBLE
                                        val previousId = viewModel.getLastEventId(getString(R.string.utils))
                                        val id = previousId.toLong() +1
                                        val tsLong = System.currentTimeMillis() / 1000
                                        val event = Event(
                                            title,
                                            description,
                                            location,
                                            date,
                                            time,
                                            skills,
                                            fee,
                                            category,
                                            tsLong.toString(),
                                            false,
                                            email.toString(),
                                            false,
                                            id.toString()
                                        )
                                        viewModel.sendEventForVerification(getString(R.string.eventRef),event)
                                        viewModel.updateLastEventId(getString(R.string.utils),id.toString())
                                        viewModel.uploadRegForm(getString(R.string.form_ref),id.toString(),hintArray)
                                        binding.pbPostEvent.visibility = View.INVISIBLE
                                        Toast.makeText(context,"Sent", Toast.LENGTH_SHORT).show()
                                    }
                                }catch (e:Exception){
                                    withContext(Dispatchers.Main){
                                        Toast.makeText(context,e.message, Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        }else{
                            Toast.makeText(context,"Enter fee details", Toast.LENGTH_SHORT).show()
                        }
                    }else{
                        Toast.makeText(context,"Enter date and time", Toast.LENGTH_SHORT).show()
                    }
                }else{
                    Toast.makeText(context,"Enter details properly", Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(context,"Set icon of the event", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnCreate.setOnClickListener {
            val createFormFragment = CreateFormFragment()
            (activity as HomeActivity).setCurrentFragmentBack(createFormFragment)
        }
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding=null
    }

    override fun onStart() {
        super.onStart()
        setFragmentResultListener("KEY") { reqKey, bundle ->
            if (reqKey == "KEY")
            {
                val result = bundle.getStringArray("RESULT")
                if(result!=null){
                    binding.btnCreate.text = "Formxx.text"
                    binding.btnCreate.setTextColor(Color.GRAY)
                    hintArray = result.toList()
                }
            }
        }
    }
}