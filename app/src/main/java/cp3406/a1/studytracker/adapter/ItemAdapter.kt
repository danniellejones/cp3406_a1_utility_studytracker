package cp3406.a1.studytracker.adapter

import android.app.AlertDialog
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import cp3406.a1.studytracker.R
import cp3406.a1.studytracker.model.StudyTimer

class ItemAdapter(val c: Context, private val dataset: MutableList<StudyTimer>) :
    RecyclerView.Adapter<ItemAdapter.ItemViewHolder>() {

    private var itemActionListener: OnItemActionListener? = null

    fun setOnItemActionListener(listener: OnItemActionListener) {
        itemActionListener = listener
    }

    interface OnItemActionListener {
        fun onItemUpdated(item: StudyTimer, position: Int)
        fun onItemRemoved(position: Int)
    }



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
        holder.titleLabel.text = item.studyTimeTitle
        holder.timeLabel.text = item.studyTimerTime
    }

    inner class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var titleLabel: TextView
        val timeLabel: TextView
        var erMenu: TextView


        init {
            titleLabel = itemView.findViewById(R.id.item_title)
            timeLabel = itemView.findViewById(R.id.time_count)
            erMenu = itemView.findViewById(R.id.edit_or_remove_menu)
            erMenu.setOnClickListener { popupMenus(itemView) }
        }

        private fun popupMenus(v: View) {
            val position = dataset[adapterPosition]
            val popupMenus = PopupMenu(c, v)
            popupMenus.inflate(R.menu.show_menu)
            popupMenus.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.edit_text -> {
                        val v = LayoutInflater.from(c).inflate(R.layout.add_item, null)
                        val titleEditText = v.findViewById<EditText>(R.id.new_title)
                        val timeEditText = v.findViewById<EditText>(R.id.new_time)
                        titleEditText.setText(position.studyTimeTitle)
                        timeEditText.setText(position.studyTimerTime)

                        AlertDialog.Builder(c).setView(v).setPositiveButton("Save") { dialog, _ ->
                            val newTitle = titleEditText.text.toString()
                            val newTime = timeEditText.text.toString()
                            position.studyTimeTitle = newTitle
                            position.studyTimerTime = newTime
//                            position.studyTimeTitle = titleLabel.text.toString()
//                            position.studyTimerTime = timeLabel.text.toString()
                            titleLabel.text = newTitle
                            timeLabel.text = newTime
                            Log.i("ItemAdapter", "Title: ${position.studyTimeTitle}")
                            Log.i("ItemAdapter", "Edited: $position")
                            itemActionListener?.onItemUpdated(position, adapterPosition)
                            notifyDataSetChanged()
                            Toast.makeText(c, "Successfully Updated", Toast.LENGTH_LONG).show()
                            dialog.dismiss()
                        }
                            .setNegativeButton("Cancel") { dialog, _ ->
                                dialog.dismiss()

                            }
                            .create()
                            .show()
                        Toast.makeText(c, "Edit clicked", Toast.LENGTH_LONG).show()
                        true

                    }
                    R.id.delete -> {
                        AlertDialog.Builder(c)
                            .setTitle("Delete")
                            .setIcon(R.drawable.warning_icon)
                            .setMessage("Are you sure you want to delete?")

                            .setPositiveButton("Confirm") { dialog, _ ->
                                dataset.removeAt(adapterPosition)
                                itemActionListener?.onItemRemoved(adapterPosition)
                                notifyDataSetChanged()
                                Toast.makeText(c, "Successfully Deleted", Toast.LENGTH_LONG).show()
                                dialog.dismiss()
                            }
                            .setNegativeButton("Cancel") { dialog, _ ->
                                dialog.dismiss()

                            }
                            .create()
                            .show()

                        Toast.makeText(c, "Delete clicked", Toast.LENGTH_LONG).show()
                        true
                    }
                    else -> true
                }
            }
            popupMenus.show()
            val popup = PopupMenu::class.java.getDeclaredField("mPopup")
            popup.isAccessible = true
            val menu = popup.get(popupMenus)
            menu.javaClass.getDeclaredMethod("setForceShowIcon", Boolean::class.java)
                .invoke(menu, true)
        }
    }
}
