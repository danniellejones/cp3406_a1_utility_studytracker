package cp3406.a1.studytracker.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
        holder.title_label.text = context.resources.getString(item.stringResourceId)
        holder.time_label.text = context.resources.getString(item.stringResourceId)
        holder.input_hour_label.text = context.resources.getString(item.stringResourceId)
        holder.input_minute_label.text = context.resources.getString(item.stringResourceId)
    }

    class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        public TextView title_label
        public TextView time_label
        public EditText input_hour_label
        public EditText input_minute_label

        public ItemViewHolder(View itemView) {
            super(itemView);
            title_label = itemView.findViewById(R.id.title_label)
            time_label = itemView.findViewById(R.id.time_label)
            input_hour_label = itemView.findViewById(R.id.input_hour_label)
            input_minute_label = itemView.findViewById(R.id.input_minute_label)
            title_label = itemView.findViewById(R.id.title_label)

        }
    }
}