package com.cometogether.utils

import io.grpc.stub.StreamObserver

interface StreamObserverOverride<T> : StreamObserver<T> {
    override fun onError(t: Throwable?) {
        //TODO("Add some useful logging")
    }

    override fun onCompleted() {
        //TODO("Add some useful logging")
    }
}