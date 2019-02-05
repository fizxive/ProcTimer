package com.imaginaryrhombus.proctimer

import androidx.test.core.app.ApplicationProvider
import com.google.firebase.FirebaseApp
import com.imaginaryrhombus.proctimer.application.UpdateChecker
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class UpdateCheckerTest {

    init {
        FirebaseApp.initializeApp(ApplicationProvider.getApplicationContext())
    }

    /**
     * バージョンの比較が正しいかどうかテストする.
     */
    @Test
    fun testVersionComparison() {
        val updateListener = object : UpdateChecker.UpdateRequiredListener {
            override fun onUpdateRequired(updateUrl: String) {
                // empty.(UpdateChecker のインスタンスのために必要.)
            }
        }
        val updateChecker = UpdateChecker(updateListener)

        val checkMethod = updateChecker.javaClass.getDeclaredMethod("isUpdateRequired", String::class.java, String::class.java)

        assertNotNull(checkMethod)

        checkMethod.isAccessible = true

        assertTrue(checkMethod.invoke(updateChecker, "0", "1") as Boolean)
        assertFalse(checkMethod.invoke(updateChecker, "1", "0") as Boolean)
        assertFalse(checkMethod.invoke(updateChecker, "1", "1") as Boolean)
    }

}
