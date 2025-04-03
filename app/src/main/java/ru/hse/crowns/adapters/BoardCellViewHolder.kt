package ru.hse.crowns.adapters

import android.content.res.TypedArray
import android.graphics.drawable.Drawable
import android.view.ViewGroup.MarginLayoutParams
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

    fun setValuePicture(picture: Drawable?, description: String) {
        binding.valueImageView.setImageDrawable(picture)
        binding.valueImageView.contentDescription = description
    }

    fun setPolyominoColor(polyominoId: Int) {
        val color: Int = colors.getColor(polyominoId,"#ffffff".toColorInt())
        binding.backgroundCardView.setCardBackgroundColor(color)
    }

    fun setAdditionalInfo(text: String) {
        binding.additionalInfoTextView.text = text
    }

    fun makeLeftBorderBold() {
        if(!isLeftBorderBold) {
            if (binding.backgroundCardView.layoutParams is MarginLayoutParams) {
                val params: MarginLayoutParams =
                    binding.backgroundCardView.layoutParams as MarginLayoutParams
                params.leftMargin *= 2
                binding.backgroundCardView.layoutParams = params
            }
            isLeftBorderBold = true
        }
    }

    fun makeRightBorderBold() {
        if(!isRightBorderBold) {
            if (binding.backgroundCardView.layoutParams is MarginLayoutParams) {
                val params: MarginLayoutParams =
                    binding.backgroundCardView.layoutParams as MarginLayoutParams
                params.rightMargin *= 2
                binding.backgroundCardView.layoutParams = params
            }
            isRightBorderBold = true
        }
    }

    fun makeBottomBorderBold() {
        if(!isBottomBorderBold) {
            if (binding.backgroundCardView.layoutParams is MarginLayoutParams) {
                val params: MarginLayoutParams =
                    binding.backgroundCardView.layoutParams as MarginLayoutParams
                params.bottomMargin *= 2
                binding.backgroundCardView.layoutParams = params
            }
            isBottomBorderBold = true
        }
    }

    fun makeTopBorderBold() {
        if(!isUpBorderBold) {
            if (binding.backgroundCardView.layoutParams is MarginLayoutParams) {
                val params: MarginLayoutParams =
                    binding.backgroundCardView.layoutParams as MarginLayoutParams
                params.topMargin *= 2
                binding.backgroundCardView.layoutParams = params
            }
            isUpBorderBold = true
        }
    }

}