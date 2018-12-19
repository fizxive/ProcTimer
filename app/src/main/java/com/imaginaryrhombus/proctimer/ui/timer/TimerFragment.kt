package com.imaginaryrhombus.proctimer.ui.timer

import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.imaginaryrhombus.proctimer.R
import com.imaginaryrhombus.proctimer.databinding.TimerFragmentBinding

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
        viewModel = ViewModelProviders.of(this).get(TimerViewModel::class.java)

        viewModel.startTick()
    }
}
