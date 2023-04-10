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
import cp3406.a1.studytracker.model.StudyTimer
import cp3406.a1.studytracker.model.TimerItem
import java.util.concurrent.TimeUnit

private const val maxProgress = 100

class TimerAdapter(
    private val timerItems: ArrayList<TimerItem>,
    private val itemAdapter: ItemAdapter,
    private val studyTimerItems: MutableList<StudyTimer>
) : RecyclerView.Adapter<TimerAdapter.TimerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimerViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        Log.i("TimerAdapter", "onCreateViewHolder")
        return TimerViewHolder(itemView, parent.context)
    }

    override fun onBindViewHolder(holder: TimerViewHolder, position: Int) {
        val timerItem = timerItems[position]
        val studyTimer = studyTimerItems[position]
        holder.bind(timerItem)
        Log.i("TimerAdapter", "onBindViewHolder")
    }

    override fun getItemCount(): Int {
        Log.i("TimerAdapter", "count=${timerItems.size} timerItems=$timerItems")
        return timerItems.size
    }

    class TimerViewHolder(
        itemView: View,
        private val context: Context
    ) :
        RecyclerView.ViewHolder(itemView) {

        // Hold the timer item associated with this view holder
        private lateinit var timerItem: TimerItem

        private val timerLabel: TextView = itemView.findViewById(R.id.time_count)
//        private val progressBar: ProgressBar = itemView.findViewById(R.id.progress_bar)
//        private val toggleTimerButton: Button = itemView.findViewById(R.id.play_button)

        init {
            Log.i("TimerViewHolder", "TimeViewHolder: Initialized")
        }

        fun bind(timerItem: TimerItem) {

            // Bind the timer item to the view holder
            this.timerItem = timerItem

            // Update the view holder UI to match the timer item state
//            validateAndFormatTime()
            // TODO: If the time shows milliseconds after use look at this line
            timerLabel.text = timerItem.timerTime
//            progressBar.max = maxProgress
//            progressBar.progress = timerItem.timeProgress
//            toggleTimerButton.setBackgroundResource(
//                (if (timerItem.isRunning) R.drawable.stop_icon else R.drawable.play_icon)
//            )

            Log.i("TimerViewHolder", "Bind called for timerItem: $timerItem")
        }

//        private fun validateAndFormatTime() {
//            //Retrieve, validate text is in time format and valid format for use
//            var timeStr = timerLabel.text.toString().trim()
//            if (!isValidTime(timeStr)) {
//                Log.d("ItemAdapter", "Invalid time format: $timeStr")
//                displayWarningAlertDialog(
//                    context,
//                    R.string.invalid_time_entered_title,
//                    R.string.invalid_time_entered_message
//                )
//                timeStr = formatTimeString(timeStr)
//                timerLabel.text = timeStr
//                return
//            }
//            timeStr = "00:00:00:01"
//            timerLabel.text = timeStr
//
//        }

//        /** Check for valid time string format */
//        private fun isValidTime(timeStr: String): Boolean {
//            return timeStr.matches("[\\d:]+".toRegex())
//        }
//
//        /** Format time string */
//        private fun formatTimeString(timeStr: String): String {
//            val parts = timeStr.split(":")
//            val formattedTimeStr: String = when (parts.size) {
//                1 -> "0:0:0:$timeStr"
//                2 -> "0:0:${parts[0]}:${parts[1]}"
//                3 -> "0:${parts[0]}:${parts[1]}:${parts[2]}"
//                4 -> timeStr
//                else -> "0:0:0:0"
//            }
//            return formattedTimeStr
//        }
    }
}
