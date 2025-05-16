package ru.hse.crowns.adapters

import android.content.res.TypedArray
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.view.ViewGroup.MarginLayoutParams
import android.widget.TextView
import androidx.core.graphics.toColorInt
import androidx.recyclerview.widget.RecyclerView
import ru.hse.crowns.R
import ru.hse.crowns.databinding.BoardCellBinding


class BoardCellViewHolder(private val binding: BoardCellBinding) :
    RecyclerView.ViewHolder(binding.root) {
    private val colors: TypedArray = itemView.resources.obtainTypedArray(R.array.polyomino_colors)

    private var isLeftBorderBold = false
    private var isRightBorderBold = false
    private var isUpBorderBold = false
    private var isBottomBorderBold = false

    private val noteTextViews: Array<TextView> = arrayOf(
        binding.noteTextView1,
        binding.noteTextView2,
        binding.noteTextView3,
        binding.noteTextView4,
        binding.noteTextView5,
        binding.noteTextView6,
        binding.noteTextView7,
        binding.noteTextView8,
        binding.noteTextView9,
    )

    /**
     * Set [picture] as the value of the cell
     * @param picture value picture
     * @param description content description
     * @param tint tint of the picture
     */
    fun setValuePicture(picture: Drawable?, description: String, tint: Color? = null) {
        binding.valueImageView.setImageDrawable(picture)
        binding.valueImageView.contentDescription = description
        if (tint != null) {
            binding.valueImageView.setColorFilter(tint.toArgb())
        }
    }

    /**
     * Set color to the cell according to the polyomino id. Colors are defined in res/values/arrays.
     */
    fun setPolyominoColor(polyominoId: Int) {
        val color: Int = colors.getColor(polyominoId, "#ffffff".toColorInt())
        binding.backgroundCardView.setCardBackgroundColor(color)
    }

    /**
     * Set [text] to the top left corner of the cell
     */
    fun setAdditionalInfo(text: String) {
        binding.additionalInfoTextView.text = text
    }

    /**
     * If left board is not bold, double its thickness
     */
    fun makeLeftBorderBold() {
        if (!isLeftBorderBold) {
            if (binding.backgroundCardView.layoutParams is MarginLayoutParams) {
                val params: MarginLayoutParams =
                    binding.backgroundCardView.layoutParams as MarginLayoutParams
                params.leftMargin *= 2
                binding.backgroundCardView.layoutParams = params
            }
            isLeftBorderBold = true
        }
    }

    /**
     * If right board is not bold, double its thickness
     */
    fun makeRightBorderBold() {
        if (!isRightBorderBold) {
            if (binding.backgroundCardView.layoutParams is MarginLayoutParams) {
                val params: MarginLayoutParams =
                    binding.backgroundCardView.layoutParams as MarginLayoutParams
                params.rightMargin *= 2
                binding.backgroundCardView.layoutParams = params
            }
            isRightBorderBold = true
        }
    }

    /**
     * If bottom board is not bold, double its thickness
     */
    fun makeBottomBorderBold() {
        if (!isBottomBorderBold) {
            if (binding.backgroundCardView.layoutParams is MarginLayoutParams) {
                val params: MarginLayoutParams =
                    binding.backgroundCardView.layoutParams as MarginLayoutParams
                params.bottomMargin *= 2
                binding.backgroundCardView.layoutParams = params
            }
            isBottomBorderBold = true
        }
    }

    /**
     * If top board is not bold, double its thickness
     */
    fun makeTopBorderBold() {
        if (!isUpBorderBold) {
            if (binding.backgroundCardView.layoutParams is MarginLayoutParams) {
                val params: MarginLayoutParams =
                    binding.backgroundCardView.layoutParams as MarginLayoutParams
                params.topMargin *= 2
                binding.backgroundCardView.layoutParams = params
            }
            isUpBorderBold = true
        }
    }

    /**
     * Set text to one of nine note note text views.
     * @param text text that will be set
     * @param textViewId number from 0 to 8. If it is out of bounds, nothing happens.
     */
    fun setNote(text: String?, textViewId: Int) {
        if (textViewId in noteTextViews.indices) {
            noteTextViews[textViewId].text = text
        }
    }

    /**
     * Clear all notes text views
     */
    fun clearNotes() {
        for(textView in noteTextViews) {
            textView.text = null
        }
    }

    /**
     * Apply red color
     */
    fun highlightRed() {
        val color: Int = itemView.resources.getColor(R.color.mistake, null)
        binding.highlightView.setBackgroundColor(color)
    }

    /**
     * Clear highlight color
     */
    fun removeHighlightColor() {
        binding.highlightView.background = null
    }

    /**
     * Apply green color
     */
    fun highlightGreen() {
        val color: Int = itemView.resources.getColor(R.color.highlight, null)
        binding.highlightView.setBackgroundColor(color)
    }
}