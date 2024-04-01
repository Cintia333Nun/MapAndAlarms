package com.example.mapsandalarmsapp.feature_alarms.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.mapsandalarmsapp.R
import com.example.mapsandalarmsapp.core.helpers.AlerterHelper
import com.example.mapsandalarmsapp.core.helpers.TimeAndDateHelper
import com.example.mapsandalarmsapp.core.helpers.gone
import com.example.mapsandalarmsapp.core.helpers.toastSimple
import com.example.mapsandalarmsapp.core.helpers.validateText
import com.example.mapsandalarmsapp.core.helpers.visible
import com.example.mapsandalarmsapp.databinding.FragmentAlarmBinding
import com.example.mapsandalarmsapp.feature_alarms.model.AlarmModel
import com.example.mapsandalarmsapp.feature_alarms.viewmodel.AlarmViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AlarmFragment : Fragment() {
    private lateinit var binding: FragmentAlarmBinding
    private val viewModel: AlarmViewModel by viewModels()
    private val timeAndDateHelper by lazy { TimeAndDateHelper(requireContext()) }

    private var timeAlarmMil: Long? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return initBinding(inflater, container)
    }

    private fun initBinding(inflater: LayoutInflater, container: ViewGroup?): View {
        binding = FragmentAlarmBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initListeners()
    }

    private fun initListeners() {
        binding.buttonAddDay.setOnClickListener{::addDayButton.invoke()}
        binding.buttonSave.setOnClickListener {::saveAlarmButton.invoke()}
    }

    private fun addDayButton() {
        val minDate = System.currentTimeMillis()
        val maxDate = minDate + (10 * 24 * 60 * 60 * 1000)

        timeAndDateHelper.setMinDate(minDate)
        timeAndDateHelper.setMaxDate(maxDate)
        timeAndDateHelper.setOnDateTimeSelectedListener { calendar ->
            timeAlarmMil = calendar.time.time
            binding.buttonAddDay.text = timeAndDateHelper.getDateTimeFormat(calendar)
        }
        timeAndDateHelper.showDateTimePicker()
    }

    private fun saveAlarmButton() {
        if (checkForm()) {
            try {
                val currentTimeMillis = System.currentTimeMillis()
                val delayMillis = timeAlarmMil!! - currentTimeMillis
                if (delayMillis > 0) {
                    binding.textErrorDateAndTime.gone()
                    viewModel.addAlarm(
                        AlarmModel(
                            (timeAlarmMil?:0).toInt(),
                            timeAlarmMil!!,
                            binding.editTextTitle.text.toString(),
                            binding.editTextMessage.text.toString()
                        )
                    )
                    AlerterHelper(requireActivity()).showAlert(
                        R.color.color_accent, "New Alert",
                        "The alert has been created successfully and will sound at ${binding.buttonAddDay.text}",
                        R.drawable.icon_map, AlerterHelper.LONG_TIME
                    )
                    activity?.onBackPressedDispatcher?.onBackPressed()
                } else {
                    binding.textErrorDateAndTime.visible()
                    context?.toastSimple("The alarm time has already passed")
                }
            } catch (e: Exception) {
                context?.toastSimple("An internal error has occurred in the app")
            }
        }
    }

    private fun checkForm(): Boolean {
        var isOk = timeAlarmMil != null
        if (isOk) binding.textErrorDateAndTime.gone()
        else binding.textErrorDateAndTime.visible()
        isOk = isOk.and(binding.editTextTitle.validateText())
        isOk = isOk.and(binding.editTextMessage.validateText())
        isOk = isOk.and(binding.editTextTitle.validateText())
        return isOk
    }
}