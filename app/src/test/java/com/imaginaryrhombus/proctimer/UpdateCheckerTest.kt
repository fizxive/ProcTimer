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

        val checkMethod = updateChecker.javaClass.getDeclaredMethod(
            "isUpdateRequired", String::class.java, String::class.java)

        assertNotNull(checkMethod)

        checkMethod.isAccessible = true

        fun isUpdateRequired(currentVersion: String, requiredVersion: String): Boolean {
            return checkMethod.invoke(updateChecker, currentVersion, requiredVersion) as Boolean
        }

        assertTrue(isUpdateRequired("0", "1"))
        assertFalse(isUpdateRequired("1", "0"))
        assertFalse(isUpdateRequired("1", "1"))

        assertTrue(isUpdateRequired("0.1", "1.0"))
        assertTrue(isUpdateRequired("0.1", "0.2"))
        assertTrue(isUpdateRequired("1.0", "2.0"))
        assertFalse(isUpdateRequired("1.1", "1.0"))
        assertFalse(isUpdateRequired("2.1", "2.1"))

        assertTrue(isUpdateRequired("0.0.1", "1.0.0"))
        assertTrue(isUpdateRequired("0.0.1", "0.1.0"))
        assertTrue(isUpdateRequired("0.0.0", "0.0.1"))
        assertTrue(isUpdateRequired("0.0.1", "0.0.2"))
        assertFalse(isUpdateRequired("1.1.0", "1.0.0"))
        assertFalse(isUpdateRequired("2.1.0", "2.1.0"))
        assertFalse(isUpdateRequired("1.0.1", "1.0.0"))
        assertFalse(isUpdateRequired("1.0.1", "1.0.1"))
    }
}
