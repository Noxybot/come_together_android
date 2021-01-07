package com.cometogether.ui.markers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.cometogether.R
import com.cometogether.core.ApplicationState
import com.cometogether.utils.GrpcUtils
import com.cometogether.utils.InjectorUtils

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.installations.FirebaseInstallations
import com.google.firebase.messaging.FirebaseMessaging
import come_together_grpc.ComeTogether

class MarkersActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        initializePush()
        val test = FirebaseInstallations.getInstance().id.toString()
        ApplicationState.loginUser("vasya_poopckin", "qweASD123"
        ) { response ->
            print ("RESULT" + response?.toString()) }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
        val sydney = LatLng(-34.0, 151.0)
        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
        initializeUI()
    }
    private fun initializeUI() {
        val factory = InjectorUtils.provideMarkersViewModelFactory()
        val viewModel = ViewModelProvider(this, factory).get(MarkersViewModel::class.java)
        viewModel.getMarkers().observe(this, Observer { markers ->
            markers.forEach {marker ->
                mMap.addMarker(MarkerOptions().position(LatLng(marker.latitude, marker.longitude)))
            }
        })
    }
    private fun initializePush() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create channel to show notifications.
            val channelId = getString(R.string.default_notification_channel_id)
            val channelName = getString(R.string.default_notification_channel_name)
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(
                NotificationChannel(channelId,
                    channelName, NotificationManager.IMPORTANCE_LOW)
            )
        }
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result
            val req = ComeTogether.send_push_token_request.newBuilder()
                //.setAccessToken("token")
                .setPushToken(token)
                .build()
            GrpcUtils.getAsyncStub().sendPushToken(req, null)
        })
    }
}