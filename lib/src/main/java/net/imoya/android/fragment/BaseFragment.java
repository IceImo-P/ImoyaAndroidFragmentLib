package net.imoya.android.fragment;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import net.imoya.android.util.Log;

/**
 * ナビゲーション前提の {@link Fragment} です。
 * {@link BaseActivity} と組み合わせて使用することを想定しています。
 */
public abstract class BaseFragment extends Fragment {
    protected static final String ARGUMENT_CONTAINER_VIEW_ID =
            BaseFragment.class.getName() + ".containerViewId";

    /**
     * {@link Application} の {@link Context}
     */
    protected Context appContext = null;

    private static final String TAG = "BaseFragment";

    /**
     * {@link Activity} に於いて、この {@link Fragment} が配置される親 {@link View} の
     * IDを設定します。
     * {@link BaseFragment} の実行前にこのメソッドを呼び出すことにより、
     * {@link #replaceThisTo(BaseFragment, String)} メソッドを利用可能となります。
     *
     * @param fragment        ID を設定する {@link BaseFragment}
     * @param containerViewId 親 {@link View} のID
     */
    public static void setContainerViewId(@NonNull BaseFragment fragment, int containerViewId) {
        final Bundle argumentsBefore = fragment.getArguments();
        final Bundle arguments = (argumentsBefore != null ? argumentsBefore : new Bundle());
        arguments.putInt(ARGUMENT_CONTAINER_VIEW_ID, containerViewId);
        if (argumentsBefore == null) {
            fragment.setArguments(arguments);
        }
    }

    /**
     * {@link Activity} に於いて、この {@link Fragment} が配置される親 {@link View} の
     * IDを取得します。
     *
     * @return 親 {@link View} のID
     */
    protected static int getContainerViewId(@NonNull BaseFragment fragment) {
        return fragment.requireArguments().getInt(ARGUMENT_CONTAINER_VIEW_ID, 0);
    }

    /**
     * この {@link Fragment} が配置されている場所へ、別の {@link Fragment} を配置します。
     * <p/>
     * 画面の切り替え時、次のアニメーションを使用します。
     * アニメーションの内容をカスタマイズする場合は、
     * アプリケーション側のリソースで、下記のリソースを再定義してください:
     * <ul>
     * <li>{@link R.anim#fragment_enter}</li>
     * <li>{@link R.anim#fragment_exit}</li>
     * <li>{@link R.anim#fragment_pop_enter}</li>
     * <li>{@link R.anim#fragment_pop_exit}</li>
     * </ul>
     *
     * @param fragment 置き換え後の {@link Fragment}
     * @param tag      {@link Fragment} に設定するタグ
     */
    protected void replaceThisTo(@NonNull BaseFragment fragment, @Nullable String tag) {
        final int containerViewId = BaseFragment.getContainerViewId(this);
        setContainerViewId(fragment, containerViewId);
        this.getParentFragmentManager().beginTransaction()
                .setCustomAnimations(
                        R.anim.fragment_enter, R.anim.fragment_exit,
                        R.anim.fragment_pop_enter, R.anim.fragment_pop_exit)
                .replace(containerViewId, fragment, tag)
                .addToBackStack(tag)
                .commit();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.appContext = context.getApplicationContext();
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(@NonNull Activity activity) {
        super.onAttach(activity);
        this.appContext = activity.getApplicationContext();
    }

    /**
     * {@link ActionBar} の表示非表示を設定します。
     *
     * @param visible 表示する場合true, 非表示とする場合false
     */
    @SuppressWarnings("SameParameterValue")
    protected void setActionBarVisibility(boolean visible) {
        final Activity activity = this.getActivity();
        Log.d(TAG, "setActionBarVisibility: activity = " + activity);
        if (activity instanceof AppCompatActivity) {
            final ActionBar actionBar = ((AppCompatActivity) activity).getSupportActionBar();
            Log.d(TAG, "setActionBarVisibility: actionBar = " + actionBar);
            if (actionBar != null) {
                if (visible) {
                    actionBar.show();
                } else {
                    actionBar.hide();
                }
            }
        }
    }

    /**
     * アクションバー上のタイトル文字列を設定します。
     *
     * @param title タイトル文字列
     */
    protected void setTitle(String title) {
        final Activity activity = this.getActivity();
        if (activity != null) {
            activity.setTitle(title);
        }
    }

    /**
     * アクションバー上のタイトル文字列を設定します。
     *
     * @param titleId タイトル文字列のリソースID
     */
    protected void setTitle(@StringRes int titleId) {
        this.setTitle(this.requireContext().getString(titleId));
    }
}
