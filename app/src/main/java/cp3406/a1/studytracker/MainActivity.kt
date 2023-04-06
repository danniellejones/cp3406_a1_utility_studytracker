package cp3406.a1.studytracker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import cp3406.a1.studytracker.databinding.ActivityMainBinding

//import cp3406.a1.studytracker.android.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        val binding: ActivityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
//        val binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
        setContentView(R.layout.activity_main)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.mainFragment) as NavHostFragment
        val navController = navHostFragment.navController

        NavigationUI.setupActionBarWithNavController(this, navController)
//        NavigationUI.setupWithNavController(bottomNavigationView, navController)
//        setupActionBarWithNavController(findNavController(R.id.mainFragment))

//        val navController = findNavController(R.id.mainFragment)
//        NavigationUI.setupActionBarWithNavController(this, navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = this.findNavController(R.id.mainFragment)
        return if (navController.currentDestination?.id == R.id.homeFragment) {
            false
        } else {
            navController.navigateUp() || super.onSupportNavigateUp()
        }
    }



//        override fun onSupportNavigateUp(): Boolean {
//        val navController = findNavController(R.id.mainFragment)
//        return navController.navigateUp() || super.onSupportNavigateUp()
//    }
}