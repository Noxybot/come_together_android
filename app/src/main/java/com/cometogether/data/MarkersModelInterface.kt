package com.cometogether.data

import androidx.lifecycle.LiveData
import come_together_grpc.ComeTogether

interface MarkersModelInterface {
    fun getMarkers() : LiveData<List<ComeTogether.marker_info>>
    fun addMarker(marker: ComeTogether.marker_info)
    fun deleteMarker(markerUUID: String)
}