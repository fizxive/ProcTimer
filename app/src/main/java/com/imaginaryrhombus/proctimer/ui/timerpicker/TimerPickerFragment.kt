package com.imaginaryrhombus.proctimer.ui.timerpicker

import android.app.AlertDialog
import android.app.Dialog
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import com.imaginaryrhombus.proctimer.R
import kotlinx.android.synthetic.main.timer_picker_fragment.view.*

class TimerPickerFragment : DialogFragment() {

    companion object {
        fun newInstance() = TimerPickerFragment()
    }

    private lateinit var viewModel: TimerPickerViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.timer_picker_fragment, container, false)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreateDialog(savedInstanceState)

        val builder = AlertDialog.Builder(activity).run {
            setMessage(R.string.timer_dialog_text)
            setPositiveButton(R.string.timer_dialog_positive_button) {dialogInterface, i ->  }
            setNegativeButton(R.string.timer_dialog_negative_button) {dialogInterface, i ->  }
            this
        }

        activity?.let { activity ->
            val inflater = activity.layoutInflater
            val view = inflater.inflate(R.layout.timer_picker_fragment, null).apply {

                val values = Array(1000) { value -> value.toString() }

                fun findEditTextAndSetInputType(viewGroup: ViewGroup) {
                    for (i in 0 until viewGroup.childCount)
                    {
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
                }

                secondsPicker.apply {
                    displayedValues = values.copyOfRange(0, 60)
                    minValue = 0
                    maxValue = 59
                    findEditTextAndSetInputType(this)
                }
            }

            builder.setView(view)
        }

        return builder.create()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(TimerPickerViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
