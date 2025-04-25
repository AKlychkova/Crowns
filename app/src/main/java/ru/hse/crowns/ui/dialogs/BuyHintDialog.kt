package ru.hse.crowns.ui.dialogs

import android.app.Dialog
import android.content.DialogInterface.OnClickListener
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import ru.hse.crowns.R
import ru.hse.crowns.utils.HINT_PRICE

class BuyHintDialog (private val onClickListener: OnClickListener) : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setTitle(getString(R.string.buy_hint, HINT_PRICE))
                .setPositiveButton(getString(R.string.yes), onClickListener)
                .setNeutralButton(getString(R.string.cancel), onClickListener)
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}