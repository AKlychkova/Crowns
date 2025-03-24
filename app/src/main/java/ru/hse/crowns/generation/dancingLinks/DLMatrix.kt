package ru.hse.crowns.generation.dancingLinks

/**
 * This class represents doubly linked matrix consisting of [nodes][Node]
 * as described in the D. E. Knuth's [article](https://doi.org/10.48550/arXiv.cs/0011047)
 * @param exactCoverMatrix 2D boolean array represented exact cover problem
 * @param primary how many first columns of the exact cover matrix are considered as primary.
 * The remaining ones are considered secondary.
 */
class DLMatrix(exactCoverMatrix: Array<BooleanArray>, primary: Int) {
    /**
     * Header node
     */
    var header: ColumnNode
        private set

    /**
     * List of all column nodes
     */
    private val columnNodes = ArrayList<ColumnNode>()

    /**
     * Number of columns in the initial exact cover matrix
     */
    val columnsNum: Int = if (exactCoverMatrix.isNotEmpty()) exactCoverMatrix[0].size else 0

    /**
     * @param exactCoverMatrix 2D boolean array represented exact cover problem without secondary columns
     */
    constructor(exactCoverMatrix: Array<BooleanArray>) : this(
        exactCoverMatrix,
        if (exactCoverMatrix.isNotEmpty()) exactCoverMatrix[0].size else 0
    )

    init {
        header = ColumnNode(-1)

        // Add columns
        var currentNode: Node = header

        if (exactCoverMatrix.isNotEmpty()) {

            for (i in exactCoverMatrix[0].indices) {
                val node = ColumnNode(i)
                columnNodes.add(node)
                if (i < primary) {
                    currentNode = currentNode.insertRight(node)
                }
            }

            // Add rows
            for (i in exactCoverMatrix.indices) {
                var previous: Node? = null
                for (j in exactCoverMatrix[i].indices) {
                    if (exactCoverMatrix[i][j]) {
                        val newNode = Node(columnNodes[j])
                        columnNodes[j].up.insertDown(newNode)
                        if (previous == null) {
                            previous = newNode
                        } else {
                            previous.insertRight(newNode)
                        }
                        columnNodes[j].size += 1
                    }
                }
            }
        }
    }

    /**
     * Cover column by its [index] in the initial exact cover matrix
     * @param index index of the column in the initial exact cover matrix
     */
    fun coverColumn(index: Int) {
        columnNodes[index].cover()
    }
}