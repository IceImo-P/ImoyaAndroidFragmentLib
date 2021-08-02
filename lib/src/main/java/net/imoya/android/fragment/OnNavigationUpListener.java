package net.imoya.android.fragment;

/**
 * 前の画面へ戻る操作発生時の処理
 */
public interface OnNavigationUpListener {
    /**
     * 前の画面へ戻る操作発生時の処理を行います。
     *
     * @return 呼び出された側で前の画面へ戻る処理を行う場合はtrue,
     * 呼び出し側へ処理を任せる場合はfalse
     */
    boolean onFragmentNavigationUp();
}

