package ind.ev.events.ui.home

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import ind.ev.events.*
import ind.ev.events.adapters.EventAdapter
import ind.ev.events.databinding.SearchEventFargBinding
import ind.ev.events.models.Event
import ind.ev.events.ui.HomeActivity
import kotlinx.coroutines.*

const val SEARCH_NEWS_TIME_DELAY=500L

@AndroidEntryPoint
class SearchEventFragment:Fragment(), EventAdapter.OnItemClickListener {

    private var _binding :SearchEventFargBinding?=null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by viewModels()
    private lateinit var eventAdapter:EventAdapter
    private lateinit var accType:String

    override fun onItemClick(position: Int) {
        val bundle = Bundle()
        val id = eventAdapter.events[position].id
        bundle.putString("type",accType)
        bundle.putString("eventId",id)
        val participationFragment = ParticipationFragment()
        participationFragment.arguments = bundle
        (activity as HomeActivity).setCurrentFragmentBack(participationFragment)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding  = SearchEventFargBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var email = ""
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val localProfile = viewModel.getProfileLocal()
                email = localProfile[0].email
                accType = localProfile[0].accType
                val events = viewModel.getFeaturedEvents(getString(R.string.eventRef))
                setupRv(events)
                if(accType=="Organizer"){
                    binding.btnPostEvent.visibility = View.VISIBLE
                    val profile = viewModel.getProfile(email,getString(R.string.orgRef))
                    if (!profile.verify){
                        binding.btnPostEvent.setBackgroundColor(Color.GRAY)
                        binding.btnPostEvent.isClickable =false
                    }
                }
            }catch (e:Exception){
                Toast.makeText(context,"Cant load events: ${e.message}",Toast.LENGTH_SHORT).show()
            }
        }

//        var job: Job?=null
//        binding.etSearchText.addTextChangedListener { editable ->
//            job?.cancel()
//            job = MainScope().launch {
//                delay(SEARCH_NEWS_TIME_DELAY)
//                editable?.let {
//                    if(editable.toString().isNotEmpty() && binding.etSearchText.hasFocus()){
//                        try {
//                            val events  = viewModel.searchEvents(getString(R.string.eventRef),editable.toString())
//                            setupRv(events)
//                        }catch (e:Exception){
//                            Toast.makeText(context,"No Result: ${e.message}",Toast.LENGTH_SHORT).show()
//                        }
//                    }
//                }
//            }
//        }
        binding.btnPostEvent.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("email",email)
            val postEventFragment = PostEventFragment()
            postEventFragment.arguments = bundle
            (activity as HomeActivity).setCurrentFragmentBack(postEventFragment)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding=  null
    }

    override fun onStart() {
        super.onStart()
        try {
            setFragmentResultListener("DONE") { requestKey, bundle ->
                val result = bundle.getString("PARTICIPATED")
                if(result=="Y"){
                    Toast.makeText(context,"Final Success",Toast.LENGTH_LONG).show()
                }
            }
        }catch (e:Exception){ }
    }

    private fun setupRv(list:List<Event>){
        eventAdapter = EventAdapter(list,this)
        binding.recyclerView.apply {
            adapter = eventAdapter
            layoutManager = LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false)
        }
    }
}