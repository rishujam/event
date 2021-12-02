package ind.ev.events.ui.home

import android.app.AlertDialog
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
import androidx.recyclerview.widget.LinearLayoutManager
import ind.ev.events.ui.HomeActivity
import ind.ev.events.R
import ind.ev.events.adapters.EventAdapter
import ind.ev.events.adapters.FormAdapter
import ind.ev.events.databinding.CreateFormFragmentBinding

class CreateFormFragment:Fragment(),EventAdapter.OnItemClickListener {

    private var _binding:CreateFormFragmentBinding?=null
    private val binding get() = _binding!!
    private lateinit var formAdapter:FormAdapter
    private val form = mutableListOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = CreateFormFragmentBinding.inflate(inflater,container,false)

        binding.btnAddField.setOnClickListener {
            val alertDialog: AlertDialog.Builder = AlertDialog.Builder(context)
            alertDialog.setTitle("Create New Field")
            alertDialog.setMessage("Set hint of the edittext.")
            val input = EditText(context)
            input.setBackgroundResource(R.drawable.custom_edittext)
            input.setPadding(32)
            val lp = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
            lp.setMargins(32,8,32,8)
            input.layoutParams = lp
            alertDialog.setView(input)
            alertDialog.setIcon(R.drawable.ic_add)
            alertDialog.setPositiveButton("Add"){_,_ ->
                val hint = input.text.toString()
                if(hint.isNotEmpty()){
                    alertDialog.setOnDismissListener {
                        it.dismiss()
                    }
                    form.add(hint)
                    setupRv(form)
                }
            }
            alertDialog.setNegativeButton("Cancel"){_,_ ->

            }
            alertDialog.create().show()
        }

        binding.btnSaveForm.setOnClickListener {
            if(form.isNotEmpty()){
                (activity as HomeActivity).supportFragmentManager.popBackStack()
            }else{
                Toast.makeText(context,"Enter minimum 1 Field",Toast.LENGTH_SHORT).show()
            }
        }
        return binding.root
    }

    private fun setupRv(lis:List<String>){
        formAdapter = FormAdapter(lis,this,false)
        formAdapter.notifyDataSetChanged()
        binding.rvFormFields.apply {
            adapter = formAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    override fun onItemClick(position: Int) {
        val element = formAdapter.hints[position]
        form.remove(element)
        formAdapter.notifyDataSetChanged()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding=null
        if(form.isNotEmpty()){
            val resultBundle = Bundle().apply { putStringArray("RESULT", form.toTypedArray()) }
            setFragmentResult("KEY", resultBundle)
        }
    }
}