package ru.hse.crowns.adapters

import android.annotation.SuppressLint
import android.content.res.TypedArray
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.graphics.toColor
import androidx.recyclerview.widget.RecyclerView
import ru.hse.crowns.R
import ru.hse.crowns.domain.boards.KillerSudokuBoard
import ru.hse.crowns.databinding.BoardCellBinding

class KillerSudokuBoardRecyclerAdapter(
    private val onItemClick: (row: Int, column: Int) -> Unit
) : RecyclerView.Adapter<BoardCellViewHolder>() {

    private lateinit var board: KillerSudokuBoard

    /**
     * Allows not to bind all itemView but change only a part of it
     */
    private enum class Payload {
        VALUE,
        POLYOMINO,
        ADDITIONAL_INFO,
        BORDERS,
        LISTENER,
        NOTES
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
        // Get itemView resources
        val resources = holder.itemView.resources

        // Get array of drawable numbers
        val numbersDrawables: TypedArray = resources.obtainTypedArray(R.array.numbers_drawable)

        val row = position / board.size
        val column = position % board.size
        val value: Int? = board.getValue(row, column)
        val polyominoId = board.getPolyomino(row, column)
        val emptyString: String = resources.getString(R.string.empty)

        // Set value
        holder.setValuePicture(
            if (value != null) {
                numbersDrawables.getDrawable(value - 1)
            } else {
                null
            },
            value?.toString() ?: emptyString
        )

        holder.clearNotes()
        for(note in board.getNotes(row, column)) {
            holder.setNote(note.toString(), note - 1)
        }

        // Set polyomino color
        holder.setPolyominoColor(polyominoId)

        // If cell has the smallest coordinates in its polyomino,
        // write polyomino total as additional information

        // Get smallest coordinates in the polyomino
        val minCoordinates = board.getPolyominoCoordinates(polyominoId)
            .minWithOrNull(compareBy({ it.first }, { it.second }))

        if (row == minCoordinates?.first && column == minCoordinates.second) {
            holder.setAdditionalInfo(board.getSum(polyominoId).toString())
        }

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
        numbersDrawables.recycle()

        holder.itemView.setOnClickListener {
            onItemClick(row, column)
        }
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
                                null
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
                }
            }
        } else {
            super.onBindViewHolder(holder, position, payloads)
        }
    }
}