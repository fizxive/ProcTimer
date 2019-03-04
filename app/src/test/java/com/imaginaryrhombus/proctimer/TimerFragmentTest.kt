package com.imaginaryrhombus.proctimer

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.imaginaryrhombus.proctimer.application.TimerSharedPreferencesComponent
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.standalone.get
import org.koin.test.AutoCloseKoinTest
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
class TimerFragmentTest : AutoCloseKoinTest() {

    @get:Rule
    val rule = ActivityScenarioRule(TimerActivity::class.java)

    @Before
    fun setUp() {
        get<TimerSharedPreferencesComponent>().reset()
    }

    /**
     * 縦向きの初期化テスト.
     */
    @Test
    @Config(qualifiers = "+port")
    fun testCreateActivityPortrait() {
        testCreateActivityInternal()
    }

    /**
     * 横向きの初期化テスト.
     */
    @Test
    @Config(qualifiers = "+land")
    fun testCreateActivityLandscape() {
        testCreateActivityInternal()
    }

    /**
     * 縦向きのタイマー追加テスト.
     */
    @Test
    @Config(qualifiers = "+port")
    fun testAddTimerPortrait() {
        testAddTimerInternal()
    }

    /**
     * 横向きのタイマー追加テスト.
     */
    @Test
    @Config(qualifiers = "+land")
    fun testAddTimerLandscape() {
        testAddTimerInternal()
    }

    /**
     * 縦向きのタイマー削除テスト.
     */
    @Test
    @Config(qualifiers = "+port")
    fun testRemoveTimerPortrait() {
        testRemoveTimerInternal()
    }

    /**
     * 横向きのタイマー削除テスト.
     */
    @Test
    @Config(qualifiers = "+land")
    fun testRemoveTimerLandscape() {
        testRemoveTimerInternal()
    }

    private fun testCreateActivityInternal() {
        onView(withId(R.id.currentTimerText)).check(matches(withText("01:00")))
        onView(withId(R.id.nextTimerText1)).check(matches(withText("01:00")))
        onView(withId(R.id.nextTimerText2)).check(matches(withText("--:--")))
        onView(withId(R.id.nextTimerText3)).check(matches(withText("--:--")))
    }

    private fun testAddTimerInternal() {
        onView(withId(R.id.addButton)).perform(click())
        onView(withId(R.id.currentTimerText)).check(matches(withText("01:00")))
        onView(withId(R.id.nextTimerText1)).check(matches(withText("01:00")))
        onView(withId(R.id.nextTimerText2)).check(matches(withText("01:00")))
        onView(withId(R.id.nextTimerText3)).check(matches(withText("--:--")))
    }

    private fun testRemoveTimerInternal() {
        onView(withId(R.id.removeButton)).perform(click())
        onView(withId(R.id.currentTimerText)).check(matches(withText("01:00")))
        onView(withId(R.id.nextTimerText1)).check(matches(withText("--:--")))
        onView(withId(R.id.nextTimerText2)).check(matches(withText("--:--")))
        onView(withId(R.id.nextTimerText3)).check(matches(withText("--:--")))
    }
}
