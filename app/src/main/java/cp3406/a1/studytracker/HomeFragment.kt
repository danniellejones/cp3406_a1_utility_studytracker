package cp3406.a1.studytracker

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.preference.PreferenceManager


/**
 * Home Fragment for the main interaction screen.
 */
class HomeFragment : Fragment(),
    SharedPreferences.OnSharedPreferenceChangeListener {

    private var defaultTimeView = view?.findViewById<TextView>(R.id.default_time)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)

//        val view = inflater.inflate(R.layout.fragment_home, container, false)
        return inflater.inflate(R.layout.fragment_home, container, false)

//        val sharedPrefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
//        val defaultTime = sharedPrefs.getString("default_time", "Peanuts")
//        val defaultTimeView = view?.findViewById<TextView>(R.id.default_time)
//        val defaultTimeString = getString(R.string.default_time_with_value, defaultTime ?: "N/A")
//        Log.d("HomeFragment", "Default time: $defaultTimeString")
//        defaultTimeView?.text = defaultTimeString
//        defaultTimeView = view.findViewById(R.id.default_time)

        // Inflate the layout for this fragment
//        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate((R.menu.options_menu), menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return NavigationUI.
        onNavDestinationSelected(item,requireView().findNavController())
                || super.onOptionsItemSelected(item)
    }

    override fun onStart() {
        super.onStart()
        Log.i("HomeFragment", "onStart called")

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        defaultTimeView = view.findViewById(R.id.default_time)
    }
    override fun onResume() {
        super.onResume()
        Log.i("HomeFragment", "onResume called")
        PreferenceManager.getDefaultSharedPreferences(requireContext())
            .registerOnSharedPreferenceChangeListener(this)

//        val defaultTime = PreferenceManager.getDefaultSharedPreferences(requireContext())
//            .getString("default_time", "")
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
            // Get the updated default time value
//            val defaultTime = sharedPreferences?.getString(key, "")

            // Update the default time text view
            updateDefaultTime()
        }
    }

    private fun updateDefaultTime() {

//        val sharedPrefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
//        val defaultTime = sharedPrefs.getString("default_time", "Peanuts")
//        val defaultTimeView = view?.findViewById<TextView>(R.id.default_time)
//        val defaultTimeString = getString(R.string.default_time_with_value, defaultTime ?: "N/A")
//        defaultTimeView?.text = defaultTimeString
//        Log.d("HomeFragment", "Default time: $defaultTimeString")

        val defaultTime = PreferenceManager.getDefaultSharedPreferences(requireContext()).getString("default_time_key", "")
        val defaultTimeString = getString(R.string.default_time_with_value, defaultTime ?: "N/A")
        defaultTimeView?.text = defaultTimeString
        Log.d("HomeFragment", "Default time: $defaultTimeString")

//        val defaultTimeView = view?.findViewById<TextView>(R.id.default_time)
//        val defaultTimeString = getString(R.string.default_time_with_value, defaultTime)
//        defaultTimeView?.text = defaultTimeString
//        Log.d("HomeFragment", "Default time: $defaultTimeString")
    }
}