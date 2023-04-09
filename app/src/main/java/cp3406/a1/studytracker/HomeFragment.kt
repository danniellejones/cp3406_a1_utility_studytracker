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
import cp3406.a1.studytracker.model.StudyTimer

/**
 * Home Fragment for the main interaction screen.
 */
@Suppress("DEPRECATION")
class HomeFragment : Fragment(), SharedPreferences.OnSharedPreferenceChangeListener, ItemAdapter.OnItemActionListener {

    private lateinit var itemAdapter: ItemAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var floatingAddButton: FloatingActionButton
    private var studyTimeList: ArrayList<StudyTimer> = ArrayList()

    /**
     * Inflates the fragment_home layout, adds list_item layout to view and sets default from settings
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        setHasOptionsMenu(true)
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
        return view
    }

    /** Inflate menu */
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate((R.menu.settings_menu), menu)
    }

    /** Handle app bar selection */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return NavigationUI.onNavDestinationSelected(
            item,
            requireView().findNavController()
        ) || super.onOptionsItemSelected(item)
    }

    /** Populate data, initialise recycler view and add button, create instance of adapter  */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Populate list with data from last session
        loadList()
        // Set up recycler view
        recyclerView = view.findViewById(R.id.tracker_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        // Set Adapter
        itemAdapter = ItemAdapter(requireContext(), studyTimeList)
        itemAdapter.setOnItemActionListener(this)
        // Set Recycler view Adapter
        recyclerView.adapter = itemAdapter

        // Set up add button with dialog
        floatingAddButton =
            view.findViewById(R.id.floating_add_button)
        floatingAddButton.setOnClickListener { addNewTimer() }
    }


    /** Add new study timer  */
    private fun addNewTimer() {
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
            recyclerView.layoutManager?.scrollToPosition(studyTimeList.size -1)
            itemAdapter.notifyItemInserted(studyTimeList.size - 1)

            // Known repeating code: required to force update of recycler view
            itemAdapter = ItemAdapter(requireContext(), studyTimeList)
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
    private fun saveList() {
        val editor = sharedPreferences.edit()
        val gson = Gson()
        val json = gson.toJson(studyTimeList)
        editor.putString("studyTimeList", json)
        editor.apply()
    }

    /** Load data from json file to populate recycler view. */
    private fun loadList() {
        val gson = Gson()
        val json = sharedPreferences.getString("studyTimeList", null)
        val type = object : TypeToken<ArrayList<StudyTimer>>() {}.type
        studyTimeList = gson.fromJson(json, type) ?: ArrayList()
    }

    /** Reload items and refresh view on resume */
    override fun onResume() {
        super.onResume()
        sharedPreferences.registerOnSharedPreferenceChangeListener(this)
        updateRecyclerView()
    }

    /** Save study timer data on pause */
    override fun onPause() {
        super.onPause()
        saveList()
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }

    /** Look for changes to the default timer settings */
    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        if (key == "default_time_key") {
            // Testing purpose only, required to override method
            sharedPreferences?.getString("default_time_key", "")
        }
    }

    /** Reload the data and update the recycler view */
    @SuppressLint("NotifyDataSetChanged")
    private fun updateRecyclerView() {
        loadList()
        itemAdapter.notifyDataSetChanged()
        Log.d("HomeFragment", "updateRV: $studyTimeList")
    }

    /** Update the values of items at a certain position */
    override fun onItemUpdated(item: StudyTimer, position: Int) {
        studyTimeList[position] = item
    }

    /** Remove an item at a certain position */
    override fun onItemRemoved(position: Int) {
        studyTimeList.removeAt(position)
    }
}