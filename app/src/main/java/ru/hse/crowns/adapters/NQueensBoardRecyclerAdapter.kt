package ru.hse.crowns.adapters

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.toColor
import androidx.recyclerview.widget.RecyclerView
import ru.hse.crowns.R
import ru.hse.crowns.domain.boards.NQueensBoard
import ru.hse.crowns.databinding.BoardCellBinding
import ru.hse.crowns.domain.boards.QueenCellStatus

class NQueensBoardRecyclerAdapter(
    private val onItemClick: (row: Int, column: Int) -> Unit
) :
    RecyclerView.Adapter<BoardCellViewHolder>() {

    private lateinit var board: NQueensBoard

    /**
     * Allows not to bind all itemView but change only a part of it
     */
    private enum class Payload {
        VALUE,
        LISTENER
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setBoard(newBoard: NQueensBoard) {
        board = newBoard
        notifyDataSetChanged()
    }

    /**
     * Update cell with coordinates ([row], [column])
     */
    fun updateCellValue(row: Int, column: Int) {
        notifyItemChanged(row * board.size + column, Payload.VALUE)
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

        val row = position / board.size
        val column = position % board.size

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
        val emptyString: String = holder.itemView.resources.getString(R.string.empty)

        val userColor: Color =
            resources.getColor(R.color.user_values, null).toColor()

        // Set value
        when (board.getStatus(row, column)) {
            QueenCellStatus.EMPTY -> holder.setValuePicture(null, emptyString)
            QueenCellStatus.ORIGINAL_QUEEN -> holder.setValuePicture(
                queenDrawable,
                queenString
            )

            QueenCellStatus.USER_QUEEN -> holder.setValuePicture(
                queenDrawable,
                queenString,
                userColor
            )

            QueenCellStatus.CROSS -> holder.setValuePicture(crossDrawable, queenString, userColor)
        }

        // Set listener
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

                        // Set value
                        when (board.getStatus(row, column)) {
                            QueenCellStatus.EMPTY -> holder.setValuePicture(
                                null,
                                emptyString,
                            )

                            QueenCellStatus.ORIGINAL_QUEEN -> holder.setValuePicture(
                                queenDrawable,
                                queenString
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
                }
            }
        } else {
            super.onBindViewHolder(holder, position, payloads)
        }
    }
}