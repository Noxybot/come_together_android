package com.cometogether.core

import come_together_grpc.ComeTogether

interface EventsListener {
    fun onNewMarker(marker: ComeTogether.marker_info) {}
    fun onMarkerEdited(marker: ComeTogether.marker_info) {}
    fun onMarkerDeleted(markerUuid: String, byUser: Boolean) {}
    fun onNewMessage(message: ComeTogether.chat_message) {}
    fun onMessageEdited(message: ComeTogether.chat_message) {}
    fun onMessageDeleted(messageUuid: String) {}
    fun onUserEdited(userInfo: ComeTogether.user_info) {}
}