package cp3406.a1.studytracker.adapter

import android.content.Context
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import cp3406.a1.studytracker.R
import cp3406.a1.studytracker.displayWarningAlertDialog
import cp3406.a1.studytracker.model.TimerItem
import java.util.concurrent.TimeUnit

private const val finishedProgressNumber = 0

class TimerAdapter(
    private val timerItems: ArrayList<TimerItem>
) : RecyclerView.Adapter<TimerAdapter.TimerViewHolder>() {

    // Count down timer
    private var countDownTimer: CountDownTimer? = null
    private var countDownTime: Long = 0
    private var countDownTimeLeft: Long = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimerViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)

        return TimerViewHolder(itemView, parent.context)
    }

    override fun onBindViewHolder(holder: TimerViewHolder, position: Int) {
        val timerItem = timerItems[position]
        holder.bind(timerItem)
        Log.i("TimerAdapter", "onBindViewHolder")

        holder.toggleTimerButton.setOnClickListener {
            toggleCountDownPlay(timerItem)
        }
    }

    override fun getItemCount(): Int {
        Log.i("TimerAdapter", "getItemCount: ${timerItems.size} timers: $timerItems")
        return timerItems.size
    }

    /** Handle the start and finish of the count down timer */
    private fun toggleCountDownPlay(
        timerItem: TimerItem
    ) {
        // Get string from text view, calculate milliseconds and start the timer
        if (!timerItem.isRunning) {
            val timeStr = timerItem.timerTime
            val timeMillis: Long = convertMillisecondsToTimeString(timeStr)

//             TODO: Remove test
            Log.i("ItemAdapter", "timeMillis: $timeMillis")

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
                        (((millisUntilFinished.toFloat() / countDownTime) * 100)).toInt()
                    timerItem.timeProgress = updatedProgress
                    timerItem.timerTime =
                        String.format("%02d:%02d:%02d:%02d", days, hours, minutes, seconds)

                    Log.i(
                        "ItemAdapter",
                        "$timerItem.timeProgress $timerItem.timerTime $timerItem.isRunning"
                    )
//                    notifyDataSetChanged()
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

    class TimerViewHolder(
        itemView: View,
        private val context: Context
    ) :
        RecyclerView.ViewHolder(itemView) {

        // Hold the timer item associated with this view holder
        private lateinit var timerItem: TimerItem

        val timerLabel: TextView = itemView.findViewById(R.id.time_count)
        val progressBar: ProgressBar = itemView.findViewById(R.id.progress_bar)
        val toggleTimerButton: Button = itemView.findViewById(R.id.play_button)

        init {
            Log.i("TimerViewHolder", "TimeViewHolder: Initialized")
        }

        fun bind(timerItem: TimerItem) {

            // Bind the timer item to the view holder
            this.timerItem = timerItem

            // Update the view holder UI to match the timer item state
            validateAndFormatTime()
            // TODO: If the time shows milliseconds after use look at this line
            timerLabel.text = timerItem.timerTime
            progressBar.max = 100
            progressBar.progress = timerItem.timeProgress
            toggleTimerButton.setBackgroundResource(
                (if (timerItem.isRunning) R.drawable.stop_icon else R.drawable.play_icon)
            )
            Log.i("TimerViewHolder", "bind called for timerItem: $timerItem")
        }

        private fun validateAndFormatTime() {
            //Retrieve, validate text is in time format and valid format for use
            var timeStr = timerLabel.text.toString().trim()
            if (!isValidTime(timeStr)) {
                Log.d("ItemAdapter", "Invalid time format: $timeStr")
                displayWarningAlertDialog(
                    context,
                    R.string.invalid_time_entered_title,
                    R.string.invalid_time_entered_message
                )
                timeStr = formatTimeString(timeStr)
                timerLabel.text = timeStr
                return
            }
            timeStr = "00:00:00:01"
            timerLabel.text = timeStr

        }

        /** Check for valid time string format */
        private fun isValidTime(timeStr: String): Boolean {
            return timeStr.matches("[\\d:]+".toRegex())
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
    }
}
