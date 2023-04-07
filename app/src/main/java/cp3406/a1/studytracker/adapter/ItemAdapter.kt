package cp3406.a1.studytracker.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import cp3406.a1.studytracker.R
import cp3406.a1.studytracker.model.StudyTimer

class ItemAdapter(private val context: Context, private val dataset: List<StudyTimer>) :
    RecyclerView.Adapter<ItemAdapter.ItemViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val adapterLayout =
            LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)

        return ItemViewHolder(adapterLayout)
    }

    override fun getItemCount(): Int {
        return dataset.size
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = dataset[position]
        holder.title_label.text = item.studyTimeTitle
        holder.time_label.text = item.studyTimerTime
//        holder.input_hour_label.hint = context.resources.getString(item.inputHourResourceId)
//        holder.input_minute_label.hint = context.resources.getString(item.inputMinuteResourceId)
    }

    class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val title_label : TextView = itemView.findViewById(R.id.item_title)
        val time_label : TextView = itemView.findViewById(R.id.time_count)
//        val input_hour_label : EditText = itemView.findViewById(R.id.input_hour)
//        val input_minute_label : EditText = itemView.findViewById(R.id.input_minute)

    }
}
