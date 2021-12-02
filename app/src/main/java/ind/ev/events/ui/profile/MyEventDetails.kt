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
import ind.ev.events.databinding.MyeventDetailFragmentBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MyEventDetails:Fragment() {

    private var _binding:MyeventDetailFragmentBinding?=null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = MyeventDetailFragmentBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val id = arguments?.getString("eventId").toString()
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val participants = viewModel.getRegisteredUserForEvent(getString(R.string.partiRef),id)

            }catch (e:Exception){
                Toast.makeText(context,"Error: ${e.message}",Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding=null
    }
}