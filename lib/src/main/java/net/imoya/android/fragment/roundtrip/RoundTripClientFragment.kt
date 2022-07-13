package net.imoya.android.fragment.roundtrip

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import net.imoya.android.fragment.ArgumentsUtil
import net.imoya.android.fragment.FragmentLog
import net.imoya.android.fragment.R

/**
 * Round-trip (往復)ナビゲーションに於いて、 "client" となる [Fragment]
 */
abstract class RoundTripClientFragment : Fragment() {
    /**
     * Round-trip (往復)ナビゲーションを終了し、 "host" の画面へ結果を通知します。
     *
     * @param result "host" の画面へ通知する結果情報
     */
    @Suppress("unused")
    open fun returnToHost(result: Bundle) {
        val args: Bundle = requireArguments().getBundle(Constants.KEY_BUNDLE)
            ?: throw IllegalStateException("RoundTrip arguments are not set")
        val requestKey: String = args.getString(Constants.KEY_REQUEST_KEY)
            ?: throw IllegalStateException("RoundTrip requestKey argument is not set")
        val tag: String = args.getString(Constants.KEY_FIRST_TAG)
            ?: throw IllegalStateException("RoundTrip firstTag argument is not set")

        FragmentLog.v(TAG) {
            "returnToHost: requestKey = $requestKey, tag = $tag"
        }

        parentFragmentManager.setFragmentResult(requestKey, result)
        parentFragmentManager.popBackStack(tag, FragmentManager.POP_BACK_STACK_INCLUSIVE)
    }

    /**
     * Round-trip (往復)ナビゲーションに於ける、次の "client" 画面へ遷移します。
     *
     * @param fragment 次の "client" 画面となる [RoundTripClientFragment]
     * @param tag      次の "client" への画面遷移に設定するタグ。
     *                 Round-trip (往復)ナビゲーション中に発生する画面遷移の中で、重複しない文字列を指定します。
     */
    @Suppress("unused")
    open fun startNextClient(fragment: RoundTripClientFragment, tag: String) {
        val args: Bundle = requireArguments().getBundle(Constants.KEY_BUNDLE)
            ?: throw IllegalStateException("RoundTrip arguments are not set")
        val containerId: Int = args.getInt(Constants.KEY_CONTAINER_ID, 0)
        if (containerId == 0) {
            throw IllegalStateException("RoundTrip containerId argument is not set")
        }

        ArgumentsUtil.setArgument(fragment) {
            it.putBundle(Constants.KEY_BUNDLE, args)
        }
        val transaction = parentFragmentManager.beginTransaction()
        onStartNextClient(transaction)
            .replace(containerId, fragment, tag)
            .addToBackStack(tag)
            .commit()
    }

    /**
     * Round-trip (往復)ナビゲーションに於ける、次の "client" 開始時に、
     * [FragmentTransaction] へ追加の設定を行います。
     *
     * @param transaction [FragmentTransaction]
     * @return [transaction] そのもの, または [transaction] に対して追加の設定を行った [FragmentTransaction]
     */
    open fun onStartNextClient(transaction: FragmentTransaction): FragmentTransaction {
        return transaction.setCustomAnimations(
            R.anim.fragment_enter, R.anim.fragment_exit,
            R.anim.fragment_pop_enter, R.anim.fragment_pop_exit
        )
    }

    companion object {
        /**
         * Tag for log
         */
        private const val TAG = "RoundTripClientFragment"
    }
}