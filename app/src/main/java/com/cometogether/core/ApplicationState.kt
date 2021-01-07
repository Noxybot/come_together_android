package com.cometogether.core

import com.cometogether.utils.GrpcUtils
import com.cometogether.utils.StreamObserverOverride
import com.google.firebase.installations.FirebaseInstallations
import come_together_grpc.ComeTogether

object ApplicationState {
    enum class State {
        LOGOFF, LOGGED_IN
    }
    val application_id = FirebaseInstallations.getInstance().id.toString()
    @Volatile var current_state = State.LOGOFF
    private set
    @Volatile var access_token: String = ""
    private set

    fun loginUser(login: String, password: String, onLogin: (response: ComeTogether.login_response?) -> Unit) {
        val req = ComeTogether.login_request
            .newBuilder()
            .setTyp(ComeTogether.login_request.type.BY_LOGIN_PASSWORD)
            .setLogin(login)
            .setPassword(password)
            .setAppId(ComeTogether.application_id.newBuilder().setId(application_id).build())
            .build()
        doLogin(req, onLogin)
    }
    fun loginUser(access_token: String,  onLogin: (response: ComeTogether.login_response?) -> Unit) {
        val req = ComeTogether.login_request
            .newBuilder()
            .setTyp(ComeTogether.login_request.type.BY_ACCESS_TOKEN)
            .setToken(ComeTogether.access_token.newBuilder().setValue(access_token).build())
            .setAppId(ComeTogether.application_id.newBuilder().setId(application_id).build())
            .build()
        doLogin(req, onLogin)
    }
    private fun doLogin(request: ComeTogether.login_request, onLogin: (response: ComeTogether.login_response?) -> Unit) {
        GrpcUtils.getAsyncStub().loginUser(request, object:
            StreamObserverOverride<ComeTogether.login_response>{
            override fun onNext(response: ComeTogether.login_response?) {
                val success = response?.res == ComeTogether.login_response.result.OK
                if (success)
                    current_state = State.LOGGED_IN
                access_token = response?.token?.value.toString()
                onLogin(response)
            }
        })
    }
}