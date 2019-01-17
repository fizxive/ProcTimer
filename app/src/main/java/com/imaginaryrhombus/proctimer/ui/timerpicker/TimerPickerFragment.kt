package com.imaginaryrhombus.proctimer.ui.timerpicker

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.res.Resources
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.text.InputType
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import com.imaginaryrhombus.proctimer.R
import com.imaginaryrhombus.proctimer.ui.timer.TimerViewModel
import kotlinx.android.synthetic.main.timer_picker_fragment.view.*

class TimerPickerFragment : DialogFragment() {

    companion object {
        fun newInstance() = TimerPickerFragment()
    }
    private lateinit var timerViewModel: TimerViewModel

    /**
     * ダイアログを閉じるときに設定するようの値,分.
     */
    private lateinit var minutesString: String
    /**
     * ダイアログを閉じるときに設定するようの値,秒.
     */
    private lateinit var secondsString: String

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreateDialog(savedInstanceState)

        activity?.run {
            timerViewModel = ViewModelProviders.of(this).get(TimerViewModel::class.java)
        } ?: throw Resources.NotFoundException("Activity Not found.")

        timerViewModel.toTimerString().run {
            minutesString = first
            secondsString = second
        }

        val setTimerOnDialogClick = { _: DialogInterface, _: Int ->
            timerViewModel.setTimerFrom(minutesString, secondsString)
        }

        val doNothingOnDialogClick = { _: DialogInterface, _: Int ->
        }

        val builder = AlertDialog.Builder(activity).run {
            setMessage(R.string.timer_dialog_text)
            setPositiveButton(R.string.timer_dialog_positive_button, setTimerOnDialogClick)
            setNegativeButton(R.string.timer_dialog_negative_button, doNothingOnDialogClick)
            this
        }

        activity?.let { activity ->
            val inflater = activity.layoutInflater
            val view = inflater.inflate(R.layout.timer_picker_fragment, null).apply {

                val values = Array(1000) { value -> value.toString() }

                fun findEditTextAndSetInputType(viewGroup: ViewGroup) {
                    for (i in 0 until viewGroup.childCount) {
                        val child = viewGroup.getChildAt(i)
                        when (child) {
                            is ViewGroup -> findEditTextAndSetInputType(child)
                            is EditText -> child.inputType = InputType.TYPE_CLASS_NUMBER
                        }
                    }
                }

                minutesPicker.apply {
                    displayedValues = values
                    minValue = 0
                    maxValue = values.size - 1
                    findEditTextAndSetInputType(this)
                    setOnValueChangedListener { _, _, newVal ->
                        minutesString = minutesPicker.displayedValues[newVal]
                    }
                    value = displayedValues.run {
                        indexOf(find { it == minutesString } ?: first())
                    }
                }

                secondsPicker.apply {
                    displayedValues = values.copyOfRange(0, 60)
                    minValue = 0
                    maxValue = 59
                    findEditTextAndSetInputType(this)
                    setOnValueChangedListener { _, _, newVal ->
                        secondsString = secondsPicker.displayedValues[newVal]
                    }
                    value = displayedValues.run {
                        indexOf(find { it == secondsString } ?: first())
                    }
                }
            }

            builder.setView(view)
        }

        val dialog = builder.create()
        dialog.setCanceledOnTouchOutside(false)

        return dialog
    }
}
