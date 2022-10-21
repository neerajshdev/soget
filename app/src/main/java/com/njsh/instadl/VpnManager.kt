package com.njsh.instadl

import android.os.Handler
import android.os.Looper
import android.util.Log
import okhttp3.internal.notifyAll
import okhttp3.internal.wait
import unified.vpn.sdk.*
import java.util.*

class VpnManager(target_country: String, host: String, carrier: String) : VpnStateListener
{
    private val TAG = javaClass.simpleName
    private var selectedCountry = ""
    private val base_host: String
    private val carrier_id: String
    private var ServerIPaddress = "00.000.000.00"
    private var unifiedSdk: UnifiedSdk? = null
    private val handler: Handler



    fun waitForVpn()
    {
        synchronized(this) {
            try
            {
                this.wait()
            } catch (e: InterruptedException)
            {
                e.printStackTrace()
            }
        }
    }

    fun Connect()
    {
        UnifiedSdk.addVpnStateListener(this)
        isLoggedIn(object : Callback<Boolean?>
        {
            override fun success(aBoolean: Boolean)
            {
                if (!aBoolean)
                {
                    handler.post { loginToVpn() }
                } else
                {
                    handler.post { connectToVpn() }
                }
            }

            override fun failure(e: VpnException)
            {
            }
        })
    }

    fun Disconnect()
    {
        disconnectFromVnp()
        UnifiedSdk.removeVpnStateListener(this)
    }

    private fun initHydraSdk()
    {
//        SharedPreferences prefs = getPrefs();
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

    override fun vpnStateChanged(vpnState: VpnState)
    {
        when (vpnState)
        {
            VpnState.IDLE ->
            {
            }
            VpnState.CONNECTING_VPN ->
            {
            }
            VpnState.CONNECTED -> synchronized(this) { notifyAll() }
            VpnState.ERROR -> synchronized(this) { notifyAll() }
            VpnState.UNKNOWN -> synchronized(this) { notifyAll() }
            VpnState.DISCONNECTING -> synchronized(this) { notifyAll() }
            VpnState.CONNECTING_PERMISSIONS ->
            {
            }
            VpnState.CONNECTING_CREDENTIALS ->
            {
            }
            VpnState.PAUSED -> synchronized(this) { notifyAll() }
        }
    }

    override fun vpnError(e: VpnException)
    {
        e.printStackTrace()
    }

    protected fun isLoggedIn(callback: Callback<Boolean?>?)
    {
        UnifiedSdk.getInstance().backend.isLoggedIn(callback!!)
    }

    protected fun loginToVpn()
    {
        Log.e(TAG, "loginToVpn: 1111")
        val authMethod = AuthMethod.anonymous()
        UnifiedSdk.getInstance().backend.login(authMethod, object : Callback<User>
        {
            override fun success(user: User)
            {
                handler.post {
                    connectToVpn()
                }
            }

            override fun failure(e: VpnException)
            {
                e.printStackTrace()
            }
        })
    }

    protected fun isConnected(callback: Callback<Boolean?>)
    {
        UnifiedSdk.getVpnState(object : Callback<VpnState>
        {
            override fun success(vpnState: VpnState)
            {
                callback.success(vpnState == VpnState.CONNECTED)
            }

            override fun failure(e: VpnException)
            {
                callback.success(false)
            }
        })
    }

    protected fun connectToVpn()
    {
        val fallbackOrder: MutableList<String> = ArrayList()
        //                    fallbackOrder.add(HydraTransport.TRANSPORT_ID);
        fallbackOrder.add(OpenVpnTransport.TRANSPORT_ID_TCP)
        fallbackOrder.add(OpenVpnTransport.TRANSPORT_ID_UDP)
        //          showConnectProgress();
        val bypassDomains: MutableList<String> = LinkedList()
        bypassDomains.add("*facebook.com")
        bypassDomains.add("*wtfismyip.com")
        UnifiedSdk.getInstance().vpn.start(SessionConfig.Builder()
            .withReason(TrackingConstants.GprReasons.M_UI).withTransportFallback(fallbackOrder)
            .withVirtualLocation(selectedCountry).withTransport(HydraTransport.TRANSPORT_ID)
            .addDnsRule(TrafficRule.Builder.bypass().fromDomains(bypassDomains)).build(),
            object : CompletableCallback
            {
                override fun complete()
                {
                    println("connection complete")
                }

                override fun error(e: VpnException)
                {
                    e.printStackTrace()
                }
            })
    }

    protected fun disconnectFromVnp()
    {
        UnifiedSdk.getInstance().vpn.stop(TrackingConstants.GprReasons.M_UI,
            object : CompletableCallback
            {
                override fun complete()
                {
                }

                override fun error(e: VpnException)
                {
                    e.printStackTrace()
                }
            })
    }

    protected fun getCurrentServer(callback: Callback<String?>)
    {
        UnifiedSdk.getVpnState(object : Callback<VpnState>
        {
            override fun success(state: VpnState)
            {
                if (state == VpnState.CONNECTED)
                {
                    UnifiedSdk.getStatus(object : Callback<SessionInfo>
                    {
                        override fun success(sessionInfo: SessionInfo)
                        {
                            ServerIPaddress = sessionInfo.credentials!!.servers[0].address
                            //              ShowIPaddera(ServerIPaddress);
                            callback.success(sessionInfo.credentials!!.servers[0].country!!)
                        }

                        override fun failure(e: VpnException)
                        {
                            callback.success(selectedCountry)
                        }
                    })
                } else
                {
                    callback.success(selectedCountry)
                }
            }

            override fun failure(e: VpnException)
            {
                callback.failure(e)
            }
        })
    }



    init
    {
        selectedCountry = target_country
        base_host = host
        carrier_id = carrier
        handler = Handler(Looper.getMainLooper())
        initHydraSdk()
    }
}