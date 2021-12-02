package ind.ev.events.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import ind.ev.events.ui.HomeActivity
import ind.ev.events.MainViewModel
import ind.ev.events.R
import ind.ev.events.adapters.EventAdapter
import ind.ev.events.adapters.MyEventsAdapter
import ind.ev.events.databinding.MyEventsFragmentBinding
import ind.ev.events.models.Event
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MyEventsFragment:Fragment(), EventAdapter.OnItemClickListener {

    private var _binding:MyEventsFragmentBinding?=null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by viewModels()
    private lateinit var myEventAdapter:MyEventsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding= MyEventsFragmentBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val email = arguments?.getString("email").toString()

        CoroutineScope(Dispatchers.Main).launch {
            try {
                val events = viewModel.getEventsOfOrg(getString(R.string.eventRef),email)
                if(events.isEmpty()){
                    binding.tvShowEmpty.visibility = View.VISIBLE
                }else{
                    setUpRv(events)
                }
            }catch (e:Exception){
                Toast.makeText(context,"Error: ${e.message}",Toast.LENGTH_SHORT).show()
            }
        }

        binding.ibBack.setOnClickListener {
            (activity as HomeActivity).supportFragmentManager.popBackStack()
        }
    }

    private fun setUpRv(list:List<Event>){
        myEventAdapter = MyEventsAdapter(list,this)
        binding.rvMyEvents.apply {
            adapter = myEventAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    override fun onItemClick(position: Int) {
        val bundle =Bundle()
        val id = myEventAdapter.events[position].id
        bundle.putString("eventId",id)
        val myEventDetails = MyEventDetails()
        myEventDetails.arguments = bundle
        (activity as HomeActivity).setCurrentFragmentBack(myEventDetails)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding=null
    }
}