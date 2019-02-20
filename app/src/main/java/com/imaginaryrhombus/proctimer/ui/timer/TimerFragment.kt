package com.imaginaryrhombus.proctimer.ui.timer

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.res.Resources
import android.media.RingtoneManager
import android.net.Uri
import androidx.lifecycle.ViewModelProviders
import androidx.databinding.DataBindingUtil
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.imaginaryrhombus.proctimer.R
import com.imaginaryrhombus.proctimer.databinding.TimerFragmentBinding
import com.imaginaryrhombus.proctimer.ui.timerpicker.TimerPickerFragment
import kotlinx.android.synthetic.main.timer_fragment.*

class TimerFragment : Fragment() {

    companion object {
        fun newInstance() = TimerFragment()
    }

    private lateinit var viewModel: TimerViewModel
    private lateinit var binding: TimerFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.timer_fragment, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // viewModel 初期化, ダイアログの出現の設定.

        activity?.run {
            viewModel = ViewModelProviders.of(this).get(TimerViewModel::class.java)

            val timerEndListener = object : TimerModel.OnEndedListener {
                override fun onEnd() {

                    // 規定のアラーム音を鳴らす、ダイアログを閉じると止まる.
                    val ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                    val ringtone = RingtoneManager.getRingtone(this@run, ringtoneUri)
                    ringtone.play()

                    val alertBuilder = AlertDialog.Builder(this@run).run {
                        setTitle(R.string.timer_end_dialog_text)
                        setPositiveButton(R.string.timer_end_dialog_button) {
                                _: DialogInterface, _: Int ->
                            ringtone.stop()
                            viewModel.nextTimer()
                        }
                    }
                    val dialog = alertBuilder.create()
                    dialog.setCanceledOnTouchOutside(false)
                    dialog.show()
                }
            }

            viewModel.setTimerEndListener(timerEndListener)
        } ?: throw Resources.NotFoundException("Activity Not found.")

        binding.timerViewModel = viewModel
        binding.lifecycleOwner = this

        // 各種リスナー設定.

        startButton.setOnClickListener {
            viewModel.startTick()
        }

        stopButton.setOnClickListener {
            viewModel.stopTick()
        }

        resetButton.setOnClickListener {
            viewModel.resetTimer()
        }

        addButton.setOnClickListener {
            viewModel.addTimer {
                Toast.makeText(context,
                    getString(R.string.cannot_add_more_timer), Toast.LENGTH_SHORT)
                    .show()
            }
        }

        removeButton.setOnClickListener {
            viewModel.removeTimer {
                Toast.makeText(context,
                    getString(R.string.cannot_delete_last_timer), Toast.LENGTH_SHORT)
                    .show()
            }
        }

        nextButton.setOnClickListener {
            viewModel.nextTimer()
        }

        currentTimerText.setOnClickListener {
            val pickerFragment = TimerPickerFragment()
            pickerFragment.show(fragmentManager, "PickerDialog")
        }

        privacy.setOnClickListener {
            // TODO : Remote Config に移動する.
            // TODO : ナビゲーションドロワーを使用して配置したい.
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(
                "https://pagehosting-d362c.firebaseapp.com/proctimer/privacy-poricy.html"))
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }
}
