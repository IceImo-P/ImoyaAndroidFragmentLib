package net.imoya.android.fragment.roundtrip

import android.os.Bundle
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentResultListener
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.LifecycleOwner
import net.imoya.android.fragment.ArgumentsUtil
import net.imoya.android.fragment.FragmentLog
import net.imoya.android.fragment.R

/**
 * Round-trip (往復)ナビゲーションの "host" が使用するロジックの abstract
 *
 * 本ライブラリに於いて、ある [AppCompatActivity] または [Fragment] が別の
 * [Fragment] を起動し、起動された [Fragment] が起動元の画面へ結果を通知するまでの一連の処理を
 * Round-trip (往復)ナビゲーションと呼びます。
 *
 * * [Fragment] を起動する側の [AppCompatActivity] または [Fragment] を "host" (ホスト) と呼びます。
 * * "host" または別の "client" に起動され、 "host" へ通知する結果を生成する [Fragment] を
 *   "client" (クライアント) と呼びます。
 * * "client" は複数の [Fragment] となり得ます。つまり、
 *   "host" → "client" 1 → "client" 2 → … → "host" という順番の遷移もあり得ます。
 *
 * このクラスは、 "host" が使用するロジックの abstract クラスです。
 */
abstract class RoundTripManager {
    /**
     * Returns [FragmentManager]
     */
    abstract val fragmentManager: FragmentManager

    /**
     * Returns [LifecycleOwner]
     */
    abstract val lifecycleOwner: LifecycleOwner

    /**
     * "host" の画面に於いて、 "client" を表示する領域となる [android.view.View] の ID
     */
    @IdRes
    var containerId: Int = 0

    /**
     * 指定のリクエストキーで表示した [androidx.fragment.app.Fragment] の終了時に、
     * [FragmentManager] が [FragmentResultListener.onFragmentResult] をコールするように設定します。
     *
     * 登録のタイミングはいつでも良いですが、なるべく画面の生成時に実行することを推奨します。
     *
     * @param requestKey リクエストキー。
     *                   "host" となる画面の中で、重複しない文字列を指定します。
     * @param listener   "client" が返した結果を受け取る [FragmentResultListener]
     */
    @Suppress("unused")
    fun setResultListener(requestKey: String, listener: FragmentResultListener) {
        fragmentManager.setFragmentResultListener(
            requestKey,
            lifecycleOwner,
            listener
        )
    }

    /**
     * Round-trip (往復)ナビゲーションに於ける、最初の "client" を開始します。
     *
     * @param requestKey  リクエストキー。
     *                    Round-trip (往復)ナビゲーションを開始する画面の中で、重複しない文字列を指定します。
     * @param fragment    開始する最初の "client" となる [RoundTripClientFragment]
     * @param tag         最初の "client" への画面遷移に設定するタグ。
     *                    Round-trip (往復)ナビゲーション中に発生する画面遷移の中で、重複しない文字列を指定します。
     */
    @Suppress("unused")
    open fun start(
        requestKey: String,
        fragment: RoundTripClientFragment,
        tag: String = Constants.TAG_FIRST_CLIENT
    ) {
        FragmentLog.v(TAG, "start: start")
        FragmentLog.v(TAG) {
            "start: requestKey = $requestKey, containerId = $containerId, tag = $tag"
        }

        checkAndWarnContainerId()

        ArgumentsUtil.setArgument(fragment) {
            val bundle = Bundle()
            bundle.putString(Constants.KEY_REQUEST_KEY, requestKey)
            bundle.putInt(Constants.KEY_CONTAINER_ID, containerId)
            bundle.putString(Constants.KEY_FIRST_TAG, tag)
            it.putBundle(Constants.KEY_BUNDLE, bundle)
        }
        val transaction = fragmentManager.beginTransaction()
        onStartClient(transaction)
            .replace(containerId, fragment, tag)
            .addToBackStack(tag)
            .commit()

        FragmentLog.v(TAG, "start: end")
    }

    /**
     * Round-trip (往復)ナビゲーションに於ける、最初の "client" を開始時に、
     * [FragmentTransaction] へ追加の設定を行います。
     *
     * @param transaction [FragmentTransaction]
     * @return [transaction] そのもの, または [transaction] に対して追加の設定を行った [FragmentTransaction]
     */
    open fun onStartClient(transaction: FragmentTransaction): FragmentTransaction {
        return transaction.setCustomAnimations(
            R.anim.fragment_enter, R.anim.fragment_exit,
            R.anim.fragment_pop_enter, R.anim.fragment_pop_exit
        )
    }

    /**
     * Check [containerId] is set to non-default value
     *
     * - If [containerId] is the default value (0), WARN log is output.
     */
    @Suppress("MemberVisibilityCanBePrivate")
    protected fun checkAndWarnContainerId() {
        if (containerId == 0) {
            FragmentLog.w(TAG, "containerId is default value(0). Is it intended?")
        }
    }

    companion object {
        /**
         * Tag for log
         */
        const val TAG = "RoundTripManager"
    }
}