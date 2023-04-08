/**
 * Main activity, sets up menu and navigation.
 */

package cp3406.a1.studytracker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI

/** Create navigation and up buttons */
class MainActivity : AppCompatActivity() {

    /** Create navigation */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Find NavController
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        NavigationUI.setupActionBarWithNavController(this, navController)
    }

    /** Navigation up button set to only appear everywhere other than home fragment */
    override fun onSupportNavigateUp(): Boolean {
        val navController = this.findNavController(R.id.nav_host_fragment)
        return if (navController.currentDestination?.id == R.id.homeFragment) {
            false
        } else {
            navController.navigateUp() || super.onSupportNavigateUp()
        }
    }
}