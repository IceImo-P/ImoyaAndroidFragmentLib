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

    /**
     * [Fragment] の arguments に設定された、指定の [Bundle] を取得します。
     * arguments または指定の [Bundle] が設定されていない場合は、
     * [IllegalArgumentException] を throw します。
     *
     * @param fragment 対象の [Fragment]
     * @param key arguments に保存されている [Bundle] の key
     * @param callback 指定の [Bundle] が引数となるコールバック
     * @throws IllegalArgumentException [fragment] に arguments が設定されていないか、
     * [key] に紐づく [Bundle] が arguments に設定されていません。
     */
    @JvmStatic
    fun getBundle(fragment: Fragment, key: String, callback: (bundle: Bundle) -> Unit) {
        val arguments = fragment.arguments
            ?: throw IllegalArgumentException("Arguments are not set")
        val bundle = arguments.getBundle(key)
            ?: throw IllegalArgumentException("Argument $key is not set")
        callback(bundle)
    }
}