package cp3406.a1.studytracker.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import cp3406.a1.studytracker.R
import cp3406.a1.studytracker.model.TimerItem

class TimerAdapter(
    private val items: List<TimerItem>,
    private val recyclerView: RecyclerView,
    private val onTogglePlay: (TimerItem, TimerViewHolder) -> Unit,
) : RecyclerView.Adapter<TimerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimerViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)

        // Use lambda function to take position and isRunning and call onTogglePlay function
        return TimerViewHolder(itemView, parent.context) { timerItem, holder ->
            onTogglePlay(
                timerItem,
                holder
            )
        }
    }

    fun onPlayButtonClick(position: Int) {
        val timerItem = items[position]
        val holder = recyclerView.findViewHolderForAdapterPosition(position) as TimerViewHolder
        onTogglePlay(timerItem, holder)
    }

    override fun onBindViewHolder(holder: TimerViewHolder, position: Int) {
        val timerItem = items[position]
        holder.bind(timerItem)
    }

    override fun getItemCount(): Int = items.size
}
