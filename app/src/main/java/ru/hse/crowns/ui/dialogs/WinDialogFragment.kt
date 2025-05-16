package ru.hse.crowns.ui.dialogs

import android.app.Dialog
import android.content.DialogInterface.OnClickListener
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import ru.hse.crowns.R

/**
 * @param prize amount of coins that will be added to users account
 */
class WinDialogFragment(private val prize: Int, private val onClickListener: OnClickListener) :
    DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // win dialog cannot be cancelled
        isCancelable = false
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setTitle(getString(R.string.win))
                .setMessage(getString(R.string.prize, prize))
                .setPositiveButton(getString(R.string.new_level), onClickListener)
                .setNeutralButton(getString(R.string.menu), onClickListener)
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}