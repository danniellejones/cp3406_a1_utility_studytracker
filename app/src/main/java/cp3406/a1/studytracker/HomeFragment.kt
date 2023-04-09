/**
 * Home fragment for holding recycler view.
 */

package cp3406.a1.studytracker

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import cp3406.a1.studytracker.adapter.ItemAdapter
import cp3406.a1.studytracker.adapter.TimerAdapter
import cp3406.a1.studytracker.model.StudyTimer
import cp3406.a1.studytracker.model.TimerItem

//private const val finishedProgressNumber = 0

private const val startProgressNumber = 100

/** Home Fragment for the main interaction screen. */
class HomeFragment : Fragment(), SharedPreferences.OnSharedPreferenceChangeListener,
    ItemAdapter.OnItemActionListener {

    // Display and Views
    private lateinit var itemAdapter: ItemAdapter
    private lateinit var timerAdapter: TimerAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var floatingAddButton: FloatingActionButton

    // Data and Settings
    private lateinit var sharedPreferences: SharedPreferences
    private var studyTimeList: ArrayList<StudyTimer> = ArrayList()
    private var itemTimers: ArrayList<TimerItem> = ArrayList()

//    // Count down timer
//    private var countDownTimer: CountDownTimer? = null
//    private var countDownTime: Long = 0
//    private var countDownTimeLeft: Long = 0

    /** Inflates the fragment_home layout, adds list_item layout to view and sets default from settings */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        Log.i("HomeFragment", "OnCreateView called")
        setHasOptionsMenu(true)

        // Inflate the layout containing the recycler view and set up shared preferences
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
        val rootView = inflater.inflate(R.layout.fragment_home, container, false)
        // Populate list with data from last session
        loadList()
        // Create a timer for each study timer item
        for (studyTimer in studyTimeList) {
            val timerItem = TimerItem(studyTimer.studyTimerTime, false, startProgressNumber)
            itemTimers.add(timerItem)
        }
        // Initialize Adapter for Study Timer
        itemAdapter = ItemAdapter(requireContext(), studyTimeList, itemTimers)
        itemAdapter.setOnItemActionListener(this)
        // Initialize Adapter for Timer
        timerAdapter = TimerAdapter(itemTimers)
        Log.d("HomeFragment", "timerAdapter: ${timerAdapter.itemCount}")
        // Set up recycler view
        recyclerView = rootView.findViewById(R.id.tracker_recycler_view) as RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        // Set Recycler view Adapter
        recyclerView.adapter = itemAdapter
        floatingAddButton =
            rootView.findViewById(R.id.floating_add_button)
        floatingAddButton.setOnClickListener { addNewTimer() }


        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d("HomeFragment", "OnViewCreated: $studyTimeList")
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate((R.menu.settings_menu), menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return NavigationUI.onNavDestinationSelected(
            item,
            requireView().findNavController()
        ) || super.onOptionsItemSelected(item)
    }

    override fun onStart() {
        super.onStart()
        Log.i("HomeFragment", "onStart called")
    }

    private fun addNewTimer() {
        Log.i("HomeFragment", "addNewTimer: $studyTimeList")
        val inflaterForAddItem = LayoutInflater.from(requireContext())
        val viewForAddItem = inflaterForAddItem.inflate(R.layout.add_item, null)

        val newStudyTimerTitle = viewForAddItem.findViewById<EditText>(R.id.new_title)
        val newStudyTimerTime = viewForAddItem.findViewById<EditText>(R.id.new_time)
        val addDialog = AlertDialog.Builder(requireContext())
        addDialog.setView(viewForAddItem)
        addDialog.setPositiveButton(R.string.save) { dialog, _ ->
            var title = newStudyTimerTitle.text.toString()
            var timer = newStudyTimerTime.text.toString()

            // Use defaults if values are not entered
            if (title == "") {
                title = sharedPreferences.getString("default_title_key", "").toString()
            }
            if (timer == "") {
                timer = sharedPreferences.getString("default_time_key", "").toString()
            }

            studyTimeList.add(StudyTimer(title, timer))
            recyclerView.layoutManager?.scrollToPosition(studyTimeList.size - 1)
            itemAdapter.notifyItemInserted(studyTimeList.size - 1)

            // TODO: Repeated code: required to force update of recycler view
            //  - tried passing in as itemAdapter as parameter from onViewCreated
            //  - tried using recyclerView.adapter
            itemAdapter = ItemAdapter(requireContext(), studyTimeList, itemTimers)
            recyclerView.adapter = itemAdapter

            Toast.makeText(requireContext(), "Adding", Toast.LENGTH_LONG).show()
            dialog.dismiss()
        }
        addDialog.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }
        addDialog.create()
        addDialog.show()
    }

    /** Save data to json file from data used in recycler view. */
    private fun saveList() {
        val editor = sharedPreferences.edit()
        val gson = Gson()
        val json = gson.toJson(studyTimeList)
        editor.putString("studyTimeList", json)
        editor.apply()
        Log.d("HomeFragment", "dataSaved: $studyTimeList")
    }

    /** Load data from json file to populate recycler view. */
    private fun loadList() {
        val gson = Gson()
        val json = sharedPreferences.getString("studyTimeList", null)
        val type = object : TypeToken<ArrayList<StudyTimer>>() {}.type
        studyTimeList = gson.fromJson(json, type) ?: ArrayList()
        Log.d("HomeFragment", "dataLoaded: $studyTimeList")
    }

    override fun onResume() {
        super.onResume()
        sharedPreferences.registerOnSharedPreferenceChangeListener(this)
        updateRecyclerView()
        Log.i("HomeFragment", "onResume called: $studyTimeList")
        Log.d("HomeFragment", "timerAdapter in resume: ${timerAdapter.itemCount}")
    }

    override fun onPause() {
        super.onPause()
        Log.i("HomeFragment", "onPause called: $studyTimeList")
        saveList()
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onStop() {
        super.onStop()
        Log.i("HomeFragment", "onStop called")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.i("HomeFragment", "onDestroyView called")
    }

    override fun onDetach() {
        super.onDetach()
        Log.i("HomeFragment", "onDetach called")
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        if (key == "default_time_key") {
            updateDefaultTime()
        }
    }

    private fun updateDefaultTime() {
        val defaultTime = sharedPreferences.getString("default_time_key", "")
        Log.d("HomeFragment", "Default time: $defaultTime")
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun updateRecyclerView() {
        loadList()
        itemAdapter.notifyDataSetChanged()
        Log.d("HomeFragment", "update RecyclerView: $studyTimeList")
    }

    override fun onItemUpdated(item: StudyTimer, position: Int) {
        studyTimeList[position] = item
        Log.d("HomeFragment", "Item Updated: $studyTimeList")
    }

    override fun onItemRemoved(position: Int) {
        studyTimeList.removeAt(position)
        Log.d("HomeFragment", "Item Removed: $studyTimeList")
    }

    /** Handle the start and finish of the count down timer */
//    fun toggleCountDownPlay(
//        timerItem: TimerItem,
//        timerTextView: TextView
//    ) {
//        // Get string from text view, calculate milliseconds and start the timer
//        if (!timerItem.isRunning) {
//            val timeStr = timerItem.timerTime
//            val timeMillis: Long = convertMillisecondsToTimeString(timeStr)
//
////             TODO: Remove test
//            Log.i("ItemAdapter", "timeMillis: $timeMillis")
//
//            countDownTime = timeMillis
//            countDownTimeLeft = countDownTime
//
//            // Use count down timer and display on text view
//            countDownTimer = object : CountDownTimer(countDownTimeLeft, 1000) {
//
//                override fun onTick(millisUntilFinished: Long) {
//                    countDownTimeLeft = millisUntilFinished
//                    val days = TimeUnit.MILLISECONDS.toDays(millisUntilFinished)
//                    val hours =
//                        TimeUnit.MILLISECONDS.toHours(millisUntilFinished) - TimeUnit.DAYS.toHours(
//                            days
//                        )
//                    val minutes =
//                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(
//                            hours
//                        ) - TimeUnit.DAYS.toMinutes(days)
//                    val seconds =
//                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(
//                            minutes
//                        ) - TimeUnit.HOURS.toSeconds(hours) - TimeUnit.DAYS.toSeconds(days)
//
//                    // Update progress bar and time
//                    val updatedProgress =
//                        (((millisUntilFinished.toFloat() / countDownTime) * 100)).toInt()
//                    timerItem.timeProgress = updatedProgress
//                    timerItem.timerTime =
//                        String.format("%02d:%02d:%02d:%02d", days, hours, minutes, seconds)
//
//                    Log.i("ItemAdapter", "$timerItem.timeProgress $timerItem.timerTime $timerItem.isRunning")
////                    notifyDataSetChanged()
//                }
//
//                override fun onFinish() {
//                    // Set progress to zero and allow chance to edit, if not item is auto-removed
//                    timerItem.timeProgress = finishedProgressNumber
////                    itemActionListener?.onItemRemoved(holder.adapterPosition)
////                    notifyDataSetChanged()
//
//                    // TODO : Remove test
//                    Log.i("ItemAdapter", "OnFinished Ended")
//                }
//            }.start()
//        } else {
//            // Update textView with new time
//            countDownTimer?.cancel()
//            Log.d("ItemAdapter", "Time on cancel: count=$timerItem.isRunning - ${timerItem.timerTime}")
////            itemActionListener?.onItemUpdated(item, holder.adapterPosition)
////            notifyDataSetChanged()
//        }
//    }

//    private fun convertMillisecondsToTimeString(timeStr: String): Long {
//        // Split string from text view and assign to days, hours, minutes and seconds
//        val timeUnits =
//            mutableListOf("stringDays", "stringHours", "stringMinutes", "stringSeconds")
//        val timeValues = timeStr.split(":").toTypedArray()
//
//        for (i in timeUnits.indices) {
//            val unit = timeUnits[i]
//            val value = if (i < timeValues.size) timeValues[i] else "00"
//            println("$unit: $value")
//        }
//
//        val stringDays: String = timeValues[0]
//        val stringHours: String = timeValues[1]
//        val stringMinutes: String = timeValues[2]
//        val stringSeconds: String = timeValues[3]
//
//        // Convert and calculate milliseconds for count down timer
//        val daysInMs: Long = TimeUnit.DAYS.toMillis(stringDays.toLong())
//        val hoursInMs: Long = TimeUnit.HOURS.toMillis(stringHours.toLong())
//        val minutesInMs: Long = TimeUnit.MINUTES.toMillis(stringMinutes.toLong())
//        val secondsInMs: Long = TimeUnit.SECONDS.toMillis(stringSeconds.toLong())
//
//        return daysInMs + hoursInMs + minutesInMs + secondsInMs
//    }

}