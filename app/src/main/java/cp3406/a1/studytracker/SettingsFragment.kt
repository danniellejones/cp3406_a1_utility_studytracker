/**
 * Settings fragment to change shared preferences.
 */

package cp3406.a1.studytracker

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat

/** Set up settings fragment and link to root preferences */
class SettingsFragment : PreferenceFragmentCompat() {

    /** Use sharedPreferences and link to root preferences to create settings */
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }
}

