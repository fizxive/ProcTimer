package com.imaginaryrhombus.proctimer

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.os.PersistableBundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.imaginaryrhombus.proctimer.application.TimerRemoteConfigClientInterface
import com.imaginaryrhombus.proctimer.application.TimerSharedPreferencesComponent
import com.imaginaryrhombus.proctimer.application.UpdateChecker
import com.imaginaryrhombus.proctimer.ui.TimerThemeConverter
import com.imaginaryrhombus.proctimer.ui.timer.TimerFragment
import kotlinx.android.synthetic.main.timer_activity.*
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject

class TimerActivity : AppCompatActivity(),
    UpdateChecker.UpdateRequiredListener,
    KoinComponent {

    private lateinit var drawerToggle: ActionBarDrawerToggle

    private val remoteConfigClient: TimerRemoteConfigClientInterface by inject()
    private val sharedPreferencesComponent: TimerSharedPreferencesComponent by inject()

    class RevertibleValue<T>(initValue:T) {

        var value = initValue
        set(value) {
            oldValue = field
            field = value
        }

        private var oldValue = initValue

        fun revertValue(): RevertibleValue<T> {
            value = oldValue
            return this
        }
    }

    private var currentTheme = RevertibleValue(R.style.Light)

    override fun onCreate(savedInstanceState: Bundle?) {
        currentTheme.value = TimerThemeConverter.toResourceId(sharedPreferencesComponent.timerTheme)
        setTheme(currentTheme.value)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.timer_activity)

        UpdateChecker(this).checkUpdateRequired()

        setSupportActionBar(toolbar)
        checkNotNull(supportActionBar).run {
            setDisplayHomeAsUpEnabled(true)
            setHomeButtonEnabled(true)
        }

        drawerToggle = object : ActionBarDrawerToggle(
            this@TimerActivity,
            drawer,
            toolbar,
            R.string.drawer_open,
            R.string.drawer_close
        ) {
            override fun onDrawerOpened(drawerView: View) {
                super.onDrawerOpened(drawerView)
                actionBar?.title = title
            }

            override fun onDrawerClosed(drawerView: View) {
                super.onDrawerClosed(drawerView)
                actionBar?.title = drawer.getDrawerTitle(GravityCompat.START)
            }
        }

        drawer.addDrawerListener(drawerToggle)
        drawerToggle.syncState()

        navigation.setNavigationItemSelectedListener { menuItem ->
            return@setNavigationItemSelectedListener when (menuItem.itemId) {
                R.id.menu_privacy -> {
                    openPrivacyPolicy()
                    true
                }
                R.id.change_theme -> {
                    openChangeThemeDialog()
                    true
                }
                else -> {
                    false
                }
            }
        }

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.frame, TimerFragment.newInstance())
                .commitNow()
        }
    }

    private fun openPrivacyPolicy() {
        val intent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse(
                remoteConfigClient.privacyPolicyUrl
            )
        )
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    private fun openChangeThemeDialog() {
        val selection = arrayOf(
            getString(R.string.theme_light),
            getString(R.string.theme_dark)
        )
        val themeMap = hashMapOf(
            getString(R.string.theme_light) to R.style.Light,
            getString(R.string.theme_dark) to R.style.Dark
        )
        val selectionMap = themeMap.entries.associateBy( {it.value} ) {it.key}
        val alertDialog = AlertDialog.Builder(this)
            .setTitle(getString(R.string.theme_dialog_title))
            .setSingleChoiceItems(selection,
                selection.indexOf(selectionMap.getValue(currentTheme.value))) {_, which ->
                currentTheme.value = requireNotNull(themeMap[selection[which]])
            }
            .setPositiveButton("OK") { _, _ ->
                val restartDialog: AlertDialog = AlertDialog.Builder(this)
                    .setMessage("変更の適用には表示の再読込が必要です。(動作中のタイマーは停止しません)")
                    .setPositiveButton("再読込") { _, _ ->
                        sharedPreferencesComponent.timerTheme =
                            TimerThemeConverter.fromResourceId(currentTheme.value)
                        setTheme(currentTheme.value)
                        recreate()
                    }
                    .setNegativeButton("キャンセル") { _, _ ->
                        currentTheme.revertValue()
                    }
                    .create()
                restartDialog.show()
            }
            .setNegativeButton("Cancel") { _, _ ->
                currentTheme.revertValue()
            }
            .create()
        alertDialog.show()
    }

    override fun onPostCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onPostCreate(savedInstanceState, persistentState)
        drawerToggle.syncState()
    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
        drawerToggle.onConfigurationChanged(newConfig)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (drawer.isDrawerOpen(navigation)) {
            drawer.closeDrawer(navigation)
        } else {
            super.onBackPressed()
        }
    }

    override fun onUpdateRequired(updateUrl: String) {

        val alertDialog = AlertDialog.Builder(this).apply {
            setTitle(applicationContext.getString(R.string.update_dialog_title))
            setMessage(applicationContext.getString(R.string.update_dialog_text))
            setPositiveButton("ストアに移動") { _: DialogInterface, _: Int ->
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(updateUrl))
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
        }.create()
        alertDialog.setCanceledOnTouchOutside(false)
        alertDialog.show()
    }
}
