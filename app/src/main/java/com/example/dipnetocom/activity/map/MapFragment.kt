package com.example.dipnetocom.activity.map

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.dipnetocom.R
import com.example.dipnetocom.databinding.FragmentMapBinding
import com.example.dipnetocom.databinding.PlaceBinding
import com.example.dipnetocom.dto.Coordinates
import com.example.dipnetocom.viewmodel.EventViewModel
import com.example.dipnetocom.viewmodel.MapViewModel
import com.example.dipnetocom.viewmodel.PostViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.layers.ObjectEvent
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.InputListener
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.mapview.MapView
import com.yandex.mapkit.user_location.UserLocationLayer
import com.yandex.mapkit.user_location.UserLocationObjectListener
import com.yandex.mapkit.user_location.UserLocationView
import com.yandex.runtime.ui_view.ViewProvider
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MapFragment : Fragment() {

    companion object {
        const val LAT_KEY = "LAT_KEY"
        const val LONG_KEY = "LONG_KEY"
        const val ITEM_TYPE = "ITEM_TYPE"

        enum class ItemType {
            POST, EVENT
        }
    }

    private val mapViewModel: MapViewModel by activityViewModels()
    private val postViewModel: PostViewModel by activityViewModels()
    private val eventViewModel: EventViewModel by activityViewModels()

    private var mapView: MapView? = null

    private lateinit var userLocationLayer: UserLocationLayer

    private val mapTapListener = object : InputListener {
        override fun onMapTap(map: Map, point: Point) = Unit

        override fun onMapLongTap(map: Map, point: Point) {
            val latitude = point.latitude.toString().substring(0, 9)
            val longitude = point.longitude.toString().substring(0, 9)
            MaterialAlertDialogBuilder(requireContext())
                .create().apply {
                    setTitle(R.string.coordinates)
                    setMessage(getString(R.string.latitude_and_longitude, latitude, longitude))
                    setIcon(R.drawable.locate_24)
                    setButton(
                        AlertDialog.BUTTON_POSITIVE,
                        getString(android.R.string.ok)
                    ) { _, _ ->
                        mapViewModel.setCoordinates(
                            Coordinates(
                                latitude,
                                longitude
                            )
                        )
                    }
                    setButton(
                        AlertDialog.BUTTON_NEGATIVE,
                        getString(android.R.string.cancel)
                    ) { _, _ ->
                        dismiss()
                    }
                }.show()
        }
    }

    private val locationObjectListener = object : UserLocationObjectListener {
        override fun onObjectAdded(userLocationView: UserLocationView) = Unit

        override fun onObjectRemoved(view: UserLocationView) = Unit

        override fun onObjectUpdated(view: UserLocationView, event: ObjectEvent) {
            userLocationLayer.cameraPosition()?.target?.let {
                mapView?.map?.move(CameraPosition(it, 10F, 0F, 0F))
            }
            userLocationLayer.setObjectListener(null)
        }
    }

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            when {
                granted -> {
                    userLocationLayer.isVisible = true
                    userLocationLayer.isHeadingEnabled = false
                    userLocationLayer.cameraPosition()?.target?.also {
                        val map = mapView?.map ?: return@registerForActivityResult
                        val cameraPosition = map.cameraPosition
                        map.move(
                            CameraPosition(
                                it,
                                cameraPosition.zoom,
                                cameraPosition.azimuth,
                                cameraPosition.tilt,
                            )
                        )
                    }
                }

                else -> {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.permission_request),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MapKitFactory.initialize(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentMapBinding.inflate(inflater, container, false)

        mapView = binding.mapView.apply {
            userLocationLayer = MapKitFactory.getInstance().createUserLocationLayer(mapWindow)
            if (requireActivity()
                    .checkSelfPermission(
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
            ) {
                userLocationLayer.isVisible = true
                userLocationLayer.isHeadingEnabled = false
            }

            map.addInputListener(mapTapListener)

            val collection = map.mapObjects.addCollection()
            viewLifecycleOwner.lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    mapViewModel.place.collectLatest {
                        collection.clear()
                        it?.let { coordinates ->
                            val placeBinding = PlaceBinding.inflate(layoutInflater)
                            collection.addPlacemark(
                                Point(coordinates.lat.toDouble(), coordinates.long.toDouble()),
                                ViewProvider(placeBinding.root)
                            )
                        }
                    }
                }
            }

            val arguments = arguments
            if (arguments != null &&
                arguments.containsKey(LAT_KEY) &&
                arguments.containsKey(LONG_KEY)
            ) {
                map.removeInputListener(mapTapListener)
                val latitude = arguments.getDouble(LAT_KEY)
                val longitude = arguments.getDouble(LONG_KEY)
                mapViewModel.setPlace(Coordinates(latitude.toString(), longitude.toString()))
                val cameraPosition = map.cameraPosition
                map.move(
                    CameraPosition(
                        Point(arguments.getDouble(LAT_KEY), arguments.getDouble(LONG_KEY)),
                        10F,
                        cameraPosition.azimuth,
                        cameraPosition.tilt,
                    )
                )
                arguments.remove(LAT_KEY)
                arguments.remove(LONG_KEY)
            } else {
                userLocationLayer.setObjectListener(locationObjectListener)
            }
        }

        binding.fabLocation.setOnClickListener {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        binding.plus.setOnClickListener {
            binding.mapView.map.move(
                CameraPosition(
                    binding.mapView.map.cameraPosition.target,
                    binding.mapView.map.cameraPosition.zoom + 1, 0.0f, 0.0f
                ),
                Animation(Animation.Type.SMOOTH, 0.3F),
                null
            )
        }

        binding.minus.setOnClickListener {
            binding.mapView.map.move(
                CameraPosition(
                    binding.mapView.map.cameraPosition.target,
                    binding.mapView.map.cameraPosition.zoom - 1, 0.0f, 0.0f
                ),
                Animation(Animation.Type.SMOOTH, 0.3F),
                null,
            )
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                mapViewModel.coordinates.collectLatest { coords ->
                    coords?.let {
                        when (requireArguments().getString(ITEM_TYPE)) {
                            ItemType.POST.name -> {
                                postViewModel.addCoordinates(coords)
                            }

                            ItemType.EVENT.name -> {
                                eventViewModel.addEventCoordinates(coords)
                            }
                        }
                        findNavController().navigateUp()
                        mapViewModel.clearCoordinates(coords)
                    }
                }
            }
        }

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        MapKitFactory.getInstance().onStart()
        mapView?.onStart()
    }

    override fun onStop() {
        mapView?.onStop()
        MapKitFactory.getInstance().onStop()
        super.onStop()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mapView = null
    }
}