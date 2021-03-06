package com.imaginaryrhombus.proctimer.ui.timer

import android.app.AlertDialog
import android.media.RingtoneManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import com.imaginaryrhombus.proctimer.R
import com.imaginaryrhombus.proctimer.databinding.TimerFragmentBinding
import com.imaginaryrhombus.proctimer.ui.timerpicker.TimerPickerFragment
import kotlinx.android.synthetic.main.timer_fragment.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class TimerFragment : Fragment() {

    companion object {
        fun newInstance() = TimerFragment()
    }

    private val viewModel: TimerViewModel by sharedViewModel()
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

        requireActivity().run {
            val timerEndListener = object : TimerModel.OnEndedListener {
                override fun onEnd() {

                    // 規定のアラーム音を鳴らす、ダイアログを閉じると止まる.
                    val ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                    val ringtone = RingtoneManager.getRingtone(this@run, ringtoneUri)
                    ringtone.play()

                    val alertBuilder = AlertDialog.Builder(this@run)
                        .setTitle(R.string.timer_end_dialog_text)
                        .setPositiveButton(R.string.button_timer_end_dialog_next_button) { _, _ ->
                            ringtone.stop()
                            viewModel.nextTimer()
                        }
                        .setNeutralButton(
                            R.string.button_timer_end_dialog_next_start_button
                        ) { _, _ ->
                            ringtone.stop()
                            viewModel.nextTimer()
                            viewModel.startTick()
                        }

                    val dialog = alertBuilder.create()
                    dialog.setCanceledOnTouchOutside(false)
                    dialog.show()
                }
            }

            viewModel.setTimerEndListener(timerEndListener)
        }

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
                Snackbar.make(
                    requireView(),
                    R.string.cannot_add_more_timer,
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }

        removeButton.setOnClickListener {
            viewModel.removeTimer {
                Snackbar.make(
                    requireView(),
                    R.string.cannot_delete_last_timer,
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }

        nextButton.setOnClickListener {
            viewModel.nextTimer()
        }

        editButton.setOnClickListener {
            val pickerFragment = TimerPickerFragment()
            pickerFragment.show(parentFragmentManager, "PickerDialog")
        }

        viewModel.isTimerWorking.observe(
            viewLifecycleOwner,
            Observer {
                setKeepScreenOn(it)
            }
        )
    }

    /**
     * 画面の常時点灯を設定する.
     */
    private fun setKeepScreenOn(enabled: Boolean) {
        val window = requireActivity().window
        if (enabled) {
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        } else {
            window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }
}
