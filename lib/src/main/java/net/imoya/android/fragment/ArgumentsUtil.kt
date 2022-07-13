package net.imoya.android.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment

/**
 * [Fragment] の arguments ユーティリティー
 */
@Suppress("unused")
object ArgumentsUtil {
    /**
     * [Fragment] の arguments へ値を設定します。
     * arguments が未設定の場合は、 [Bundle] を新規作成して [Fragment] へ保存します。
     *
     * @param fragment 対象の [Fragment]
     * @param callback [fragment] に既存の arguments [Bundle], または新規作成した
     * [Bundle] が引数となるコールバック
     */
    @JvmStatic
    fun setArgument(fragment: Fragment, callback: (arguments: Bundle) -> Unit) {
        val originalArguments = fragment.arguments
        val arguments: Bundle = originalArguments ?: Bundle()

        callback(arguments)

        if (originalArguments == null) {
            fragment.arguments = arguments
        }
    }
}