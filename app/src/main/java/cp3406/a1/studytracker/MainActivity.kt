package cp3406.a1.studytracker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Explicit type declaration binding
//        val binding: ActivityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        // Type inference binding
//        val binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
        setContentView(R.layout.activity_main)

        // Find NavController
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.mainFragment) as NavHostFragment
        val navController = navHostFragment.navController
        NavigationUI.setupActionBarWithNavController(this, navController)
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
}