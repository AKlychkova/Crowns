package ru.hse.crowns.adapters

import android.annotation.SuppressLint
import android.content.res.TypedArray
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.graphics.toColor
import androidx.recyclerview.widget.RecyclerView
import ru.hse.crowns.R
import ru.hse.crowns.domain.domainObjects.boards.KillerSudokuBoard
import ru.hse.crowns.databinding.BoardCellBinding

class KillerSudokuBoardRecyclerAdapter(
    private val onItemClick: (row: Int, column: Int) -> Unit
) : RecyclerView.Adapter<BoardCellViewHolder>() {

    private lateinit var board: KillerSudokuBoard
    private var currentRed = emptySet<Int>()
    private var currentGreen = emptySet<Int>()

    /**
     * Allows not to bind all itemView but change only a part of it
     */
    private enum class Payload {
        VALUE,
        POLYOMINO,
        ADDITIONAL_INFO,
        BORDERS,
        LISTENER,
        NOTES,
        HIGHLIGHT_RED,
        REMOVE_HIGHLIGHT,
        HIGHLIGHT_GREEN
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setBoard(newBoard: KillerSudokuBoard) {
        board = newBoard
        notifyDataSetChanged()
    }

    /**
     * Update cell with coordinates ([row], [column])
     */
    fun updateCellValue(row: Int, column: Int) {
        notifyItemChanged(row * board.size + column, Payload.VALUE)
        notifyItemChanged(row * board.size + column, Payload.NOTES)
    }

    fun updateHighlights(greenPositions: Iterable<Pair<Int, Int>> = emptyList(),
                         redPositions: Iterable<Pair<Int, Int>> = emptyList()) {
        val green = greenPositions.map { it.first * board.size + it.second}.toSet()
        for (position in (green - currentGreen)) {
            notifyItemChanged(position, Payload.HIGHLIGHT_GREEN)
        }
        for (position in (currentGreen - green)) {
            notifyItemChanged(position, Payload.REMOVE_HIGHLIGHT)
        }
        currentGreen = green

        val red = redPositions.map { it.first * board.size + it.second}.toSet()
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
        onBindViewHolder(holder, position, mutableListOf(
            Payload.VALUE,
            Payload.POLYOMINO,
            Payload.ADDITIONAL_INFO,
            Payload.BORDERS,
            Payload.LISTENER,
            Payload.NOTES,
            if(position in currentRed) Payload.HIGHLIGHT_RED else Payload.REMOVE_HIGHLIGHT,
        if(position in currentGreen) Payload.HIGHLIGHT_GREEN else Payload.REMOVE_HIGHLIGHT))
    }

    override fun onBindViewHolder(
        holder: BoardCellViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isNotEmpty()) {
            val row = position / board.size
            val column = position % board.size
            val polyominoId = board.getPolyomino(row, column)

            for (payload: Payload in payloads.mapNotNull { it as? Payload }) {
                when (payload) {
                    Payload.VALUE -> {
                        // Get itemView resources
                        val resources = holder.itemView.resources

                        // Get array of drawable numbers
                        val numbersDrawables: TypedArray =
                            resources.obtainTypedArray(R.array.numbers_drawable)
                        // Get value or null if cell is empty
                        val value: Int? = board.getValue(row, column)
                        // Get description strings
                        val emptyString: String = resources.getString(R.string.empty)
                        // Get color
                        val userColor: Color =
                            resources.getColor(R.color.user_values, null).toColor()
                        val originalColor: Color =
                            resources.getColor(R.color.original_values, null).toColor()

                        // Set value
                        holder.setValuePicture(
                            if (value != null) {
                                numbersDrawables.getDrawable(value - 1)
                            } else {
                                null
                            },
                            value?.toString() ?: emptyString,
                            if(!board.isOriginal(row, column)) {
                                userColor
                            } else {
                                originalColor
                            }
                        )

                        numbersDrawables.recycle()
                    }

                    Payload.POLYOMINO -> {
                        holder.setPolyominoColor(polyominoId)
                    }

                    Payload.ADDITIONAL_INFO ->  {
                        // If cell has the smallest coordinates in its polyomino,
                        // write polyomino total as additional information

                        // Get smallest coordinates in the polyomino
                        val minCoordinates = board.getPolyominoCoordinates(polyominoId)
                            .minWithOrNull(compareBy({ it.first }, { it.second }))

                        if (row == minCoordinates?.first && column == minCoordinates.second) {
                            holder.setAdditionalInfo(board.getSum(polyominoId).toString())
                        }
                    }

                    Payload.BORDERS -> {
                        if (row % board.boxSize == 0) {
                            holder.makeTopBorderBold()
                        }
                        if (row % board.boxSize == board.boxSize - 1) {
                            holder.makeBottomBorderBold()
                        }
                        if (column % board.boxSize == 0) {
                            holder.makeLeftBorderBold()
                        }
                        if (column % board.boxSize == board.boxSize - 1) {
                            holder.makeRightBorderBold()
                        }
                    }

                    Payload.LISTENER -> {
                        holder.itemView.setOnClickListener {
                            onItemClick(row, column)
                        }
                    }

                    Payload.NOTES -> {
                        holder.clearNotes()
                        for(note in board.getNotes(row, column)) {
                            holder.setNote(note.toString(), note - 1)
                        }
                    }

                    Payload.HIGHLIGHT_RED -> holder.highlightRed()
                    Payload.REMOVE_HIGHLIGHT -> holder.removeHighlightColor()
                    Payload.HIGHLIGHT_GREEN -> holder.highLightGreen()
                }
            }
        } else {
            super.onBindViewHolder(holder, position, payloads)
        }
    }
}