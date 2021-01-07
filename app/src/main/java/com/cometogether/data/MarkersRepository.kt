package com.cometogether.data

import come_together_grpc.ComeTogether

class MarkersRepository private constructor(private val markersModel: MarkersModelInterface) {
    fun addMarker(marker: ComeTogether.marker_info) {
        markersModel.addMarker(marker);
    }

    fun getMarkers() = markersModel.getMarkers()

    companion object {
        @Volatile private var instance: MarkersRepository? = null
        fun getInstance(markersModel: MarkersModelInterface) =
            instance ?: synchronized(this) {
                instance ?: MarkersRepository(markersModel).also { instance = it }
            }
    }
}