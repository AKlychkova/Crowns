package ru.hse.crowns.domain.generation.dancingLinks

/**
 * This class represents a node of double linked matrix
 * as described in the D. E. Knuth's [article](https://doi.org/10.48550/arXiv.cs/0011047)
 */
open class Node() {
    var left: Node = this
    var right: Node = this
    var up: Node = this
    var down: Node = this
    var column: ColumnNode? = null
        protected set

    /**
     * @param column the head of the relevant column
     */
    constructor(column: ColumnNode) : this() {
        this.column = column
    }

    /**
     * Insert another [node][other] to the right of this one
     * @param other node that must be inserted
     */
    fun insertRight(other: Node): Node {
        other.right = this.right
        other.right.left = other
        other.left = this
        this.right = other
        return other
    }

    /**
     * Insert another [node][other]  from the bottom of this one
     * @param other node that must be inserted
     */
    fun insertDown(other: Node): Node {
        if (this.column != other.column) {
            throw IllegalArgumentException("Cannot insert node from another column")
        }
        other.down = this.down
        other.down.up = other
        other.up = this
        this.down = other
        return other
    }

    /**
     * Remove this node from the horizontal list
     */
    fun unlinkLR() {
        this.right.left = this.left
        this.left.right = this.right
    }

    /**
     * Restore this node in the horizontal list
     */
    fun relinkLR() {
        this.right.left = this
        this.left.right = this
    }

    /**
     * Remove this node from vertical list
     */
    fun unlinkUD() {
        this.up.down = this.down
        this.down.up = this.up
    }

    /**
     * Restore this node in the vertical list
     */
    fun relinkUD() {
        this.up.down = this
        this.down.up = this
    }
}