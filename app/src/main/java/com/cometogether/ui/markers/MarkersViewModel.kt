package com.cometogether.ui.markers

import androidx.lifecycle.ViewModel
import com.cometogether.data.MarkersRepository
import come_together_grpc.ComeTogether

class MarkersViewModel(private val markersRepository: MarkersRepository)
    : ViewModel() {
    fun getMarkers() = markersRepository.getMarkers()

    fun addMarker(marker: ComeTogether.marker_info) = markersRepository.addMarker(marker)
}