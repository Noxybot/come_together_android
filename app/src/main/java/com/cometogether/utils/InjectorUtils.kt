package com.cometogether.utils

import com.cometogether.data.MarkersNetworkModel
import com.cometogether.data.MarkersRepository
import com.cometogether.ui.markers.MarkersViewModelFactory

object InjectorUtils {
    fun provideMarkersViewModelFactory() : MarkersViewModelFactory {
        val markerRepository = MarkersRepository.getInstance(MarkersNetworkModel.getInstance())
        return MarkersViewModelFactory(markerRepository)
    }
}