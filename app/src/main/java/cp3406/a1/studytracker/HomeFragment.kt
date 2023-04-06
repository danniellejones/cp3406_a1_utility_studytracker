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
class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)

        val view = inflater.inflate(R.layout.fragment_home, container, false)

        val sharedPrefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
        val defaultTime = sharedPrefs.getString("default_time", "Peanuts")
        val defaultTimeView = view?.findViewById<TextView>(R.id.default_time)
        val defaultTimeString = getString(R.string.default_time_with_value, defaultTime ?: "N/A")
        Log.d("HomeFragment", "Default time: $defaultTimeString")
        defaultTimeView?.text = defaultTimeString

        // Inflate the layout for this fragment
        return view
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


}