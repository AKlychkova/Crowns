package ru.hse.crowns.adapters

import android.content.res.TypedArray
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.hse.crowns.R
import ru.hse.crowns.boards.KillerSudokuBoard
import ru.hse.crowns.databinding.BoardCellBinding

class KillerSudokuBoardRecyclerAdapter(private var board: KillerSudokuBoard) :
    RecyclerView.Adapter<BoardCellViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BoardCellViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = BoardCellBinding.inflate(layoutInflater, parent, false)
        return BoardCellViewHolder(binding)
    }

    override fun getItemCount(): Int = board.size * board.size

    override fun onBindViewHolder(holder: BoardCellViewHolder, position: Int) {
        // Get array of drawable numbers
        val numbersDrawables: TypedArray =
            holder.itemView.resources.obtainTypedArray(R.array.numbers_drawable)

        val row = position / board.size
        val column = position % board.size
        val value: Int? = board.getValue(row, column)
        val polyominoId = board.getPolyomino(row, column)

        // Set value
        holder.setValuePicture(
            if (value != null) {
                numbersDrawables.getDrawable(value - 1)
            } else {
                null
            }
        )

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
        if(column % board.boxSize == 0) {
            holder.makeLeftBorderBold()
        }
        if(column % board.boxSize == board.boxSize - 1) {
            holder.makeRightBorderBold()
        }
        numbersDrawables.recycle()
    }
}