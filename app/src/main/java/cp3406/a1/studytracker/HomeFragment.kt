package cp3406.a1.studytracker

import android.app.AlertDialog
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import cp3406.a1.studytracker.adapter.ItemAdapter
import cp3406.a1.studytracker.model.StudyTimer

/**
 * Home Fragment for the main interaction screen.
 */
class HomeFragment : Fragment(), SharedPreferences.OnSharedPreferenceChangeListener {

    private lateinit var floatingAddButton: FloatingActionButton
    private lateinit var recv: RecyclerView
    private lateinit var studyTimeList: ArrayList<StudyTimer>
    private lateinit var itemAdapter: ItemAdapter
    private lateinit var defaultTimeView: TextView

//    private var defaultTimeView = view?.findViewById<TextView>(R.id.default_time)


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)

        val view = inflater.inflate(R.layout.fragment_home, container, false)
        val listView = inflater.inflate(R.layout.list_item, null)
        defaultTimeView = listView.findViewById(R.id.default_time)
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

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        defaultTimeView = view.findViewById(R.id.default_time)

        // Set List
        studyTimeList = ArrayList()
        // Set Find Id
        floatingAddButton =
            view.findViewById(R.id.floating_add_button)
        recv = view.findViewById(R.id.tracker_recycler_view)
        // Set Adapter
        itemAdapter = ItemAdapter(requireContext(), studyTimeList)
        // Set Recycler view Adapter
        recv.layoutManager = LinearLayoutManager(requireContext())
        recv.adapter = itemAdapter
        // Set Dialog
        floatingAddButton.setOnClickListener{addInfo()}
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
            itemAdapter.notifyDataSetChanged()
            Toast.makeText(requireContext(), "Adding", Toast.LENGTH_LONG).show()
            dialog.dismiss()
        }
        addDialog.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }
        addDialog.create()
        addDialog.show()
    }


    override fun onResume() {
        super.onResume()
        Log.i("HomeFragment", "onResume called")
        PreferenceManager.getDefaultSharedPreferences(requireContext())
            .registerOnSharedPreferenceChangeListener(this)
        updateDefaultTime()
    }

    override fun onPause() {
        super.onPause()
        Log.i("HomeFragment", "onPause called")
        PreferenceManager.getDefaultSharedPreferences(requireContext())
            .unregisterOnSharedPreferenceChangeListener(this)
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
        val defaultTime = PreferenceManager.getDefaultSharedPreferences(requireContext())
            .getString("default_time_key", "")
        val defaultTimeString = getString(R.string.default_time_with_value, defaultTime ?: "N/A")
        defaultTimeView.text = defaultTimeString
        Log.d("HomeFragment", "Default time: $defaultTimeString")
    }
}