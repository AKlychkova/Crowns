package ru.hse.crowns

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import ru.hse.crowns.ui.host.HostActivity

@RunWith(AndroidJUnit4::class)
class NavigationBarTest {
    @Rule
    @JvmField
    var activityRule = ActivityScenarioRule(HostActivity::class.java)

    @Test
    fun clickNQueensTab() {
        onView(withId(R.id.navigation_n_queens))
            .check(matches(isDisplayed()))

        onView(withId(R.id.navigation_n_queens))
            .perform(click())

        onView(withId(R.id.startNQueensButton)).check(matches(isDisplayed()))
    }

    @Test
    fun clickQueensTab() {
        onView(withId(R.id.navigation_queens))
            .check(matches(isDisplayed()))

        onView(withId(R.id.navigation_queens))
            .perform(click())

        onView(withId(R.id.startQueensButton)).check(matches(isDisplayed()))
    }

    @Test
    fun clickKillerSudokuTab() {
        onView(withId(R.id.navigation_killer_sudoku))
            .check(matches(isDisplayed()))

        onView(withId(R.id.navigation_killer_sudoku))
            .perform(click())

        onView(withId(R.id.startKillerSudokuButton)).check(matches(isDisplayed()))
    }
}