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
        val progressBar = holder.itemView.findViewById<ProgressBar>(R.id.progress_bar)
        holder.quickAddButton.setOnClickListener {
            val hoursStrFromQuickAdd = holder.inputHours?.text?.toString()?.takeIf(String::isNotBlank) ?: "0"
            val minutesStrFromQuickAdd = holder.inputMinutes?.text?.toString()?.takeIf(String::isNotBlank) ?: "0"
            val hoursFromQuickAdd = hoursStrFromQuickAdd.toInt()
            val minutesFromQuickAdd = minutesStrFromQuickAdd.toInt()
            var timeStr = holder.timeLabel.text?.toString()
            timeStr = formatTimeString(timeStr as String)
            Log.d("ItemAdapter", "Str Quick Orig: $timeStr")

            val timeStrToAdd =
                String.format("00:%02d:%02d:00", hoursFromQuickAdd, minutesFromQuickAdd)
            Log.d("ItemAdapter", "Str Quick Add: $timeStrToAdd")
            val totalSeconds = calculateNewTime(timeStr, timeStrToAdd)
            val newTimeString = convertTimeInSecondsToString(totalSeconds)
            item.studyTimerTime = newTimeString
            holder.inputHours?.text?.clear()
            holder.inputMinutes?.text?.clear()
            itemActionListener?.onItemUpdated(item, holder.adapterPosition)
            notifyDataSetChanged()
        }
        holder.playButton.setOnClickListener {
            Log.i("ItemAdapter", "Initialised isCountingDown = $isCountingDown")
            toggleCountDownPlay(holder.playButton, holder.timeLabel, item, holder, progressBar)
            onCountDownStateChangedListener?.onCountDownStateChanged(isCountingDown)
        }

    }


    private fun calculateNewTime(timeStr: String, timeStrToAdd: String): Int {
        val oldTimeInSeconds: Int = calculateSeconds(timeStr)
        val timeToAddInSeconds: Int = calculateSeconds(timeStrToAdd)
        val newTimeInSeconds = if (timeToAddInSeconds > oldTimeInSeconds) { 0
        } else {
            oldTimeInSeconds - timeToAddInSeconds
        }

        return newTimeInSeconds
    }

    private fun convertTimeInSecondsToString(totalSeconds: Int): String {
        val days = totalSeconds / (24 * 3600)
        val hours = (totalSeconds % (24 * 3600)) / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60

        return String.format("%02d:%02d:%02d:%02d", days, hours, minutes, seconds)
    }

    private fun calculateSeconds(timeString: String): Int {
        var totalSeconds = 0
        val timeParts = timeString.split(":")
        val multipliers = listOf(86400, 3600, 60, 1)
        for ((index, part) in timeParts.withIndex()) {
            val value = part.toInt()
            if (value != 0) {
                val seconds = value * multipliers[index]
                totalSeconds += seconds
            }
        }
        return totalSeconds
    }

//    private fun calculateSeconds(timeString: String): Int {
//        var daysInSeconds = 0
//        var hoursInSeconds = 0
//        var minutesInSeconds = 0
//        var seconds = 0
//        val timeParts = timeString.split(":")
//        if (timeParts[0].toInt() != 0) {
//            daysInSeconds = timeParts[0].toInt() * 86400
//        }
//        else if (timeParts[1].toInt() != 0) {
//            hoursInSeconds = timeParts[1].toInt() * 3600
//        }
//        else if (timeParts[2].toInt() != 0) {
//            minutesInSeconds = timeParts[2].toInt() * 60
//        }
//        else {
//            seconds = timeParts[3].toInt()
//        }
//        return daysInSeconds + hoursInSeconds + minutesInSeconds + seconds
//    }


    private fun toggleCountDownPlay(
        countDownButton: Button,
        timeLabel: TextView,
        item: StudyTimer,
        holder: ItemViewHolder,
        progressBar: ProgressBar
    ) {
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

                    // Update progress bar
                    val updatedProgress =
                        (((millisUntilFinished.toFloat() / countDownTime) * 100)).toInt()
                    progressBar.progress = updatedProgress
                    Log.i("ItemAdapter", "Progress: $updatedProgress")
//                    holder.progressBar.setProgress((int) (millisUntilFinished / 1000));
//                    val progress = (millisUntilFinished / 1000).toInt()
//                    updateProgressBar(holder.adapterPosition, progress)

                    timeLabel.text =
                        String.format("%02d:%02d:%02d:%02d", days, hours, minutes, seconds)
                }

                override fun onFinish() {
                    println("Finished")
                    countDownButton.setBackgroundResource(R.drawable.play_icon)
                    progressBar.progress = 0  // Completed progress bar value

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
        val inputHours: EditText? = itemView.findViewById(R.id.input_hour)
        val inputMinutes: EditText? = itemView.findViewById(R.id.input_minute)
        val quickAddButton: Button = itemView.findViewById(R.id.quick_add_button)


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
                                newTitle =
                                    sharedPreferences.getString("default_title_key", "").toString()
                            }
                            if (newTime == "") {
                                newTime =
                                    sharedPreferences.getString("default_time_key", "").toString()
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
