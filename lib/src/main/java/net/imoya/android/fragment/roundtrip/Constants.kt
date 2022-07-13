package net.imoya.android.fragment.roundtrip

import androidx.fragment.app.FragmentTransaction

/**
 * Round-trip (往復)ナビゲーションに於いて使用する、定数の定義
 */
object Constants {
    /**
     * "host" が最初の "client" を起動する画面遷移に於いて、
     * [FragmentTransaction] へ設定するタグのデフォルト値
     */
    const val TAG_FIRST_CLIENT = "net.imoya.android.fragment.roundtrip"

    /**
     * 各種キーの prefix 文字列
     */
    private const val KEY_PREFIX = "net.imoya.android.fragment.roundtrip"

    /**
     * "client" が保持する arguments に於いて、
     * Round-trip (往復)ナビゲーションの設定値を含めた [android.os.Bundle] のキー
     */
    const val KEY_BUNDLE = "$KEY_PREFIX.bundle"

    /**
     * [android.os.Bundle] へリクエストキーを保存する際に使用するキー
     */
    const val KEY_REQUEST_KEY = "requestKey"

    /**
     * [android.os.Bundle] へ、 "client" を表示する領域となる [android.view.View] の ID を保存するキー
     */
    const val KEY_CONTAINER_ID = "containerId"

    /**
     * [android.os.Bundle] へ、
     * Round-trip (往復)ナビゲーション開始時の画面遷移に設定されたタグを保存するキー
     */
    const val KEY_FIRST_TAG = "firstTag"
}