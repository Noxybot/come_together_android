package com.cometogether.ui.markers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.cometogether.data.MarkersRepository

class MarkersViewModelFactory(private val repository: MarkersRepository)
    : ViewModelProvider.NewInstanceFactory(){
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return MarkersViewModel(repository) as T;
    }
}