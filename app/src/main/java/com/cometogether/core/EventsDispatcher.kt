package com.cometogether.core

import com.cometogether.utils.GrpcUtils
import come_together_grpc.ComeTogether
import io.grpc.stub.StreamObserver

object EventsDispatcher{
    enum class State {
        CONNECTED, DISCONNECTED
    }
    @Volatile var current_state = State.DISCONNECTED
    private set
    private val listeners = java.util.Collections.synchronizedList(mutableListOf<EventsListener>())
    fun subscribeToEvents(listener: EventsListener) {
        listeners.add(listener)
    }
    init {
        connectToServer()
    }
    fun connectToServer() {
        val req = ComeTogether.application_id.newBuilder().setId(ApplicationState.application_id).build()
        GrpcUtils.getAsyncStub().subscribeToEvents(req, object:
            StreamObserver<ComeTogether.event> {
            init {current_state = State.CONNECTED}
            override fun onNext(event: ComeTogether.event) {
                when (event.eventType) {
                    ComeTogether.event.type.MARKER_ADDED -> listeners.forEach { it.onNewMarker(event.mInfo) }
                    ComeTogether.event.type.MARKER_DELETED_BY_TIMEOUT -> listeners.forEach { it.onMarkerDeleted(event.mInfo.uuid, false) }
                    ComeTogether.event.type.MARKER_DELETED_BY_USER -> listeners.forEach { it.onMarkerDeleted(event.mInfo.uuid, true) }
                    ComeTogether.event.type.MARKER_EDITED -> listeners.forEach { it.onMarkerEdited(event.mInfo) }
                    ComeTogether.event.type.MESSAGE_ADDED -> listeners.forEach { it.onNewMessage(event.cMessage) }
                    ComeTogether.event.type.MESSAGE_DELETED -> listeners.forEach { it.onMessageDeleted(event.cMessage.uuid) }
                    ComeTogether.event.type.MESSAGE_EDITED -> listeners.forEach {it.onMessageEdited(event.cMessage) }
                    ComeTogether.event.type.USER_UPDATED -> listeners.forEach {it.onUserEdited(event.uInfo) }
                    else -> print("Unknown eventType: " + event.eventType.toString())
                }

            }
            override fun onError(t: Throwable?) {
                print(t?.toString())
                current_state = State.DISCONNECTED
                listeners.clear()
                connectToServer()
            }
            override fun onCompleted() {
                print("completed")
                current_state = State.DISCONNECTED
                listeners.clear()
                connectToServer()
            }
        })
    }

}