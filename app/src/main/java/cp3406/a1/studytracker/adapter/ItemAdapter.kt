package cp3406.a1.studytracker.adapter

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.RecyclerView
import cp3406.a1.studytracker.R
import cp3406.a1.studytracker.model.StudyTimer
import java.util.concurrent.TimeUnit

class ItemAdapter(val c: Context, private val dataset: MutableList<StudyTimer>) :
    RecyclerView.Adapter<ItemAdapter.ItemViewHolder>() {

    private lateinit var sharedPreferences: SharedPreferences

    private var itemActionListener: OnItemActionListener? = null
    var onCountDownStateChangedListener: OnCountDownStateChangedListener? = null
    private var isCountingDown: Boolean = false
    private var countDownTimer: CountDownTimer? = null
    private var countDownTime: Long = 0
    private var countDownTimeLeft: Long = 0


    fun setOnItemActionListener(listener: OnItemActionListener) {
        itemActionListener = listener
    }

    interface OnItemActionListener {
        fun onItemUpdated(item: StudyTimer, position: Int)
        fun onItemRemoved(position: Int)
    }

    interface OnCountDownStateChangedListener {
        fun onCountDownStateChanged(isCountingDown: Boolean)
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
        holder.playButton.setOnClickListener {
            Log.i("ItemAdapter", "Initialised isCountingDown = $isCountingDown")
            toggleCountDownPlay(holder.playButton, holder.timeLabel, item, holder)
            onCountDownStateChangedListener?.onCountDownStateChanged(isCountingDown)
        }
    }

    private fun toggleCountDownPlay(countDownButton: Button, timeLabel: TextView, item: StudyTimer, holder: ItemViewHolder) {
        // Change from play to stop icon
        isCountingDown = !isCountingDown
        countDownButton.setBackgroundResource(if (isCountingDown) R.drawable.stop_icon else R.drawable.play_icon)

        // Retrieve the entered time and start/stop timer
        Log.i("ItemAdapter", "HERE isCountingDown = $isCountingDown")
        if (isCountingDown) {

            Log.i("ItemAdapter", "CountDown Started")

            //Retrieve, validate text is in time format and format for use
            var timeStr = timeLabel.text.toString().trim()
            if (!isValidTime(timeStr)) {
                Log.d("ItemAdapter", "Invalid time format: $timeStr")
                isCountingDown = false
                return
            }
            timeStr = formatTimeString(timeStr)

            Log.i("ItemAdapter", "timeStr: $timeStr")

            // Split string from text view and assign to days, hours, minutes and seconds
            val timeUnits = mutableListOf("sDays", "sHours", "sMins", "sSecs")
            val timeValues = timeStr.split(":").toTypedArray()

            for (i in timeUnits.indices) {
                val unit = timeUnits[i]
                val value = if (i < timeValues.size) timeValues[i] else "00"
                println("$unit: $value")
            }

            val sDays: String = timeValues[0]
            val sHours: String = timeValues[1]
            val sMins: String = timeValues[2]
            val sSecs: String = timeValues[3]

            Log.d("ItemAdapter", "daysStr = $sDays")
            Log.d("ItemAdapter", "hoursStr = $sHours")
            Log.d("ItemAdapter", "minutesStr = $sMins")
            Log.d("ItemAdapter", "secondsStr = $sSecs")

            // Convert and calculate milliseconds for count down timer
            val daysInMs: Long = TimeUnit.DAYS.toMillis(sDays.toLong())
            val hoursInMs: Long = TimeUnit.HOURS.toMillis(sHours.toLong())
            val minutesInMs: Long = TimeUnit.MINUTES.toMillis(sMins.toLong())
            val secondsInMs: Long = TimeUnit.SECONDS.toMillis(sSecs.toLong())

            Log.d("ItemAdapter", "daysinms = $daysInMs")
            Log.d("ItemAdapter", "hoursinms = $hoursInMs")
            Log.d("ItemAdapter", "minutesinms = $minutesInMs")
            Log.d("ItemAdapter", "secondssinms = $secondsInMs")

            val timeMillis: Long = daysInMs + hoursInMs + minutesInMs + secondsInMs
            countDownTime = timeMillis
            val test: Long = timeMillis
            Log.i("ItemAdapter", "timeMillis: $test")
            countDownTimeLeft = countDownTime

            // Use count down timer and display on text view
            countDownTimer = object : CountDownTimer(countDownTimeLeft, 1000) {

                override fun onTick(millisUntilFinished: Long) {
                    countDownTimeLeft = millisUntilFinished
                    val days = TimeUnit.MILLISECONDS.toDays(millisUntilFinished)
                    val hours =
                        TimeUnit.MILLISECONDS.toHours(millisUntilFinished) - TimeUnit.DAYS.toHours(
                            days
                        )
                    val minutes =
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(
                            hours
                        ) - TimeUnit.DAYS.toMinutes(days)
                    val seconds =
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(
                            minutes
                        ) - TimeUnit.HOURS.toSeconds(hours) - TimeUnit.DAYS.toSeconds(days)

                    timeLabel.text =
                        String.format("%02d:%02d:%02d:%02d", days, hours, minutes, seconds)
                }
                override fun onFinish() {
                    println("Finished")
                    countDownButton.setBackgroundResource(R.drawable.play_icon)

                    // Set textView to zero and allow chance to edit, if not item is auto-removed
                    item.studyTimerTime = timeLabel.text.toString()
                    itemActionListener?.onItemRemoved(holder.adapterPosition)
                    notifyDataSetChanged()
                    Toast.makeText(c, "You finished, well done!", Toast.LENGTH_LONG).show()
                    isCountingDown = false
                    Log.i("ItemAdapter", "CountDown Stopped")
                }
            }.start()
        } else {
            countDownTimer?.cancel()
            // Update textView with new time
            item.studyTimerTime = timeLabel.text.toString()
            itemActionListener?.onItemUpdated(item, holder.adapterPosition)
            notifyDataSetChanged()
        }
    }

    private fun isValidTime(timeStr: String): Boolean {
        return timeStr.matches("[\\d:]+".toRegex())
    }

    private fun formatTimeString(timeStr: String): String {
        val parts = timeStr.split(":")
        val formattedTimeStr: String = when (parts.size) {
            1 -> "0:0:0:$timeStr"
            2 -> "0:0:${parts[0]}:${parts[1]}"
            3 -> "0:${parts[0]}:${parts[1]}:${parts[2]}"
            4 -> timeStr
            else -> "0:0:0:0"
        }
        return formattedTimeStr
    }

    inner class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var titleLabel: TextView
        val timeLabel: TextView
        var erMenu: TextView
        val playButton: Button = itemView.findViewById(R.id.play_button)


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
                            var newTitle = titleEditText.text.toString()
                            var newTime = timeEditText.text.toString()

                            // Use defaults if values are removed through edit
                            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(c)
                            if (newTitle == "") {
                                newTitle = sharedPreferences.getString("default_title_key", "").toString()
                            }
                            if (newTime == "") {
                                newTime = sharedPreferences.getString("default_time_key", "").toString()
                            }

                            position.studyTimeTitle = newTitle
                            position.studyTimerTime = newTime
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
