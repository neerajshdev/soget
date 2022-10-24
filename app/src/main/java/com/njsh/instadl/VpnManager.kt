package com.njsh.instadl

import android.os.Handler
import android.os.Looper
import android.util.Log
import kotlinx.coroutines.CompletableDeferred
import okhttp3.internal.notifyAll
import unified.vpn.sdk.*
import java.util.*

object VpnManager  {
    private val TAG = javaClass.simpleName
    private lateinit var selectedCountry : String
    private lateinit var base_host: String
    private lateinit var carrier_id: String
    private var ServerIPaddress = "00.000.000.00"
    private var unifiedSdk: UnifiedSdk? = null

    private var isInitialized = false

    fun init(vpnCountry: String, host: String, carrierId: String) {
        selectedCountry = vpnCountry
        base_host = host
        carrier_id = carrierId
        initHydraSdk()
    }

    fun isInitialize() = isInitialized

    private fun initHydraSdk() {
        val clientInfo = ClientInfo.newBuilder().addUrl(base_host).carrierId(carrier_id).build()
        val transportConfigList: MutableList<TransportConfig> = ArrayList()
        transportConfigList.add(HydraTransportConfig.create())
        transportConfigList.add(OpenVpnTransportConfig.tcp())
        transportConfigList.add(OpenVpnTransportConfig.udp())
        UnifiedSdk.update(transportConfigList, CompletableCallback.EMPTY)
        val config = UnifiedSdkConfig.newBuilder().build()
        unifiedSdk = UnifiedSdk.getInstance(clientInfo, config)
        val notificationConfig = SdkNotificationConfig.newBuilder().disabled().build()
        UnifiedSdk.update(notificationConfig)
        UnifiedSdk.setLoggingLevel(Log.VERBOSE)
    }

    suspend fun connect(): Boolean {
        if (loginToVpn()) {
            return connectToVpn()
        }
        return false
    }

    fun isLoggedIn(callback: Callback<Boolean?>?) {
        UnifiedSdk.getInstance().backend.isLoggedIn(callback!!)
    }

    private suspend fun loginToVpn(): Boolean {
        val deferred = CompletableDeferred<Boolean>()
        val authMethod = AuthMethod.anonymous()
        UnifiedSdk.getInstance().backend.login(authMethod, object : Callback<User> {
            override fun success(user: User) {
                deferred.complete(true)
            }

            override fun failure(e: VpnException) {
                deferred.complete(false)
                e.printStackTrace()
            }
        })
        return deferred.await()
    }

    suspend fun isConnected(): Boolean {
        val deferred = CompletableDeferred<Boolean>()

        UnifiedSdk.getVpnState(object : Callback<VpnState> {
            override fun success(vpnState: VpnState) {
                deferred.complete(vpnState == VpnState.CONNECTED)
            }

            override fun failure(e: VpnException) {
                deferred.complete(false)
            }
        })

        return deferred.await()
    }

    private suspend fun connectToVpn(): Boolean {
        val deferred = CompletableDeferred<Boolean>()

        val fallbackOrder: MutableList<String> =
            ArrayList() //                    fallbackOrder.add(HydraTransport.TRANSPORT_ID);
        fallbackOrder.add(OpenVpnTransport.TRANSPORT_ID_TCP)
        fallbackOrder.add(OpenVpnTransport.TRANSPORT_ID_UDP) //          showConnectProgress();
        val bypassDomains: MutableList<String> = LinkedList()
        bypassDomains.add("*facebook.com")
        bypassDomains.add("*wtfismyip.com")
        UnifiedSdk.getInstance().vpn.start(SessionConfig.Builder()
            .withReason(TrackingConstants.GprReasons.M_UI).withTransportFallback(fallbackOrder)
            .withVirtualLocation(selectedCountry).withTransport(HydraTransport.TRANSPORT_ID)
            .addDnsRule(TrafficRule.Builder.bypass().fromDomains(bypassDomains)).build(),
            object : CompletableCallback {
                override fun complete() {
                    println("connection complete")
                    deferred.complete(true)
                }

                override fun error(e: VpnException) {
                    deferred.complete(false)
                    e.printStackTrace()
                }
            })

        return deferred.await()
    }

    fun disconnectFromVnp() {
        UnifiedSdk.getInstance().vpn.stop(
            TrackingConstants.GprReasons.M_UI,
            object : CompletableCallback {
                override fun complete() {
                }

                override fun error(e: VpnException) {
                    e.printStackTrace()
                }
            })
    }

    fun getCurrentServer(callback: Callback<String?>) {
        UnifiedSdk.getVpnState(object : Callback<VpnState> {
            override fun success(state: VpnState) {
                if (state == VpnState.CONNECTED) {
                    UnifiedSdk.getStatus(object : Callback<SessionInfo> {
                        override fun success(sessionInfo: SessionInfo) {
                            ServerIPaddress =
                                sessionInfo.credentials!!.servers[0].address //              ShowIPaddera(ServerIPaddress);
                            callback.success(sessionInfo.credentials!!.servers[0].country!!)
                        }

                        override fun failure(e: VpnException) {
                            callback.success(selectedCountry)
                        }
                    })
                } else {
                    callback.success(selectedCountry)
                }
            }

            override fun failure(e: VpnException) {
                callback.failure(e)
            }
        })
    }
}
