package cp3406.a1.studytracker.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import cp3406.a1.studytracker.R
import cp3406.a1.studytracker.model.StudyTimer

class ItemAdapter(private val context: Context, private val dataset: List<StudyTimer>) :
    RecyclerView.Adapter<ItemAdapter.ItemViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val adapterView =
            LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)

        return ItemViewHolder(adapterView)
    }

    override fun getItemCount(): Int {
        return dataset.size
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = dataset[position]
        holder.title_label.text = item.studyTimeTitle
        holder.time_label.text = item.studyTimerTime
    }

    class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val title_label : TextView = itemView.findViewById(R.id.item_title)
        val time_label : TextView = itemView.findViewById(R.id.time_count)
    }
}
