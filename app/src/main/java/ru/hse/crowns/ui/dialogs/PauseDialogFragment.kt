package ru.hse.crowns.ui.dialogs

import android.app.Dialog
import android.content.DialogInterface
import android.content.DialogInterface.OnClickListener
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import ru.hse.crowns.R

/**
 * @property onCancel on cancel dialog callback
 */
class PauseDialogFragment (
    private val onClickListener: OnClickListener,
    private val onCancel: () -> Unit
) : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setTitle(getString(R.string.pause))
                .setPositiveButton(getString(R.string.new_level), onClickListener)
                .setNegativeButton(getString(R.string.rerun), onClickListener)
                .setNeutralButton(getString(R.string.menu), onClickListener)
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    override fun onCancel(dialog: DialogInterface) {
        onCancel()
        super.onCancel(dialog)
    }
}