package cp3406.a1.studytracker

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.preference.PreferenceManager

import android.os.Build
import android.content.SharedPreferences
import android.util.Log
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import kotlinx.android.synthetic.main.fragment_home.*

//import cp3406.a1.studytracker.databinding.FragmentHomeBinding

//import kotlinx.android.synthetic.main.fragment_home.*

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
//        val binding = DataBindingUtil.inflate<FragmentHomeBinding>(inflater, R.layout.fragment_home, container, false)
//        val binding: FragmentHomeBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        // Inflate the layout for this fragment
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_home, container, false)
//        return binding.root
    }

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        val navController = this.findNavController(R.id.mainFragment)
//        NavigationUI.setupActionBarWithNavController(this, navController)
//    }


//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        fab_settings.setOnClickListener {
//            findNavController().navigate(R.id.action_homeFragment_to_settingsFragment)
//        }
//
//        loadSettings()
//    }
//
//    private fun loadSettings() {
//        val sp = PreferenceManager.getDefaultSharedPreferences(getBaseContext())
//        val defaultTime = sp.getString("default_time_key", "")
//
//        default_time.text = "Default time: $defaultTime"
//    }
//
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