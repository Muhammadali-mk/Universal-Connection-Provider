package uz.muhammadali.connection

import android.content.Context
import android.content.IntentFilter
import android.net.ConnectivityManager
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import io.reactivex.Observable
import kotlinx.coroutines.flow.Flow
import java.lang.ref.WeakReference

class ConnectionProvider(
    private val context: Context,
    lifecycleOwner: LifecycleOwner? = null,
    private val onNetworkChange: ((Boolean) -> Unit)? = null
) : DefaultLifecycleObserver {

    private val lifecycleReference = WeakReference(lifecycleOwner)
    private val receiver: ConnectionReceiver

    init {
        lifecycleReference.get()?.lifecycle?.addObserver(this)
        receiver = ConnectionReceiver(context) { isConnected ->
            onNetworkChange?.invoke(isConnected)
        }
    }

    override fun onCreate(owner: LifecycleOwner) {
        val intentFilter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        context.registerReceiver(receiver, intentFilter)
        super.onCreate(owner)
    }

    fun getConnectionObservable(): Observable<Boolean> {
        return receiver.getConnectionObservable()
    }

    fun isConnected(): Boolean {
        return receiver.isConnected()
    }

    fun isDisconnected(): Boolean {
        return receiver.isConnected().not()
    }

    override fun onDestroy(owner: LifecycleOwner) {
        context.unregisterReceiver(receiver)
        lifecycleReference.get()?.lifecycle?.removeObserver(this)
        super.onDestroy(owner)
    }

    fun getConnectionLiveData(): LiveData<Boolean> {
        return receiver.getConnectionLiveData()
    }

    fun getConnectionFlow(): Flow<Boolean> {
        return receiver.getConnectionSharedFlow()
    }
}