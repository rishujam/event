package ind.ev.events.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ind.ev.events.databinding.ItemEventBinding
import ind.ev.events.models.Event

class EventAdapter(
    val events:List<Event>,
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<EventAdapter.EventViewHolder>() {

    inner class EventViewHolder(val binding:ItemEventBinding):RecyclerView.ViewHolder(binding.root),View.OnClickListener{
        init {
            binding.cdEvent.setOnClickListener(this)
        }
        override fun onClick(v: View?) {
            val position = adapterPosition
            if(position!=RecyclerView.NO_POSITION){
                listener.onItemClick(position)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        return EventViewHolder(ItemEventBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        holder.binding.apply {
            val event = events[position]
            itemTitle.text = event.title
            itemCategory.text = event.category
            itemLocation.text = event.location
            itemDate.text = event.date
        }
    }

    override fun getItemCount(): Int {
        return events.size
    }

    interface OnItemClickListener{
        fun onItemClick(position: Int)
    }
}