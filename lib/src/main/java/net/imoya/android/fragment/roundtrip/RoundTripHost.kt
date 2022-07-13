package net.imoya.android.fragment.roundtrip

import androidx.annotation.IdRes
import androidx.fragment.app.FragmentManager

/**
 * Round-trip (往復)ナビゲーションの "client" がコールする "host" の機能を実装します。
 *
 * @param requestKey  リクエストキー。
 *                    Round-trip (往復)ナビゲーションを開始する画面の中で、重複しない文字列を指定します。
 * @param containerId "host" の画面に於いて、 "client" を表示する領域となる [android.view.View] の ID
 * @param tag         最初の "client" への画面遷移に設定するタグ。
 *                    Round-trip (往復)ナビゲーション中に発生する画面遷移の中で、重複しない文字列を指定します。
 * @param manager     [RoundTripManager.fragmentManager] の値を指定します。
 */
class RoundTripHost(
    /**
     * リクエストキー
     */
    val requestKey: String,
    /**
     * "host" の画面に於いて、 "client" を表示する領域となる [android.view.View] の ID
     */
    @IdRes val containerId: Int,
    /**
     * 最初の "client" への画面遷移に設定するタグ
     */
    private val tag: String,
    /**
     * [RoundTripManager.fragmentManager] の値
     */
    private val manager: FragmentManager
) {
    /**
     * "host" の画面へ遷移します。
     */
    fun returnToHost() {
        manager.popBackStack(tag, 0)
    }
}