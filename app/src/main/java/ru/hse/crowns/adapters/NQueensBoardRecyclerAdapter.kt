package ru.hse.crowns.adapters

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.toColor
import androidx.recyclerview.widget.RecyclerView
import ru.hse.crowns.R
import ru.hse.crowns.domain.domainObjects.boards.NQueensBoard
import ru.hse.crowns.databinding.BoardCellBinding
import ru.hse.crowns.domain.domainObjects.boards.QueenCellStatus

/**
 * RecyclerView adapter for n queens board
 * @param onItemClick on board cell click callback
 */
class NQueensBoardRecyclerAdapter(
    private val onItemClick: (row: Int, column: Int) -> Unit
) :
    RecyclerView.Adapter<BoardCellViewHolder>() {

    private lateinit var board: NQueensBoard
    private var currentRed = emptySet<Int>()
    private var currentGreen = emptySet<Int>()

    /**
     * Allows not to bind all itemView but change only a part of it
     */
    private enum class Payload {
        VALUE,
        HIGHLIGHT_RED,
        HIGHLIGHT_GREEN,
        REMOVE_HIGHLIGHT,
        LISTENER
    }

    /**
     * Set new [board][newBoard]
     * @param newBoard new board that will be displayed in the recyclerview
     */
    @SuppressLint("NotifyDataSetChanged")
    fun setBoard(newBoard: NQueensBoard) {
        board = newBoard
        notifyDataSetChanged()
    }

    /**
     * Update value of cell with coordinates ([row], [column])
     */
    fun updateCellValue(row: Int, column: Int) {
        notifyItemChanged(row * board.size + column, Payload.VALUE)
    }

    /**
     * Highlight [greenPositions] with green color and [redPositions] with red color.
     * If position is in [greenPositions] and in [redPositions], green color is dominant.
     * @param greenPositions positions that will be colored green
     * @param redPositions positions that will be colored red
     */
    fun updateHighlights(
        greenPositions: Iterable<Pair<Int, Int>> = emptyList(),
        redPositions: Iterable<Pair<Int, Int>> = emptyList()
    ) {
        val green = greenPositions.map { it.first * board.size + it.second }.toSet()
        for (position in (green - currentGreen)) {
            notifyItemChanged(position, Payload.HIGHLIGHT_GREEN)
        }
        for (position in (currentGreen - green)) {
            notifyItemChanged(position, Payload.REMOVE_HIGHLIGHT)
        }
        currentGreen = green

        val red = redPositions.map { it.first * board.size + it.second }.toSet()
        for (position in (red - currentRed - currentGreen)) {
            notifyItemChanged(position, Payload.HIGHLIGHT_RED)
        }
        for (position in (currentRed - red)) {
            notifyItemChanged(position, Payload.REMOVE_HIGHLIGHT)
        }
        currentRed = red - currentGreen
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BoardCellViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = BoardCellBinding.inflate(layoutInflater, parent, false)
        return BoardCellViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return if (this::board.isInitialized) {
            board.size * board.size
        } else {
            0
        }
    }

    override fun onBindViewHolder(holder: BoardCellViewHolder, position: Int) {
        onBindViewHolder(
            holder, position, mutableListOf(
                Payload.VALUE,
                Payload.LISTENER,
                when (position) {
                    in currentRed -> Payload.HIGHLIGHT_RED
                    in currentGreen -> Payload.HIGHLIGHT_GREEN
                    else -> Payload.REMOVE_HIGHLIGHT
                }
            )
        )
    }

    override fun onBindViewHolder(
        holder: BoardCellViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isNotEmpty()) {
            val row = position / board.size
            val column = position % board.size

            for (payload: Payload in payloads.mapNotNull { it as? Payload }) {
                when (payload) {
                    Payload.VALUE -> {
                        // Get itemView resources
                        val resources = holder.itemView.resources
                        // Get drawables
                        val queenDrawable = ResourcesCompat.getDrawable(
                            resources,
                            R.drawable.ic_chess_black_24dp,
                            null
                        )
                        val crossDrawable = ResourcesCompat.getDrawable(
                            resources,
                            R.drawable.ic_cross_black_24dp,
                            null
                        )
                        // Get description strings
                        val queenString: String = holder.itemView.resources.getString(R.string.fers)
                        val emptyString: String =
                            holder.itemView.resources.getString(R.string.empty)
                        // Get color
                        val userColor: Color =
                            resources.getColor(R.color.user_values, null).toColor()
                        val originalColor: Color =
                            resources.getColor(R.color.original_values, null).toColor()

                        // Set value
                        when (board.getStatus(row, column)) {
                            QueenCellStatus.EMPTY -> holder.setValuePicture(
                                null,
                                emptyString,
                            )

                            QueenCellStatus.ORIGINAL_QUEEN -> holder.setValuePicture(
                                queenDrawable,
                                queenString,
                                originalColor
                            )

                            QueenCellStatus.USER_QUEEN -> holder.setValuePicture(
                                queenDrawable,
                                queenString,
                                userColor
                            )

                            QueenCellStatus.CROSS -> holder.setValuePicture(
                                crossDrawable,
                                queenString,
                                userColor
                            )
                        }
                    }

                    Payload.LISTENER -> {
                        // Set listener
                        holder.itemView.setOnClickListener {
                            onItemClick(row, column)
                        }
                    }

                    Payload.HIGHLIGHT_RED -> holder.highlightRed()
                    Payload.REMOVE_HIGHLIGHT -> holder.removeHighlightColor()
                    Payload.HIGHLIGHT_GREEN -> holder.highlightGreen()
                }
            }
        } else {
            super.onBindViewHolder(holder, position, payloads)
        }
    }
}