package com.example.mapsandalarmsapp.feature_map.view

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController

import com.example.mapsandalarmsapp.R
import com.example.mapsandalarmsapp.core.helpers.AlertDialogHelper
import com.example.mapsandalarmsapp.core.helpers.AlerterHelper
import com.example.mapsandalarmsapp.core.helpers.SharePreferenceHelper
import com.example.mapsandalarmsapp.core.helpers.toastSimple
import com.example.mapsandalarmsapp.databinding.FragmentMapBinding
import com.example.mapsandalarmsapp.databinding.ViewMarkerBinding
import com.example.mapsandalarmsapp.feature_map.model.LocationModel
import com.example.mapsandalarmsapp.feature_map.model.MarkerModel
import com.example.mapsandalarmsapp.feature_map.viewmodel.MapViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MapFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMapClickListener  {
    //region VARS AND VALS
    companion object {
        private const val NUMBER_MAX_MARKERS = 10
    }
    private val viewModel: MapViewModel by viewModels()
    private val alerter by lazy { AlerterHelper(requireActivity()) }
    private val alertDialogHelper by lazy { AlertDialogHelper(requireActivity()) }
    private val preference by lazy { SharePreferenceHelper.getInstance(requireContext()) }
    private lateinit var binding: FragmentMapBinding
    private lateinit var map: GoogleMap
    //endregion

    //region LIFE CYCLE ACTIVITY
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return initBinding(inflater, container)
    }

    private fun initBinding(inflater: LayoutInflater, container: ViewGroup?): View {
        binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initMap()
        initListeners()
        initObservers()
    }
    //endregion

    //region ON VIEW CREATED METHODS
    private fun initMap() {
        val mapGoogle = childFragmentManager.findFragmentById(R.id.map_markers) as SupportMapFragment
        mapGoogle.getMapAsync(this)
        binding.switchMarkers.isChecked = preference?.getBooData(
            SharePreferenceHelper.KEY_IS_CHECKED
        ) ?: false
    }

    private fun initListeners() {
        with(binding) {
            buttonAddAlarm.setOnClickListener { ::goToAddAlarm.invoke() }
            buttonDeleteMarkers.setOnClickListener { ::clearAllMarkers.invoke() }
            switchMarkers.setOnCheckedChangeListener { _, isChecked ->
                preference?.saveData(SharePreferenceHelper.KEY_IS_CHECKED, isChecked)
                if (isChecked) {
                    context?.toastSimple("Add markers when tapping on map is enabled")
                } else {
                    context?.toastSimple("Add markers when tapping on map is disable")
                }
            }
        }
    }

    private fun initObservers() {
        viewModel.listMarkers.observe(viewLifecycleOwner) { list ->
            if (list.isNotEmpty()) {
                map.clear()
                list.forEach { model ->
                    addMarkerToMap(
                        model.index,
                        model.locationModel.location?.longitude ?: 0.0,
                        model.locationModel.location?.latitude ?: 0.0
                    )
                }
            }
        }
        viewModel.isClear.observe(viewLifecycleOwner) { isClear ->
            if (isClear) map.clear()
        }
    }
    //endregion

    //region LISTENERS METHODS
    private fun goToAddAlarm() {
        val action = MapFragmentDirections.actionMapFragmentToListAlarmsFragment()
        findNavController().navigate(action)
    }

    private fun clearAllMarkers() {
        if (viewModel.getMarkersSize() != 0) {
            alertDialogHelper.showAlertDialog(
                "Clear all markers",
                "Are you sure you want to delete all markers?",
                "Close",
                "Delete all",
                {},
                {
                    viewModel.clearAllMarkers()
                    viewModel.setIsClear(true)
                }
            )
        } else context?.toastSimple("No markers exist")
    }
    //endregion

    //region ON MAP READY METHODS
    private fun goToUserLocation() {
        viewModel.goToUserLocation { longitude, latitude ->
            animateToLocationMap(longitude, latitude)
        }
    }

    @SuppressLint("PotentialBehaviorOverride")
    private fun actionMarkerSelected() {
        map.setOnMarkerClickListener { marker ->
            viewModel.getAddressLocation(marker, ::callbackAddress)
            true
        }
    }
    //endregion

    //region MAPS METHODS
    private fun animateToLocationMap(longitude: Double, latitude: Double) {
        val location = LatLng(latitude, longitude)
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 20f))
    }

    @SuppressLint("SetTextI18n")
    private fun addMarkerToMap(index: Long, longitude: Double, latitude: Double) {
        val viewMarker = ViewMarkerBinding.inflate(
            LayoutInflater.from(requireActivity()), null, false
        )
        viewMarker.data.text = "Latitude: $latitude, Longitude: $longitude"
        val markerData = MarkerOptions()
            .draggable(false)
            .position(LatLng(latitude, longitude))
            .title(index.toString())
            .icon(
                BitmapDescriptorFactory.fromBitmap(
                    loadBitmapFromView(
                        viewMarker.root
                    )
                )
            )
        map.addMarker(markerData)
    }

    private fun callbackAddress(markerId:Long, message: String) {
        alertDialogHelper.showAlertDialog(
            "Location",
            message,
            "Close",
            "Delete",
            {},
            { viewModel.deleteMarker(markerId)}
        )
    }

    private fun loadBitmapFromView(view: View): Bitmap {
        view.measure(
            WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT
        )
        val bitmap = Bitmap.createBitmap(view.measuredWidth, view.measuredHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.layout(0, 0, view.measuredWidth, view.measuredHeight)
        view.draw(canvas)
        return bitmap
    }
    //endregion

    //region MAPS INTERFACE METHODS
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.setOnMapClickListener(this)
        viewModel.getAllMarkersFromDataBase()
        goToUserLocation()
        actionMarkerSelected()
    }

    override fun onMapClick(position: LatLng) {
        if (binding.switchMarkers.isChecked) {
            if (viewModel.getMarkersSize() < NUMBER_MAX_MARKERS) {
                viewModel.addNewMarker(MarkerModel(
                    viewModel.getMarkersSize().toLong(),
                    LocationModel(LatLng( position.latitude, position.longitude),"MAP"),
                    ""
                ))
            } else {
                alerter.showAlert(
                    R.color.color_accent,
                    "Add markers",
                    "You can no longer add any markers, the limit of $NUMBER_MAX_MARKERS has already been passed.",
                    R.drawable.icon_notifications_active,
                    AlerterHelper.LONG_TIME
                )
            }
        } else {
            alerter.showAlert(
                R.color.color_accent,
                "Add markers is not enable",
                "Please activate the option to add markers in the top menu when changing the switch status.",
                R.drawable.icon_notifications_active,
                AlerterHelper.LONG_TIME
            )
        }
    }
    //endregion
}