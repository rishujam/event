package ind.ev.events.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ind.ev.events.databinding.ItemFormBinding

class FormAdapter(
    val hints:List<String>,
    private val listener: EventAdapter.OnItemClickListener,
    private val isPay:Boolean
):RecyclerView.Adapter<FormAdapter.FormViewHolder>() {

    inner class FormViewHolder(val binding:ItemFormBinding):RecyclerView.ViewHolder(binding.root),View.OnClickListener{
        init {
            binding.ibDelete.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val position = adapterPosition
            if(position!=RecyclerView.NO_POSITION){
                listener.onItemClick(position)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FormViewHolder {
        return FormViewHolder(ItemFormBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: FormViewHolder, position: Int) {
        holder.binding.apply {
            val item = hints[position]
            etHint.hint = item
            if(isPay){
                val field = item.split("#")[0]
                val data = item.split("#")[1]
                etHint.isFocusable = false
                ibDelete.visibility = View.INVISIBLE
                etHint.setText("${field}: ${data}")
            }
        }
    }

    override fun getItemCount(): Int {
        return hints.size
    }
}