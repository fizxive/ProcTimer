package com.imaginaryrhombus.proctimer

import android.app.AlertDialog
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.os.PersistableBundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.GravityCompat
import com.google.android.gms.common.wrappers.InstantApps
import com.imaginaryrhombus.proctimer.application.TimerRemoteConfigClientInterface
import com.imaginaryrhombus.proctimer.application.TimerSharedPreferencesComponent
import com.imaginaryrhombus.proctimer.application.UpdateChecker
import com.imaginaryrhombus.proctimer.ui.timer.TimerFragment
import kotlinx.android.synthetic.main.timer_activity.*
import kotlinx.android.synthetic.main.timer_fragment.*
import org.koin.core.KoinComponent
import org.koin.core.inject

class TimerActivity : AppCompatActivity(),
    UpdateChecker.UpdateRequiredListener,
    KoinComponent {

    private lateinit var drawerToggle: ActionBarDrawerToggle

    private val remoteConfigClient: TimerRemoteConfigClientInterface by inject()
    private val sharedPreferencesComponent: TimerSharedPreferencesComponent by inject()

    private var currentMode = AppCompatDelegate.MODE_NIGHT_UNSPECIFIED

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        currentMode = sharedPreferencesComponent.timerTheme
        AppCompatDelegate.setDefaultNightMode(currentMode)

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
            drawer.closeDrawer(navigation)
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
        if (InstantApps.isInstantApp(applicationContext)) {
            Toast.makeText(
                this, getString(R.string.instant_apps_privacy_toast), Toast.LENGTH_SHORT
            )
                .show()
        } else {
            val privacyPolicyUrl = remoteConfigClient.privacyPolicyUrl
            if (privacyPolicyUrl.isNotEmpty()) {
                val intent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(
                        privacyPolicyUrl
                    )
                )
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            } else {
                Toast.makeText(
                    this, getString(R.string.privacy_policy_offline),
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
        }
    }

    private fun openChangeThemeDialog() {
        data class DarkSetting (
            val text: String,
            val mode: Int
        )
        val modes = listOf(
            DarkSetting(getString(R.string.theme_light), AppCompatDelegate.MODE_NIGHT_NO),
            DarkSetting(getString(R.string.theme_dark), AppCompatDelegate.MODE_NIGHT_YES),
            DarkSetting(getString(R.string.theme_device), AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM),
            DarkSetting(getString(R.string.theme_battery), AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY),
        )
        val selections = modes.map { it.text }.toTypedArray()
        var mode = currentMode
        val currentItem = modes.indexOfFirst { it.mode == currentMode }
        val alertDialog = AlertDialog.Builder(this)
            .setTitle(getString(R.string.theme_dialog_title))
            .setSingleChoiceItems(selections, currentItem) { _, which ->
                mode = modes[which].mode
            }
            .setPositiveButton(getString(R.string.button_theme_change)) { _, _ ->
                val restartDialog: AlertDialog = AlertDialog.Builder(this)
                    .setMessage(getString(R.string.reload_activity_message))
                    .setPositiveButton(getString(R.string.button_reload)) { _, _ ->
                        AppCompatDelegate.setDefaultNightMode(mode)
                        currentMode = mode
                        sharedPreferencesComponent.timerTheme = mode
                    }
                    .setNegativeButton(getString(R.string.button_cancel)) { _, _ ->

                    }
                    .create()
                restartDialog.show()
            }
            .setNegativeButton(getString(R.string.button_cancel)) { _, _ ->

            }
            .create()
        alertDialog.show()
    }

    override fun onPostCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onPostCreate(savedInstanceState, persistentState)
        drawerToggle.syncState()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        drawerToggle.onConfigurationChanged(newConfig)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
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
            setTitle(getString(R.string.update_dialog_title))
            setMessage(getString(R.string.update_dialog_text))
            setPositiveButton(getString(R.string.button_move_to_store)) { _, _ ->
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(updateUrl))
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
        }.create()
        alertDialog.setCanceledOnTouchOutside(false)
        alertDialog.show()
    }
}
