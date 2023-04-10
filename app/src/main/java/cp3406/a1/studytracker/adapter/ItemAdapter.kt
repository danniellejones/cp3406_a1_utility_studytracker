/**
 * Item Adapter for recycler view.
 * Known warnings: Reflective access to mPopup; Use of notifyDataSetChanged
 */

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
import cp3406.a1.studytracker.model.TimerItem
import java.util.concurrent.TimeUnit

private const val finishedProgressNumber = 0
private const val startProgressNumber = 100

/** Set up item adapter for recycle view */
class ItemAdapter(
    val itemAdapterContext: Context,
    private val studyTimerItems: MutableList<StudyTimer>,
    private val timerItems: MutableList<TimerItem>

) :
    RecyclerView.Adapter<ItemAdapter.ItemViewHolder>() {

    private var itemClickListener: OnItemClickListener? = null
    private var itemActionListener: OnItemActionListener? = null
    private lateinit var sharedPreferences: SharedPreferences

    // Count down timer
    private var countDownTimer: CountDownTimer? = null
    private var countDownTime: Long = 0
    private var countDownTimeLeft: Long = 0

    /** Allow edit and remove of items from the recyclerview */
    interface OnItemActionListener {
        fun onItemUpdated(item: StudyTimer, position: Int)
        fun onItemRemoved(position: Int)
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    fun updateTimerItemsToMatchStudyTimers() {
        timerItems.clear()
        for (studyTimerItem in studyTimerItems) {
            val timerItem = TimerItem(studyTimerItem.studyTimerTime, false, startProgressNumber)
            timerItems.add(timerItem)
        }
        notifyDataSetChanged()
    }

    fun updateStudyTimerTimeToTimerTime() {
        for (studyTimerItem in studyTimerItems) {
            for (timerItem in timerItems) {
                val position = studyTimerItems.indexOf(studyTimerItem)
                val selectedTimerItem = timerItems[position]
                studyTimerItem.studyTimerTime = selectedTimerItem.timerTime
            }
            notifyDataSetChanged()
        }
    }


    fun setOnItemClickListener(listener: OnItemClickListener) {
        itemClickListener = listener
    }

//    fun updateTimerItems() {
//        timerItems.clear()
//        for (studyTimer in timerItems) {
//            val timerItem = TimerItem(studyTimer.timerTime, false, startProgressNumber)
//            timerItems.add(timerItem)
//        }
//        notifyDataSetChanged()
//    }


    /** Listen for updates to the itemAdapter */
    fun setOnItemActionListener(listener: ItemAdapter.OnItemActionListener) {
        itemActionListener = listener
    }

    /** Inflate and create new instance of ItemViewHolder class for each item in RecyclerView*/
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val adapterView =
            LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)

        return ItemViewHolder(adapterView)
    }

    /** Get number of items in recycler view */
    override fun getItemCount(): Int {
        return studyTimerItems.size
    }

    /** Hold data to views, add time using quick add and toggle play timer on/off */
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {

        val item = studyTimerItems[position]
        holder.titleLabel.text = item.studyTimeTitle
        holder.timeLabel.text = item.studyTimerTime

        // Quick add hours and minutes from edit text views by pressing button
        holder.quickAddButton.setOnClickListener {
            // TODO: Add error checking for if count down is running - warning dialog?
            addTimeFromQuickAdd(holder, item)
        }
    }

    private fun addTimeFromQuickAdd(
        holder: ItemViewHolder,
        item: StudyTimer
    ) {
        // Get input from edit text views
        val hoursStrFromQuickAdd =
            holder.inputHours?.text?.toString()?.takeIf(String::isNotBlank) ?: "0"
        val minutesStrFromQuickAdd =
            holder.inputMinutes?.text?.toString()?.takeIf(String::isNotBlank) ?: "0"
        val hoursFromQuickAdd = hoursStrFromQuickAdd.toInt()
        val minutesFromQuickAdd = minutesStrFromQuickAdd.toInt()
        var timeStr = holder.timeLabel.text?.toString()

        // Format and calculate the time difference
        timeStr = formatTimeString(timeStr as String)
        val timeStrToAdd =
            String.format("00:%02d:%02d:00", hoursFromQuickAdd, minutesFromQuickAdd)
        Log.d("ItemAdapter", "Str Quick Add: $timeStrToAdd")
        val totalSeconds = calculateTimeDifferenceInSeconds(timeStr, timeStrToAdd)
        val newTimeString = convertTimeInSecondsToString(totalSeconds)

        // Update item with new time, clear input fields and update recycler view
        item.studyTimerTime = newTimeString
        holder.inputHours?.text?.clear()
        holder.inputMinutes?.text?.clear()
        itemActionListener?.onItemUpdated(item, holder.adapterPosition)
        notifyDataSetChanged()
    }

    /** Format time string */
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

    /** Calculate difference in time between time strings */
    private fun calculateTimeDifferenceInSeconds(timeStr: String, timeStrToAdd: String): Int {
        val oldTimeInSeconds: Int = calculateSeconds(timeStr)
        val timeToAddInSeconds: Int = calculateSeconds(timeStrToAdd)
        val newTimeInSeconds = if (timeToAddInSeconds > oldTimeInSeconds) {
            0
        } else {
            oldTimeInSeconds - timeToAddInSeconds
        }
        return newTimeInSeconds
    }

    /** Convert time in seconds to formatted time string */
    private fun convertTimeInSecondsToString(totalSeconds: Int): String {
        val days = totalSeconds / (24 * 3600)
        val hours = (totalSeconds % (24 * 3600)) / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60

        return String.format("%02d:%02d:%02d:%02d", days, hours, minutes, seconds)
    }

    /** Calculate seconds from dd:hh:mm:ss time string */
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

    inner class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var titleLabel: TextView = itemView.findViewById(R.id.item_title)
        val timeLabel: TextView = itemView.findViewById(R.id.time_count)
        val inputHours: EditText? = itemView.findViewById(R.id.input_hour)
        val inputMinutes: EditText? = itemView.findViewById(R.id.input_minute)
        val quickAddButton: Button = itemView.findViewById(R.id.quick_add_button)

        init {
            val editRecyclerItemMenu: TextView = itemView.findViewById(R.id.edit_or_remove_menu)
            editRecyclerItemMenu.setOnClickListener { popupMenus(itemView) }
            val togglePlayButton: Button = itemView.findViewById(R.id.play_button)
//            togglePlayButton.setOnClickListener { toggleCountDownPlay(timerItem) }

            togglePlayButton.setOnClickListener {
                val selectedStudyTimer = studyTimerItems[adapterPosition]
                val position = studyTimerItems.indexOf(selectedStudyTimer)
                toggleCountDown(timerItems[position])
            }
        }

        private fun testFunction(any: Any) {
            val position = studyTimerItems[adapterPosition]
            itemActionListener?.onItemUpdated(position, adapterPosition)
            Log.i("ItemAdapter", "Test Function $any")
        }


        /** Create popup menu for edit and remove interactions */
        private fun popupMenus(viewForPopup: View) {

            // Get menu, text and views to be able to remove/edit recycler item
            val position = studyTimerItems[adapterPosition]
            val popupMenus = PopupMenu(itemAdapterContext, viewForPopup)
            popupMenus.inflate(R.menu.edit_menu)
            popupMenus.setOnMenuItemClickListener {
                when (it.itemId) {
                    // On edit selection
                    R.id.edit_text -> {
                        val layoutInflaterView =
                            LayoutInflater.from(itemAdapterContext)
                                .inflate(R.layout.add_item, null)
                        val titleEditText =
                            layoutInflaterView.findViewById<EditText>(R.id.new_title)
                        val timeEditText =
                            layoutInflaterView.findViewById<EditText>(R.id.new_time)
                        titleEditText.setText(position.studyTimeTitle)
                        timeEditText.setText(position.studyTimerTime)

                        // Create alert dialog box for editing
                        AlertDialog.Builder(itemAdapterContext).setView(layoutInflaterView)
                            .setPositiveButton(R.string.save) { dialog, _ ->
                                var newTitle = titleEditText.text.toString()
                                var newTime = timeEditText.text.toString()

                                // Use defaults if values are removed through edit
                                sharedPreferences =
                                    PreferenceManager.getDefaultSharedPreferences(
                                        itemAdapterContext
                                    )
                                if (newTitle == "") {
                                    newTitle =
                                        sharedPreferences.getString("default_title_key", "")
                                            .toString()
                                }
                                if (newTime == "") {
                                    newTime =
                                        sharedPreferences.getString("default_time_key", "")
                                            .toString()
                                }

                                // Update text and recycler view, then close dialog
                                position.studyTimeTitle = newTitle
                                position.studyTimerTime = newTime
                                titleLabel.text = newTitle
                                timeLabel.text = newTime
                                itemActionListener?.onItemUpdated(position, adapterPosition)
                                notifyDataSetChanged()
                                dialog.dismiss()
                            }
                            .setNegativeButton(R.string.cancel) { dialog, _ ->
                                dialog.dismiss()
                            }
                            .create()
                            .show()
                        true
                    }
                    // On delete selection
                    R.id.delete -> {
                        AlertDialog.Builder(itemAdapterContext)
                            .setTitle(R.string.delete)
                            .setIcon(R.drawable.warning_icon)
                            .setMessage(R.string.confirm_delete_message)
                            .setPositiveButton(R.string.confirm) { dialog, _ ->
                                studyTimerItems.removeAt(adapterPosition)  // Remove item from data
                                itemActionListener?.onItemRemoved(adapterPosition)  // Remove item from recycler view
                                notifyDataSetChanged()
                                Toast.makeText(
                                    itemAdapterContext,
                                    R.string.success_delete_message,
                                    Toast.LENGTH_SHORT
                                ).show()
                                dialog.dismiss()
                            }
                            .setNegativeButton(R.string.cancel) { dialog, _ ->
                                dialog.dismiss()
                            }
                            .create()
                            .show()
                        true
                    }
                    else -> true
                }
            }
            popupMenus.show()
            // Known warning: Reflective access to mPopup, which is not part of public SDK
            val popup = PopupMenu::class.java.getDeclaredField("mPopup")
            popup.isAccessible = true
            val menu = popup.get(popupMenus)
            menu.javaClass.getDeclaredMethod("setForceShowIcon", Boolean::class.java)
                .invoke(menu, true)
        }

        /** Handle the start and finish of the count down timer */
        private fun toggleCountDown(
            timerItem: TimerItem
        ) {
            // Get string from text view, calculate milliseconds and start the timer
            if (!timerItem.isRunning) {
                val timeStr = timerItem.timerTime
                val timeMillis: Long = convertMillisecondsToTimeString(timeStr)

//             TODO: Remove test
                Log.i("ItemAdapter", "Start timeMillis: $timeMillis")

                countDownTime = timeMillis
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

                        // Update progress bar and time
                        val updatedProgress =
                            (((countDownTimeLeft.toFloat() / countDownTime) * 100)).toInt()
                        timerItem.timeProgress = updatedProgress
                        timerItem.timerTime =
                            String.format("%02d:%02d:%02d:%02d", days, hours, minutes, seconds)
                        val position = timerItems.indexOf(timerItem)
                        timeLabel.text = timerItem.timerTime



//                        notifyDataSetChanged()
                        Log.i(
                            "ItemAdapter",
                            "CountDown: $timerItem"
                        )

                    }

                    override fun onFinish() {
                        // Set progress to zero and allow chance to edit, if not item is auto-removed
                        timerItem.timeProgress = finishedProgressNumber
//                    itemActionListener?.onItemRemoved(holder.adapterPosition)
//                    notifyDataSetChanged()

                        // TODO : Remove test
                        Log.i("ItemAdapter", "OnFinished Ended")
                    }
                }.start()
            } else {
                // Update textView with new time
                countDownTimer?.cancel()
                Log.d(
                    "ItemAdapter",
                    "Time on cancel: count=$timerItem.isRunning - ${timerItem.timerTime}"
                )
//            itemActionListener?.onItemUpdated(item, holder.adapterPosition)
//            notifyDataSetChanged()
            }
        }

        private fun convertMillisecondsToTimeString(timeStr: String): Long {
            // Split string from text view and assign to days, hours, minutes and seconds
            val timeUnits =
                mutableListOf("stringDays", "stringHours", "stringMinutes", "stringSeconds")
            val timeValues = timeStr.split(":").toTypedArray()

            for (i in timeUnits.indices) {
                val unit = timeUnits[i]
                val value = if (i < timeValues.size) timeValues[i] else "00"
                println("$unit: $value")
            }

            val stringDays: String = timeValues[0]
            val stringHours: String = timeValues[1]
            val stringMinutes: String = timeValues[2]
            val stringSeconds: String = timeValues[3]

            // Convert and calculate milliseconds for count down timer
            val daysInMs: Long = TimeUnit.DAYS.toMillis(stringDays.toLong())
            val hoursInMs: Long = TimeUnit.HOURS.toMillis(stringHours.toLong())
            val minutesInMs: Long = TimeUnit.MINUTES.toMillis(stringMinutes.toLong())
            val secondsInMs: Long = TimeUnit.SECONDS.toMillis(stringSeconds.toLong())

            return daysInMs + hoursInMs + minutesInMs + secondsInMs
        }

//        override fun onClick(view: View?) {
//            val position = adapterPosition
//            // Check if the position is still relevant: has not been deleted
//            if (position != RecyclerView.NO_POSITION) {
//                listener?.onItemClick(position)
//            }
//        }
    }

}
