package ru.hse.crowns.generation.dancingLinks

/**
 * Check if exact cover problem has no more than one solution
 */
abstract class DLUniqueChecker {
    /**
     * Predicate may contain additional conditions for verifying the solution or be null if there is no such conditions
     */
    protected abstract val solutionPredicate: ((Array<BooleanArray>) -> Boolean)?

    /**
     * @return dancing links matrix which must be checked
     */
    protected abstract fun createDLMatrix() : DLMatrix

    private lateinit var problem: DLMatrix

    /**
     * Amount of solutions have been found
     */
    private var solutionsNum: Int = 0

    /**
     * Current solution
     */
    private var solution: ArrayList<Node> = ArrayList()

    /**
     * Recursive procedure for finding solutions of exact cover problem
     * described in D. E. Knuth's [article](https://doi.org/10.48550/arXiv.cs/0011047)
     */
    private fun search(): Boolean {
        // Base case: exact cover has been found
        if (problem.header.right == problem.header) {
            // Check predicate if necessary
            if (solutionPredicate == null || solutionPredicate?.invoke(handleSolution()) == true) {
                // Increment solution number
                solutionsNum += 1
            }
            // Return false, if there is more than one solution, otherwise return true
            return solutionsNum <= 1
        }

        // Choose column
        val c: ColumnNode = chooseNextColumnNode()
        // Cover chosen column
        c.cover()
        // Go through rows
        var r: Node = c.down
        while (r != c) {
            // Try adding row to the solution
            solution.add(r)
            var j: Node = r.right
            while (j != r) {
                j.column?.cover()
                j = j.right
            }

            // Recursive call
            if (!search()) {
                return false
            }

            // Undo
            solution.removeAt(solution.size - 1)
            j = r.left
            while (j != r) {
                j.column?.uncover()
                j = j.left
            }

            r = r.down
        }
        c.uncover()
        return true
    }

    /**
     * @returns a column node with the smallest number of elements in its list
     */
    private fun chooseNextColumnNode(): ColumnNode {
        var selectedNode: ColumnNode = problem.header.right as ColumnNode
        var minSize = selectedNode.size

        var j: ColumnNode = selectedNode.right as ColumnNode
        while (j != problem.header) {
            if (j.size < minSize) {
                minSize = j.size
                selectedNode = j
            }
            j = j.right as ColumnNode
        }
        return selectedNode
    }

    /**
     * Convert current solution to 2D boolean array
     */
    private fun handleSolution(): Array<BooleanArray> {
        val resultMatrix = Array(solution.size, { BooleanArray(problem.columnsNum, { false }) })
        for ((i, node) in solution.withIndex()) {
            var j: Node = node
            do {
                resultMatrix[i][j.column!!.index] = true
                j = j.right
            } while (j != node)
        }
        return resultMatrix
    }

    /**
     * @return true if created by [createDLMatrix] method matrix has no more than one solution, otherwise false
     */
    protected fun check(): Boolean {
        problem = createDLMatrix()
        solutionsNum = 0
        solution = ArrayList()
        return search()
    }
}