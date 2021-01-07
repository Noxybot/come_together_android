package com.cometogether.utils

import come_together_grpc.MainEndpointGrpc
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder

object GrpcUtils {
    private val channel: ManagedChannel = ManagedChannelBuilder
        .forAddress("192.168.0.105", 53681)
        .usePlaintext()
        .build()
    fun getAsyncStub() = MainEndpointGrpc.newStub(channel)!!
    fun getBlockingStub() = MainEndpointGrpc.newBlockingStub(channel)!!
    fun getFutureStub() = MainEndpointGrpc.newFutureStub(channel)!!
}