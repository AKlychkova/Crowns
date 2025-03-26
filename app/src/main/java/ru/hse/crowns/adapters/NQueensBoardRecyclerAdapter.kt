package ru.hse.crowns.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.hse.crowns.R
import ru.hse.crowns.boards.NQueensBoard
import ru.hse.crowns.databinding.BoardCellBinding

class NQueensBoardRecyclerAdapter(private var board: NQueensBoard) :
    RecyclerView.Adapter<BoardCellViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BoardCellViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = BoardCellBinding.inflate(layoutInflater, parent, false)
        return BoardCellViewHolder(binding)
    }

    override fun getItemCount(): Int = board.size * board.size

    override fun onBindViewHolder(holder: BoardCellViewHolder, position: Int) {
        val row = position / board.size
        val column = position % board.size
        val queenDrawable =
            holder.itemView.resources.getDrawable(R.drawable.ic_chess_black_24dp, null)
        val queenString: String = holder.itemView.resources.getString(R.string.fers)
        val emptyString: String = holder.itemView.resources.getString(R.string.empty)

        // Set value
        if (board.hasQueen(row, column)) {
            holder.setValuePicture(queenDrawable, queenString)
        } else {
            holder.setValuePicture(null, emptyString)
        }
    }
}