package com.example.mapsandalarmsapp.feature_alarms.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.colorResource
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.mapsandalarmsapp.R
import com.example.mapsandalarmsapp.core.helpers.AlerterHelper
import com.example.mapsandalarmsapp.core.helpers.TimeAndDateHelper
import com.example.mapsandalarmsapp.core.helpers.toastSimple
import com.example.mapsandalarmsapp.feature_alarms.model.AlarmModel
import com.example.mapsandalarmsapp.feature_alarms.viewmodel.AlarmViewModel
import com.example.mapsandalarmsapp.ui.theme.MapsAndAlarmsAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ListAlarmsFragment : Fragment() {
    private val viewModel: AlarmViewModel by viewModels()
    private var numberAlarms = 0
    companion object {
        private const val NUMBER_MAX_ALARMS = 15
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel.getAllAlarms()
        return ComposeView(requireContext()).apply {
            setContent {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = colorResource(id = R.color.white)
                ) {
                    ListAlarmsScreen(viewModel, ::goToNewAlarm)
                }
            }
        }
    }

    private fun goToNewAlarm() {
        if (numberAlarms <= NUMBER_MAX_ALARMS) {
            val action = ListAlarmsFragmentDirections.actionListAlarmsFragmentToAlarmFragment()
            findNavController().navigate(action)
        } else {
            AlerterHelper(requireActivity()).showAlert(
                R.color.color_accent,
                "Add alarm",
                "You can no longer add any alarms, " +
                        "the limit of $NUMBER_MAX_ALARMS has already been passed",
                R.drawable.icon_notifications_active,
                AlerterHelper.LONG_TIME
            )
        }
    }
}