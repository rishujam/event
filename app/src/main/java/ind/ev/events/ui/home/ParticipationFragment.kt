package ind.ev.events.ui.home

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import ind.ev.events.databinding.ParticipationFragmentBinding
import ind.ev.events.models.Event
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ind.ev.events.ui.HomeActivity
import ind.ev.events.MainViewModel
import ind.ev.events.R

@AndroidEntryPoint
class ParticipationFragment:Fragment() {

    private var _binding:ParticipationFragmentBinding?=null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by viewModels()
    private lateinit var event:Event

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ParticipationFragmentBinding.inflate(inflater,container,false)
        val accType = arguments?.getString("type")
        val id = arguments?.getString("eventId")
        if(accType=="Organizer"){
            binding.btnParticipate.setBackgroundColor(Color.GRAY)
        }

        CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.Main){
                try{
                    val collection = getString(R.string.eventRef)
                    event = viewModel.showEvent(collection,id.toString())
                    binding.tvTitle.text = event.title
                    binding.tvTime.text = event.time
                    binding.tvDate.text = event.date
                    binding.tvDescription.text = event.description
                    binding.tvSkills.text = event.skillReq
                    if(event.fees!="0"){
                        binding.tvFee.text  = "₹${event.fees}"
                    }else{
                        binding.tvFee.text = "Free"
                        binding.btnParticipate.text = "Participate for free"
                    }
                }catch (e:Exception){
                    Toast.makeText(context,e.message,Toast.LENGTH_LONG).show()
                }
            }
        }

        binding.ibBack.setOnClickListener {
            (activity as HomeActivity).supportFragmentManager.popBackStack()
        }



        binding.btnParticipate.setOnClickListener {
            if(accType=="Participant"){
                val  regFormFragment = RegFormFragment()
                val bundle = Bundle()
                bundle.putString("PAY",event.fees)
                bundle.putString("eventId",event.id)
                regFormFragment.arguments = bundle
                (activity as HomeActivity).setCurrentFragmentBack(regFormFragment)
            }else{
                Toast.makeText(context,"Organizers can't participate",Toast.LENGTH_SHORT).show()
            }
        }
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        try{
            setFragmentResultListener("KEY") { requestKey, bundle ->
                val result = bundle.getString("RESULT")
                if(result=="Success"){
                    val resultBundle = Bundle().apply { putString("PARTICIPATED","Y") }
                    setFragmentResult("DONE", resultBundle)
                    (activity as HomeActivity).supportFragmentManager.popBackStack()
                }
            }
        }catch (e:Exception){
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding=null
    }

}