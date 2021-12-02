package ind.ev.events.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import androidx.recyclerview.widget.LinearLayoutManager
import ind.ev.events.adapters.EventAdapter
import ind.ev.events.adapters.FormAdapter
import ind.ev.events.databinding.PayDialogBinding

class PayDialogFragment:DialogFragment(),EventAdapter.OnItemClickListener {

    private var _binding:PayDialogBinding?=null
    private val binding get() = _binding!!
    private var pay = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = PayDialogBinding.inflate(inflater,container,false)
        val list = arguments?.getStringArray("FORMDATA")?.toList()!!
        setupRv(list)

        binding.btnCancel.setOnClickListener {
            dismiss()
        }
        binding.btnPay.setOnClickListener {
            pay = true
            dismiss()
        }
        return binding.root
    }

    private fun setupRv(lis:List<String>){
        val formAdapter = FormAdapter(lis,this,true)
        binding.rvFormData.apply {
            adapter = formAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    override fun onItemClick(position: Int) {}


    override fun onDestroy() {
        super.onDestroy()
        _binding =null
        if(pay){
            val bundle = Bundle().apply { putString("isPay","Y") }
            setFragmentResult("PAY",bundle)
        }
    }
}