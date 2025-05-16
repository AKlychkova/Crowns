package ru.hse.crowns

import org.junit.Assert.*
import org.junit.Test
import ru.hse.crowns.domain.generation.dancingLinks.DLMatrix
import ru.hse.crowns.domain.generation.dancingLinks.DLUniqueChecker


class DLUniqueCheckerTest {

    @Test
    fun testSmallUniqueWithoutSecondary() {
        val test = object : DLUniqueChecker() {
            override val solutionPredicate: ((Array<BooleanArray>) -> Boolean)? = null

            override fun createDLMatrix(): DLMatrix {
                return DLMatrix(
                    arrayOf(
                        booleanArrayOf(false, true, true, false),
                        booleanArrayOf(false, false, false, true),
                        booleanArrayOf(true, false, false, false),
                        booleanArrayOf(true, true, false, false)
                    )
                )
            }

            fun test() {
                assertTrue(check())
            }
        }
        test.test()
    }

    @Test
    fun testSmallNonUniqueWithoutSecondary() {
        val test = object : DLUniqueChecker() {
            override val solutionPredicate: ((Array<BooleanArray>) -> Boolean)? = null

            override fun createDLMatrix(): DLMatrix {
                return DLMatrix(
                    arrayOf(
                        booleanArrayOf(false, true, true, false),
                        booleanArrayOf(true, false, false, true),
                        booleanArrayOf(false, false, false, true),
                        booleanArrayOf(true, false, false, false)
                    )
                )
            }

            fun test() {
                assertFalse(check())
            }
        }
        test.test()
    }

    @Test
    fun testUniqueWithoutSecondary() {
        val test = object : DLUniqueChecker() {
            override val solutionPredicate: ((Array<BooleanArray>) -> Boolean)? = null

            override fun createDLMatrix(): DLMatrix {
                return DLMatrix(
                    arrayOf(
                        booleanArrayOf(false, false, true, false, true, true, false),
                        booleanArrayOf(true, false, false, true, false, false, true),
                        booleanArrayOf(false, true, true, false, false, true, false),
                        booleanArrayOf(true, false, false, true, false, false, false),
                        booleanArrayOf(false, true, false, false, false, false, true),
                        booleanArrayOf(false, false, false, true, true, false, true)
                    )
                )
            }

            fun test() {
                assertTrue(check())
            }
        }
        test.test()
    }

    @Test
    fun testNonUniqueWithoutSecondary() {
        val test = object : DLUniqueChecker() {
            override val solutionPredicate: ((Array<BooleanArray>) -> Boolean)? = null

            override fun createDLMatrix(): DLMatrix {
                return DLMatrix(
                    arrayOf(
                        booleanArrayOf(false, false, true, false, true, true, false),
                        booleanArrayOf(true, false, false, true, false, false, true),
                        booleanArrayOf(false, true, true, false, false, true, false),
                        booleanArrayOf(true, false, false, true, false, false, false),
                        booleanArrayOf(false, true, false, false, false, false, true),
                        booleanArrayOf(false, false, false, true, true, false, true),
                        booleanArrayOf(false, false, true, false, false, false, false),
                        booleanArrayOf(false, false, false, false, true, true, false),
                    )
                )
            }

            fun test() {
                assertFalse(check())
            }
        }
        test.test()
    }

    @Test
    fun testNonUniqueWithSecondary() {
        val test = object : DLUniqueChecker() {
            override val solutionPredicate: ((Array<BooleanArray>) -> Boolean)? = null

            override fun createDLMatrix(): DLMatrix {
                return DLMatrix(
                    exactCoverMatrix = arrayOf(
                        booleanArrayOf(false, false, true, false, true, true, false),
                        booleanArrayOf(true, false, false, true, false, false, true),
                        booleanArrayOf(false, true, true, false, false, true, false),
                        booleanArrayOf(true, false, false, true, false, false, false),
                        booleanArrayOf(false, true, false, false, false, false, true),
                        booleanArrayOf(false, false, false, true, true, false, true)
                    ),
                    primary = 4
                )
            }

            fun test() {
                assertFalse(check())
            }
        }
        test.test()
    }

    @Test
    fun testUniqueWithSecondary() {
        val test = object : DLUniqueChecker() {
            override val solutionPredicate: ((Array<BooleanArray>) -> Boolean)? = null

            override fun createDLMatrix(): DLMatrix {
                return DLMatrix(
                    exactCoverMatrix = arrayOf(
                        booleanArrayOf(false, false, true, false, true, true, false),
                        booleanArrayOf(true, false, false, true, false, false, true),
                        booleanArrayOf(false, true, true, false, false, true, false),
                        booleanArrayOf(true, false, false, true, false, false, false),
                        booleanArrayOf(false, true, false, false, false, false, true),
                        booleanArrayOf(false, false, false, true, true, false, true)
                    ),
                    primary = 5
                )
            }

            fun test() {
                assertTrue(check())
            }
        }
        test.test()
    }
}