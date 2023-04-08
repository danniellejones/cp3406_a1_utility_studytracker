/**
 * Home fragment.
 */

package cp3406.a1.studytracker

import android.app.AlertDialog
import android.content.Context
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
import android.os.CountDownTimer

/**
 * Home Fragment for the main interaction screen.
 */
class HomeFragment : Fragment(), SharedPreferences.OnSharedPreferenceChangeListener, ItemAdapter.OnItemActionListener, ItemAdapter.OnCountDownStateChangedListener {

    private lateinit var itemAdapter: ItemAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var floatingAddButton: FloatingActionButton
//    private lateinit var countDownButton: Button

//    private var isCountingDown: Boolean = false
    private var studyTimeList: ArrayList<StudyTimer> = ArrayList()

    //    private lateinit var defaultTimeView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        isCountingDown = false  // Set again here, in case previous run left as true
        Log.i("HomeFragment", "onCreate called")
    }

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
        Log.i("HomeFragment", "OnCreateView called")
        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate((R.menu.options_menu), menu)
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Populate list with data from last session
        loadList()
        Log.d("HomeFragment", "OnViewCreated: $studyTimeList")
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
//        countDownButton = view.findViewById(R.id.play_button)
//        countDownButton.setOnClickListener { toggleCountDownPlay() }

        Log.i("HomeFragment", "onViewCreated after add: $studyTimeList")
        Log.i("HomeFragment", "onViewCreated called")
    }


    private fun addNewTimer() {
        val inflaterForAddItem = LayoutInflater.from(requireContext())
        val viewForAddItem = inflaterForAddItem.inflate(R.layout.add_item, null)

        val newStudyTimerTitle = viewForAddItem.findViewById<EditText>(R.id.new_title)
        val newStudyTimerTime = viewForAddItem.findViewById<EditText>(R.id.new_time)
        val addDialog = AlertDialog.Builder(requireContext())
        addDialog.setView(viewForAddItem)
        addDialog.setPositiveButton("Save") { dialog, _ ->
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
            var newItemTest = studyTimeList[studyTimeList.size - 1]
            recyclerView.getLayoutManager()?.scrollToPosition(studyTimeList.size -1)
            Log.i("HomeFragment", "Add called: $newItemTest")
            itemAdapter.notifyItemInserted(studyTimeList.size - 1)

            // TODO: Redundant code: required to force update of recycler view
            //  - tried passing in as itemAdapter as parameter from onViewCreated
            //  - tried using recyclerView.adapter
            itemAdapter = ItemAdapter(requireContext(), studyTimeList)
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

    /**
     * Save data to json file from data used in recycler view.
     */
    private fun saveList() {
        val editor = sharedPreferences.edit()
        val gson = Gson()
        val json = gson.toJson(studyTimeList)
        editor.putString("studyTimeList", json)
        editor.apply()
        Log.d("HomeFragment", "dataSaved: $studyTimeList")
    }

    /**
     * Load data from json file to populate recycler view.
     */
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
//        updateDefaultTime()
        updateRecyclerView()
        Log.i("HomeFragment", "onResume called: $studyTimeList")

    }

    override fun onPause() {
        super.onPause()
        Log.i("HomeFragment", "onPause called")
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
//        val defaultTimeString = getString(R.string.default_time_with_value, defaultTime ?: "N/A")
//        defaultTimeView.text = defaultTimeString
        Log.d("HomeFragment", "Default time: $defaultTime")
    }

    private fun updateRecyclerView() {
        loadList()
        itemAdapter.notifyDataSetChanged()
        Log.d("HomeFragment", "updateRV: $studyTimeList")
    }

    override fun onItemUpdated(item: StudyTimer, position: Int) {
        studyTimeList[position] = item
    }

    override fun onItemRemoved(position: Int) {
        studyTimeList.removeAt(position)
    }

    override fun onCountDownStateChanged(isCountingDown: Boolean) {
        val sharedPreferences = requireActivity().getPreferences(Context.MODE_PRIVATE)
        sharedPreferences.edit().putBoolean("IS_COUNTING_DOWN", isCountingDown).apply()
    }
}