package cp3406.a1.studytracker.data

import cp3406.a1.studytracker.R
import cp3406.a1.studytracker.model.StudyTimer

class Datasource {

    fun loadStudyTimers(): List<StudyTimer> {
        return listOf(
            StudyTimer(R.string.item_title)
        )
    }
}