package com.imaginaryrhombus.proctimer.ui.timer

import android.content.res.Resources
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.databinding.DataBindingUtil
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.imaginaryrhombus.proctimer.R
import com.imaginaryrhombus.proctimer.databinding.TimerFragmentBinding
import com.imaginaryrhombus.proctimer.ui.timerpicker.TimerPickerFragment
import kotlinx.android.synthetic.main.timer_fragment.*

class TimerFragment : Fragment() {

    companion object {
        fun newInstance() = TimerFragment()
    }

    private lateinit var viewModel: TimerViewModel
    private lateinit var binding : TimerFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate<TimerFragmentBinding>(inflater, R.layout.timer_fragment, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        activity?.run {
            viewModel = ViewModelProviders.of(this).get(TimerViewModel::class.java)
        } ?: throw Resources.NotFoundException("Activity Not found.")

        binding.timerViewModel = viewModel
        binding.setLifecycleOwner(this)

        startButton.setOnClickListener {
            viewModel.startTick()
        }

        stopButton.setOnClickListener {
            viewModel.stopTick()
        }

        currentTimerText.setOnClickListener {
            val pickerFragment = TimerPickerFragment()
            pickerFragment.show(fragmentManager, "PickerDialog")
        }
    }
}
