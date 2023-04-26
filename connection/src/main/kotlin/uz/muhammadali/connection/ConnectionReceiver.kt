package uz.muhammadali.connection

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

internal class ConnectionReceiver(
    private val context: Context,
    private val onNetworkChange: ((Boolean) -> Unit)? = null
) : BroadcastReceiver() {
    private var isConnected = false
    private val connectionSharedFlow: MutableSharedFlow<Boolean> = MutableSharedFlow()
    private val connectionLiveData: MutableLiveData<Boolean> = MutableLiveData<Boolean>()
    private val networkStateSubject = PublishSubject.create<Boolean>()
    private val connectionObservable: Observable<Boolean> = networkStateSubject.hide()

    init {
        isConnected = checkConnection(context)
    }

    fun isConnected(): Boolean {
        return isConnected
    }

    fun getConnectionLiveData(): LiveData<Boolean> {
        return connectionLiveData
    }

    fun getConnectionSharedFlow(): Flow<Boolean> {
        return connectionSharedFlow
    }

    fun getConnectionObservable(): Observable<Boolean> {
        return connectionObservable
    }

    override fun onReceive(p0: Context?, p1: Intent?) {
        isConnected = checkConnection(context = context)
        onNetworkChange?.invoke(isConnected)
        setOrPostValue(isConnected)
        connectionSharedFlow.tryEmit(isConnected)
        networkStateSubject.onNext(isConnected)
    }

    private fun checkConnection(context: Context): Boolean {
        val manager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = manager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

    private fun setOrPostValue(value: Boolean) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            connectionLiveData.setValue(value)
        } else {
            connectionLiveData.postValue(value)
        }
    }
}