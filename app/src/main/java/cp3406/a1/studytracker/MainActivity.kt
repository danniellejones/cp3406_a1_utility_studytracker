package cp3406.a1.studytracker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.service.autofill.Dataset
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import cp3406.a1.studytracker.adapter.ItemAdapter
import cp3406.a1.studytracker.model.StudyTimer

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Explicit type declaration binding
//        val binding: ActivityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        // Type inference binding
//        val binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
        setContentView(R.layout.activity_main)
//        replaceFragment(HomeFragment())
        // Find NavController
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.mainFragment) as NavHostFragment
        val navController = navHostFragment.navController
        NavigationUI.setupActionBarWithNavController(this, navController)

        Log.i("MainActivity", "onCreate called")
    }

    // Navigation up to only appear everywhere other than home fragment
    override fun onSupportNavigateUp(): Boolean {
        val navController = this.findNavController(R.id.mainFragment)
        return if (navController.currentDestination?.id == R.id.homeFragment) {
            false
        } else {
            navController.navigateUp() || super.onSupportNavigateUp()
        }
    }

//    private fun replaceFragment(homeFragment: Fragment) {
//        val fragmentManager = supportFragmentManager
//        val fragmentTransaction = fragmentManager.beginTransaction()
//        fragmentTransaction.replace(R.id.tracker_recycler_view, homeFragment)
//        fragmentTransaction.commit()
//    }
}