package ru.hse.crowns.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import ru.hse.crowns.R
import ru.hse.crowns.domain.boards.QueensBoard
import ru.hse.crowns.databinding.BoardCellBinding

class QueensBoardRecyclerAdapter(
    private val onItemClick: (row: Int, column: Int) -> Unit
) : RecyclerView.Adapter<BoardCellViewHolder>() {

    private lateinit var board: QueensBoard

    /**
     * Allows not to bind all itemView but change only a part of it
     */
    private enum class Payload {
        VALUE,
        POLYOMINO,
        LISTENER
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setBoard(newBoard: QueensBoard) {
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

    override fun getItemCount(): Int = board.size * board.size

    override fun onBindViewHolder(holder: BoardCellViewHolder, position: Int) {
        val row = position / board.size
        val column = position % board.size
        val polyominoId = board.getPolyomino(row, column)

        // Get itemView resources
        val resources = holder.itemView.resources
        // Get drawable value
        val queenDrawable = ResourcesCompat.getDrawable(
            resources,
            R.drawable.ic_crown_black_24dp,
            null
        )
        // Get description strings
        val queenString: String = holder.itemView.resources.getString(R.string.queen)
        val emptyString: String = holder.itemView.resources.getString(R.string.empty)

        // Set value
        if (board.hasQueen(row, column)) {
            holder.setValuePicture(queenDrawable, queenString)
        } else {
            holder.setValuePicture(null, emptyString)
        }

        // Set polyomino color
        holder.setPolyominoColor(polyominoId)

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
            val polyominoId = board.getPolyomino(row, column)

            for (payload: Payload in payloads.mapNotNull { it as? Payload }) {
                when (payload) {
                    Payload.VALUE ->  {
                        // Get itemView resources
                        val resources = holder.itemView.resources
                        // Get drawable value
                        val queenDrawable = ResourcesCompat.getDrawable(
                            resources,
                            R.drawable.ic_crown_black_24dp,
                            null
                        )
                        // Get description strings
                        val queenString: String = holder.itemView.resources.getString(R.string.queen)
                        val emptyString: String = holder.itemView.resources.getString(R.string.empty)

                        // Set value
                        if (board.hasQueen(row, column)) {
                            holder.setValuePicture(queenDrawable, queenString)
                        } else {
                            holder.setValuePicture(null, emptyString)
                        }
                    }

                    Payload.POLYOMINO -> {
                        // Set polyomino color
                        holder.setPolyominoColor(polyominoId)
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