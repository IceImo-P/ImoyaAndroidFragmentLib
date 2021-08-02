package net.imoya.android.fragment

import androidx.fragment.app.Fragment

/**
 * [Fragment] 用、戻るキーリスナ
 */
interface OnBackKeyListener {
    /**
     * 戻るキーが押された時の処理
     *
     * @return 処理を実行した(呼び出し元の処理が不要の)場合はtrue, 呼び出し元の処理に任せる場合はfalse
     */
    fun onBackKeyPressed(): Boolean
}