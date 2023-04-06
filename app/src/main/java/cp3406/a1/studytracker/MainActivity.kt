package cp3406.a1.studytracker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
//import cp3406.a1.studytracker.android.navigation.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        val binding = DataBindingUtil.setContentView<ActivityMainBuilding>(this, R.layout.activity_main)
        setContentView(R.layout.activity_main)

//        val navController = this.findNavController(R.id.mainFragment)
//        NavigationUI.setupActionBarWithNavController(this,navController)
    }

//    override fun onSupportNavigateUp(): Boolean {
//        val navController = this.findNavController(R.id.mainFragment)
//        return navController.navigateUp()
//    }
}