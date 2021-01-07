package com.cometogether.data

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.cometogether.core.ApplicationState
import com.cometogether.utils.GrpcUtils
import com.cometogether.utils.StreamObserverOverride
import com.cometogether.core.EventsDispatcher
import com.cometogether.core.EventsListener
import come_together_grpc.ComeTogether

class MarkersNetworkModel private constructor() : MarkersModelInterface, EventsListener{
    //TODO(refactor if bottleneck (maybe use MutableList for LiveData))
    private val markersList = java.util.Collections.synchronizedList(mutableListOf<ComeTogether.marker_info>())
    private val markers = MutableLiveData<List<ComeTogether.marker_info>>()
    init {
        val req = ComeTogether.access_token.newBuilder().setValue(ApplicationState.access_token).build();
        GrpcUtils.getAsyncStub().getAllMarkers(req, object:
            StreamObserverOverride<ComeTogether.marker_info> {
            override fun onNext(marker: ComeTogether.marker_info) {
                markersList.add(marker)
                markers.postValue(markersList)
            }
        })
        markers.postValue(markersList)
        EventsDispatcher.subscribeToEvents(this)
    }
    override fun getMarkers() = markers as LiveData<List<ComeTogether.marker_info>>
    override fun addMarker(marker: ComeTogether.marker_info) {
        markersList.add(marker)
        markers.postValue(markersList)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun deleteMarker(markerUUID: String) {
        val req = ComeTogether.delete_marker_request.newBuilder()
            .setToken(ComeTogether.access_token.newBuilder().setValue(ApplicationState.access_token).build())
            .setMarkerUuid(markerUUID)
            .build()
        GrpcUtils.getAsyncStub().deleteMarker(req, object:
            StreamObserverOverride<ComeTogether.delete_marker_reponse> {
            override fun onNext(response: ComeTogether.delete_marker_reponse) {
                if (response.res == ComeTogether.delete_marker_reponse.result.OK) {
                    markersList.removeIf { it.uuid == markerUUID }
                    markers.postValue(markersList)
                }
                else {
                    //TODO("Process error")
                }
            }
        })
    }

    override fun onNewMarker(marker: ComeTogether.marker_info) {
        markersList.add(marker)
        markers.postValue(markersList)
    }
    companion object {
        @Volatile private var instance: MarkersNetworkModel? = null
        fun getInstance() =
            instance ?: synchronized(this) {
                instance ?: MarkersNetworkModel().also { instance = it }
            }
    }
}