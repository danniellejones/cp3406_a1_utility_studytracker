/**
 * Timer Adapter to handle multiple count down timers.
 */

package cp3406.a1.studytracker.adapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import cp3406.a1.studytracker.R
import cp3406.a1.studytracker.model.TimerItem

/** Time Adapter to join to study timers to allow multiple count down instances */
class TimerAdapter(
    private val timerItems: ArrayList<TimerItem>
) : RecyclerView.Adapter<TimerAdapter.TimerViewHolder>() {

    /** Inflate layout */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimerViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        return TimerViewHolder(itemView)
    }

    /** Bind timer items */
    override fun onBindViewHolder(holder: TimerViewHolder, position: Int) {
        val timerItem = timerItems[position]
        holder.bind(timerItem)
    }

    /** Get size */
    override fun getItemCount(): Int {
        return timerItems.size
    }

    /** Bind timerItem time value to studyTimeItem time value */
    class TimerViewHolder(
        itemView: View
    ) :
        RecyclerView.ViewHolder(itemView) {

        // Hold the timer item associated with this view holder
        private lateinit var timerItem: TimerItem
        private val timerLabel: TextView = itemView.findViewById(R.id.time_count)

        fun bind(timerItem: TimerItem) {

            // Bind the timer item to the view holder
            this.timerItem = timerItem

            // Update the view holder UI to match the timer item state
            timerLabel.text = timerItem.timerTime
        }
    }
}
