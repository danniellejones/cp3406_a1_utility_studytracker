package cp3406.a1.studytracker

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
class HomeFragment : Fragment(), SharedPreferences.OnSharedPreferenceChangeListener {

    private lateinit var itemAdapter: ItemAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var studyTimeList: ArrayList<StudyTimer>
//    private lateinit var fragmentManager: FragmentManager
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var defaultTimeView: TextView
    private lateinit var floatingAddButton: FloatingActionButton
//    private lateinit var deleteButton: Button


//    private var defaultTimeView = view?.findViewById<TextView>(R.id.default_time)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i("HomeFragment", "onCreate called")
//        replacementFragment(HomeFragment())
//        updateRecyclerView()
//        Log.i("HomeFragment", "onCreate: $studyTimeList")



    }

    // Inflates the fragment_home layout, adds list_item layout to view and sets default from settings
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        setHasOptionsMenu(true)

        val view = inflater.inflate(R.layout.fragment_home, container, false)
        val listView = inflater.inflate(R.layout.list_item, container, false)
        defaultTimeView = listView.findViewById(R.id.default_time)
        // add the listView to the main view
        view.findViewById<FrameLayout>(R.id.frame_recycler_home).addView(listView)
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
        Log.i("HomeFragment", "OnCreateView called")
        return view


        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_home, container, false)
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
        Log.d("HomeFragment", "OnStart: $studyTimeList")

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set List
        studyTimeList = ArrayList()
        loadList()
        Log.d("HomeFragment", "OnViewCreated: $studyTimeList")
        // Set up recycler view
        recyclerView = view.findViewById(R.id.tracker_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        // Set Adapter
        itemAdapter = ItemAdapter(requireContext(), studyTimeList)
        // Set Recycler view Adapter
        recyclerView.adapter = itemAdapter

        // Set up add button with dialog
        floatingAddButton =
            view.findViewById(R.id.floating_add_button)
        floatingAddButton.setOnClickListener { addInfo() }
//        deleteButton.setOnClickListener { removeStudyTimer() }
//        itemAdapter.notifyDataSetChanged()
        Log.i("HomeFragment", "onViewCreated called")
    }

    private fun addInfo() {
        val inflaterA = LayoutInflater.from(requireContext())
        val v = inflaterA.inflate(R.layout.add_item, null)

        val newStudyTimerTitle = v.findViewById<EditText>(R.id.new_title)
        val newStudyTimerTime = v.findViewById<EditText>(R.id.new_time)
        val addDialog = AlertDialog.Builder(requireContext())
        addDialog.setView(v)
        addDialog.setPositiveButton("Ok") { dialog, _ ->
            val title = newStudyTimerTitle.text.toString()
            val timer = newStudyTimerTime.text.toString()
            studyTimeList.add(StudyTimer(title, timer))
//            itemAdapter.notifyDataSetChanged()
            itemAdapter.notifyItemInserted(studyTimeList.size -1)
            Toast.makeText(requireContext(), "Adding", Toast.LENGTH_LONG).show()
            dialog.dismiss()
        }
        addDialog.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }
        addDialog.create()
        addDialog.show()
    }

//    private fun removeStudyTimer() {
//        val inflaterA = LayoutInflater.from(requireContext())
//        val v = inflaterA.inflate(R.layout.list_item, null)
//        val inflaterB = LayoutInflater.from(requireContext())
//        val v2 = inflaterB.inflate(R.layout.fragment_home, null)
//        recyclerView = v2.findViewById(R.id.tracker_recycler_view)
//        deleteButton = v.findViewById<Button>(R.id.delete_button)
//
//        recyclerView.removeView(v)
//        }

   private fun saveList() {
        val editor = sharedPreferences.edit()
        val gson = Gson()
        val json = gson.toJson(studyTimeList)
        editor.putString("studyTimeList", json)
        editor.apply()
       Log.d("HomeFragment", "dataSaved")
       Log.d("HomeFragment", "$studyTimeList")
    }

    private fun loadList() {
        val gson = Gson()
        val json = sharedPreferences.getString("studyTimeList", null)
        val type = object : TypeToken<ArrayList<StudyTimer>>() {}.type
        studyTimeList = gson.fromJson(json, type) ?: ArrayList()
        Log.d("HomeFragment", "dataLoaded")
        Log.d("HomeFragment", "From load list: $studyTimeList")
    }


    override fun onResume() {
        super.onResume()
        Log.i("HomeFragment", "onResume called")
        sharedPreferences.registerOnSharedPreferenceChangeListener(this)
        updateDefaultTime()
        updateRecyclerView()
        Log.d("HomeFragment", "onResume: $studyTimeList")

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
        val defaultTimeString = getString(R.string.default_time_with_value, defaultTime ?: "N/A")
        defaultTimeView.text = defaultTimeString
        Log.d("HomeFragment", "Default time: $defaultTimeString")
    }

    private fun updateRecyclerView() {
        loadList()
        itemAdapter.notifyDataSetChanged()
        Log.d("HomeFragment", "After recycler view update: $studyTimeList")
    }

//    private fun replacementFragment(homeFragment: Fragment) {
//        val fragmentManager = supportFragmentManager
//        val fragmentTransaction = fragmentManager.beginTransaction()
//        fragmentTransaction.replace(R.id.tracker_recycler_view, homeFragment)
//        fragmentTransaction.commit()
//    }
}