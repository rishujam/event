package ind.ev.events.ui.history

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
import ind.ev.events.databinding.PartiHistoryFragmentBinding
import ind.ev.events.models.Event
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PartiHistoryFragment:Fragment(),EventAdapter.OnItemClickListener {

    private var _binding:PartiHistoryFragmentBinding?=null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by viewModels()
    private lateinit var myEventsAdapter: MyEventsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding= PartiHistoryFragmentBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val email = arguments?.getString("email").toString()
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val events  = viewModel.getEventsForParticipant(getString(R.string.form_data),email)
                if(events.isEmpty()){
                    binding.tvEmptyRv.visibility = View.VISIBLE
                }else{
                    setupRv(events)
                }
            }catch (e:Exception){
                Toast.makeText(context,"Error: ${e.message}",Toast.LENGTH_SHORT).show()
            }

        }
    }

    private fun setupRv(lis:List<Event>){
        if(lis.isEmpty()){
            binding.tvEmptyRv.visibility = View.VISIBLE
        }else{
            myEventsAdapter = MyEventsAdapter(lis,this)
            binding.rvMyEventsP.apply {
                adapter = myEventsAdapter
                layoutManager = LinearLayoutManager(context)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding=null
    }

    override fun onItemClick(position: Int) {
        val ticketFragment = TicketFragment()
        (activity as HomeActivity).setCurrentFragmentBack(ticketFragment)
    }
}