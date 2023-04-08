/**
 * Settings fragment to change shared preferences.
 */

package cp3406.a1.studytracker

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.preference.PreferenceFragmentCompat

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.i("SettingsFragment", "onAttach called")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i("SettingsFragment", "onCreate called")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.i("SettingsFragment", "onViewCreated called")
    }

    override fun onStart() {
        super.onStart()
        Log.i("SettingsFragment", "onStart called")
    }

    override fun onResume() {
        super.onResume()
        Log.i("SettingsFragment", "onResume called")
    }

    override fun onPause() {
        super.onPause()
        Log.i("SettingsFragment", "onPause called")
    }

    override fun onStop() {
        super.onStop()
        Log.i("SettingsFragment", "onStop called")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.i("SettingsFragment", "onDestroyView called")
    }

    override fun onDetach() {
        super.onDetach()
        Log.i("SettingsFragment", "onDetach called")
    }
}