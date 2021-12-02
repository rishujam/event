package ind.ev.events.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ind.ev.events.databinding.ItemMyeventBinding
import ind.ev.events.models.Event

class MyEventsAdapter(
    val events:List<Event>,
    val listener:EventAdapter.OnItemClickListener
) : RecyclerView.Adapter<MyEventsAdapter.MyEventsViewHolder>(){

    inner class MyEventsViewHolder(val binding:ItemMyeventBinding):RecyclerView.ViewHolder(binding.root),View.OnClickListener{

        init {
            binding.cdMyEvent.setOnClickListener(this)
        }
        override fun onClick(v: View?) {
            val position = adapterPosition
            if(position!=RecyclerView.NO_POSITION){
                listener.onItemClick(position)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyEventsViewHolder {
        return MyEventsViewHolder(ItemMyeventBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: MyEventsViewHolder, position: Int) {
        val event = events[position]
        holder.binding.apply {
            tvFeeMyE.text="₹${event.fees}"
            tvLocationMyE.text =event.location
            tvTitleMyE.text = event.title
            tvDateMyE.text = event.date
        }
    }

    override fun getItemCount(): Int {
        return events.size
    }
}