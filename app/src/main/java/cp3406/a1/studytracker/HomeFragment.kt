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

private const val startProgressNumber = 100

/** Home Fragment for the main interaction screen. */
class HomeFragment : Fragment(), SharedPreferences.OnSharedPreferenceChangeListener,
    ItemAdapter.OnItemActionListener, ItemAdapter.OnItemClickListener {

    // Display and Views
    private lateinit var itemAdapter: ItemAdapter
    private lateinit var timerAdapter: TimerAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var floatingAddButton: FloatingActionButton

    // Data and Settings
    private lateinit var sharedPreferences: SharedPreferences
    private var studyTimerItems: ArrayList<StudyTimer> = ArrayList()
    private var timerItems: ArrayList<TimerItem> = ArrayList()

    /** Inflates the fragment_home layout, adds list_item layout to view and sets default from settings */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)

        // Inflate the layout containing the recycler view and set up shared preferences
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
        val rootView = inflater.inflate(R.layout.fragment_home, container, false)

        // Load study time data from file
        loadStudyTimersFromFile()
        // Create a timer for each study timer item
        createTimerInstances()

        // Initialize Adapter for Study Timer
        itemAdapter = ItemAdapter(requireContext(), studyTimerItems, timerItems)
        // Set up action listener for edit/remove clicks and play button clicks
        itemAdapter.setOnItemActionListener(this)
        itemAdapter.setOnItemClickListener(this)
        // Initialize Adapter for Timer
        timerAdapter = TimerAdapter(timerItems, itemAdapter, studyTimerItems)
        // Set up recycler view
        recyclerView = rootView.findViewById(R.id.tracker_recycler_view) as RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        // Set Recycler view Adapter
        recyclerView.adapter = itemAdapter
        floatingAddButton =
            rootView.findViewById(R.id.floating_add_button)
        floatingAddButton.setOnClickListener { addNewStudyTimer() }

        Log.i(
            "HomeFragment",
            "OnCreateView: count=${timerAdapter.itemCount} timerItems=${timerItems}"
        )

        return rootView
    }

    private fun createTimerInstances() {
        if (timerItems.size != studyTimerItems.size) {
            val difference = studyTimerItems.size - timerItems.size
            if (difference > 0) {
                for (i in timerItems.size until studyTimerItems.size) {
                    val studyTimer = studyTimerItems[i]
                    val newTimerItem =
                        TimerItem(studyTimer.studyTimerTime, false, startProgressNumber)
                    timerItems.add(newTimerItem)
                }
            } else {
                timerItems.subList(timerItems.size + difference, timerItems.size).clear()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.i(
            "HomeFragment",
            "OnViewCreated: count=${itemAdapter.itemCount} studyTimers=$studyTimerItems"
        )
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

    private fun addNewStudyTimer() {
        Log.i("HomeFragment", "addNewTimer: $studyTimerItems $timerItems")
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

            studyTimerItems.add(StudyTimer(title, timer))
            recyclerView.layoutManager?.scrollToPosition(studyTimerItems.size - 1)
            itemAdapter.notifyItemInserted(studyTimerItems.size - 1)
//            itemAdapter.updateTimerItemsToMatchStudyTimers()

            // TODO: Repeated code: required to force update of recycler view
            //  - tried passing in as itemAdapter as parameter from onViewCreated
            //  - tried using recyclerView.adapter
            itemAdapter = ItemAdapter(requireContext(), studyTimerItems, timerItems)
            recyclerView.adapter = itemAdapter

            dialog.dismiss()
        }
        addDialog.setNegativeButton(R.string.cancel) { dialog, _ ->
            dialog.dismiss()
        }
        addDialog.create()
        addDialog.show()
    }

    /** Save data to json file from data used in recycler view. */
    private fun saveStudyTimersToFile() {
        val editor = sharedPreferences.edit()
        val gson = Gson()
        val json = gson.toJson(studyTimerItems)
        editor.putString("studyTimeList", json)
        editor.apply()
        Log.i("HomeFragment", "Saved: $studyTimerItems")
    }

    /** Load data from json file to populate recycler view. */
    private fun loadStudyTimersFromFile() {
        val gson = Gson()
        val json = sharedPreferences.getString("studyTimeList", null)
        val type = object : TypeToken<ArrayList<StudyTimer>>() {}.type
        studyTimerItems = gson.fromJson(json, type) ?: ArrayList()
        Log.i("HomeFragment", "Loaded: $studyTimerItems")
    }

    override fun onResume() {
        super.onResume()
        sharedPreferences.registerOnSharedPreferenceChangeListener(this)
        updateRecyclerView()
        Log.i("HomeFragment", "onResume: studyTimers=$studyTimerItems timerItems=${timerItems}")
    }

    override fun onPause() {
        super.onPause()
        Log.i("HomeFragment", "onPause called: $studyTimerItems")
        saveStudyTimersToFile()
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onStop() {
        super.onStop()
        Log.i("HomeFragment", "onStop: studyTimers=$studyTimerItems timerItems=${timerItems}")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.i(
            "HomeFragment",
            "onDestroyView: studyTimers=$studyTimerItems timerItems=${timerItems}"
        )
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
        loadStudyTimersFromFile()
        itemAdapter.notifyDataSetChanged()
//        Log.i("HomeFragment", "updateRecyclerView: $studyTimeList")
    }

    override fun onItemUpdated(item: StudyTimer, position: Int) {
        studyTimerItems[position] = item
        itemAdapter.updateTimerItemsToMatchStudyTimers()
        Log.d("HomeFragment", "Item Updated: $studyTimerItems $timerItems")
    }

    override fun onItemRemoved(position: Int) {
        studyTimerItems.removeAt(position)
        itemAdapter.updateTimerItemsToMatchStudyTimers()
        Log.d("HomeFragment", "Item Removed: $studyTimerItems $timerItems")
    }

    override fun onItemClick(position: Int) {
        Log.d("HomeFragment", "onItemClick")

    }
}