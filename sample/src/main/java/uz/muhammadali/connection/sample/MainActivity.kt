package uz.muhammadali.connection.sample

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposables
import io.reactivex.schedulers.Schedulers
import uz.muhammadali.connection.ConnectionProvider
import uz.muhammadali.connection.R


class MainActivity : AppCompatActivity() {

    private lateinit var connectionProvider: ConnectionProvider

    private var disposable = Disposables.disposed()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        connectionProvider = ConnectionProvider(this, this) { isConnected ->
            Log.d(TAG, "ConnectionCallback (Lambda) invoked: $isConnected")
        }
        observeConnectionChanges()
        connectionProvider.getConnectionLiveData().observe(this) {
            Log.d(TAG, "ConnectionLiveData observed: isConnected $it")
        }
    }

    private fun observeConnectionChanges() {
        disposable = connectionProvider.getConnectionObservable()
            .observeOn(Schedulers.io())
            .subscribeOn(AndroidSchedulers.mainThread())
            .subscribe {
                Log.d(TAG, "ConnectionObservable observed: isConnected $it")
            }
    }

    private companion object {
        const val TAG = "ConnectionProvider "
    }
}