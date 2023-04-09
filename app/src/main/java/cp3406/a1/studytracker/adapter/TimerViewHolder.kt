package cp3406.a1.studytracker.adapter

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import cp3406.a1.studytracker.R
import cp3406.a1.studytracker.model.TimerItem
import cp3406.a1.studytracker.displayWarningAlertDialog

class TimerViewHolder(
    itemView: View,
    private val context: Context,
    private val onTogglePlay: (TimerItem, TimerViewHolder) -> Unit
) :
    RecyclerView.ViewHolder(itemView) {

    // Hold the timer item associated with this view holder
    private var timerItem: TimerItem? = null

    private val timerLabel: TextView = itemView.findViewById(R.id.time_count)
    private val progressBar: ProgressBar = itemView.findViewById(R.id.progress_bar)
    private val togglePlayButton: Button = itemView.findViewById(R.id.play_button)

    // Define a variable to hold the click listener for the toggle play button
//    private var onTogglePlayClickListener: ((TimerItem, TimerViewHolder) -> Unit)? = null

    init {
        togglePlayButton.setOnClickListener {
            Log.i("TimerViewHolder", "TimeViewHolder: Clicked")
            timerItem?.let { item ->
                val isRunning = !item.isRunning
                onTogglePlay.invoke(item, this)
                togglePlayButton.setBackgroundResource(if (isRunning) R.drawable.stop_icon else R.drawable.play_icon)
            } ?: Log.e("TimerViewHolder", "TimerItemIsNull")
        }
    }

    fun bind(timerItem: TimerItem) {
        // Bind the timer item to the view holder and set the click listener for the toggle play button
        this.timerItem = timerItem

        // Update the view holder UI to match the timer item state
        validateAndFormatTime()
        timerLabel.text = timerItem.timerTime
        progressBar.max = 100
        progressBar.progress = timerItem.timeProgress
        togglePlayButton.setBackgroundResource(
            (if (timerItem.isRunning) R.drawable.stop_icon else R.drawable.play_icon)
        )
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
