package com.imaginaryrhombus.proctimer

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.imaginaryrhombus.proctimer.application.UpdateChecker
import com.imaginaryrhombus.proctimer.ui.timer.TimerFragment

class TimerActivity : AppCompatActivity(),
    UpdateChecker.UpdateRequiredListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.timer_activity)

        UpdateChecker(this).checkUpdateRequired()

        FirebaseDynamicLinks.getInstance().getDynamicLink(intent)
            .addOnSuccessListener { pendingDynamicLinkData ->
                var deepLink: Uri? = null
                if (pendingDynamicLinkData != null) {
                    deepLink = pendingDynamicLinkData.link
                }
                Toast.makeText(applicationContext,
                    "リンクの処理に成功しました.($deepLink)", Toast.LENGTH_SHORT)
                    .show()
            }
            .addOnFailureListener {
                Toast.makeText(applicationContext,
                    "リンクの処理に失敗しました.", Toast.LENGTH_SHORT)
                    .show()
            }

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, TimerFragment.newInstance())
                .commitNow()
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
