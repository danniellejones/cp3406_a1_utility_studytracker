package cp3406.a1.studytracker

import android.app.AlertDialog
import android.content.Context

fun displayWarningAlertDialog(context: Context, titleResourceId: Int, messageResourceId: Int) {
    val title = context.getString(titleResourceId)
    val message = context.getString(messageResourceId)

    val builder = AlertDialog.Builder(context)
    builder.setIcon(R.drawable.warning_icon)
    builder.setTitle(title)
    builder.setMessage(message)
    builder.setPositiveButton("OK") { dialog, _ ->
        dialog.dismiss()
    }
    builder.create()
    builder.show()
}