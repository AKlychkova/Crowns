package ru.hse.crowns.generation.dancingLinks

/**
 * This class represents a column node
 * as described in the D. E. Knuth's [article](https://doi.org/10.48550/arXiv.cs/0011047)
 * @param index identifier of the column
 */
class ColumnNode(val index: Int) : Node() {
    init {
        column = this
    }

    /**
     * Amount of elements in this column node's list
     */
    var size: Int = 0

    /**
     * Removes this column node from the header list and
     * removes all rows in this column node's own list from the other column lists they are in.
     */
    fun cover() {
        unlinkLR()
        var i: Node = this.down
        while (i != this) {
            var j: Node = i.right
            while (j != i) {
                j.unlinkUD()
                j.column!!.size -= 1
                j = j.right
            }
            i = i.down
        }
    }

    /**
     * Cancels the coverage of this column
     */
    fun uncover() {
        var i = this.up
        while (i != this) {
            var j = i.left
            while (j != i) {
                j.column!!.size += 1
                j.relinkUD()
                j = j.left
            }
            i = i.up
        }
        relinkLR()
    }
}