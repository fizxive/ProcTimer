package com.imaginaryrhombus.proctimer

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.firebase.FirebaseApp
import com.imaginaryrhombus.proctimer.application.UpdateChecker
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UpdateCheckerTest {

    init {
        // JvmStatic, BeforeClass だとランタイムエラーを起こす.
        FirebaseApp.initializeApp(ApplicationProvider.getApplicationContext<Application>())
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
