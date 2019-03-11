package com.imaginaryrhombus.proctimer.ui.timerpicker

import androidx.appcompat.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.text.InputType
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AppCompatDialogFragment
import com.imaginaryrhombus.proctimer.R
import com.imaginaryrhombus.proctimer.ui.timer.TimerViewModel
import kotlinx.android.synthetic.main.timer_picker_fragment.view.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class TimerPickerFragment : AppCompatDialogFragment() {

    private val timerViewModel: TimerViewModel by sharedViewModel()

    override fun onCreateDialog(savedInstanceState: Bundle?): AlertDialog {
        super.onCreateDialog(savedInstanceState)

        val fragmentActivity = checkNotNull(activity) { "Activity Not found." }

        val initMinutesString: String
        val initSecondsString: String

        timerViewModel.toTimerString().run {
            initMinutesString = first
            initSecondsString = second
        }

        val dialogView = requireNotNull(
            fragmentActivity.layoutInflater.inflate(
                R.layout.timer_picker_fragment, null
            )
        )

        val builder = AlertDialog.Builder(requireActivity())
            .setView(dialogView)
            .setMessage(R.string.timer_dialog_text)
            .setPositiveButton(R.string.timer_dialog_positive_button) {
                _: DialogInterface, _: Int ->
                dialogView.run {
                    timerViewModel.setCurrentTimerFrom(
                        minutesPicker.displayedValues[minutesPicker.value],
                        secondsPicker.displayedValues[secondsPicker.value]
                    )
                }
            }
            .setNegativeButton(R.string.timer_dialog_negative_button) {
                _: DialogInterface, _: Int ->
            }

        dialogView.run {
            fun findEditTextAndSetInputType(viewGroup: ViewGroup) {
                for (i in 0 until viewGroup.childCount) {
                    val child = viewGroup.getChildAt(i)
                    when (child) {
                        is ViewGroup -> findEditTextAndSetInputType(child)
                        is EditText -> {
                            child.inputType = InputType.TYPE_CLASS_NUMBER
                        }
                    }
                }
            }

            minutesPicker.apply {
                displayedValues = Array(1000) { it.toString() }
                minValue = displayedValues.indexOf(displayedValues.first())
                maxValue = displayedValues.indexOf(displayedValues.last())
                findEditTextAndSetInputType(this)
                value = displayedValues.run {
                    indexOf(find { it == initMinutesString } ?: first())
                }
                isSaveEnabled = false
                isSaveFromParentEnabled = false
            }

            secondsPicker.apply {
                displayedValues = Array(60) { it.toString() }
                minValue = displayedValues.indexOf(displayedValues.first())
                maxValue = displayedValues.indexOf(displayedValues.last())
                findEditTextAndSetInputType(this)
                value = displayedValues.run {
                    indexOf(find { it == initSecondsString } ?: first())
                }
                isSaveEnabled = false
                isSaveFromParentEnabled = false
            }
        }

        val dialog = builder.create()
        dialog.setCanceledOnTouchOutside(false)

        return dialog
    }
}
