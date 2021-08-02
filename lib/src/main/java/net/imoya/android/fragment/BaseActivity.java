package net.imoya.android.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import net.imoya.android.util.Log;

/**
 * {@link Fragment} のナビゲーションの共通処理をある程度実装した状態の {@link Activity} です。
 * <p/>
 * <ul>
 * <li>{@link Activity} の画面全体に、1個の {@link Fragment} が表示される画面を前提とします。</li>
 * <li>デフォルトの実装では、BackStack へ {@link Fragment}
 * が追加された場合に、アクションバーへ戻るボタンを表示します。</li>
 * <li>表示中の {@link Fragment} が {@link OnNavigationUpListener}
 * を実装している場合、アクションバーの戻るボタン押下時に
 * {@link OnNavigationUpListener#onFragmentNavigationUp()} の既定の処理を実行します。</li>
 * <li>表示中の {@link Fragment} が {@link OnBackKeyListener}
 * を実装している場合、アクションバーの戻るボタン押下時に
 * {@link OnBackKeyListener#onBackKeyPressed()} の既定の処理を実行します。</li>
 * </ul>
 */
public abstract class BaseActivity extends AppCompatActivity {
    private static final String TAG = "VoiceClockMainScreen";

    private boolean isFirstResume;

    /**
     * この {@link Fragment} が配置されている場所へ、別の {@link Fragment} を配置します。
     * <p/>
     * 画面の切り替え時、次のアニメーションを使用します。
     * アニメーションの内容をカスタマイズする場合はアプリケーション側のリソースで、下記のリソースを再定義してください:
     * <ul>
     * <li>{@link R.anim#fragment_enter}</li>
     * <li>{@link R.anim#fragment_exit}</li>
     * <li>{@link R.anim#fragment_pop_enter}</li>
     * <li>{@link R.anim#fragment_pop_exit}</li>
     * </ul>
     *
     * @param containerViewId {@link Fragment} を配置する {@link View}
     * @param fragmentAfter   置き換え後の {@link Fragment}
     * @param tag             {@link Fragment} に設定するタグ
     */
    @SuppressWarnings("SameParameterValue")
    protected void replaceTo(int containerViewId, Fragment fragmentAfter, String tag) {
        if (fragmentAfter instanceof BaseFragment) {
            BaseFragment.setContainerViewId((BaseFragment) fragmentAfter, containerViewId);
        }
        this.getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(
                        R.anim.fragment_enter, R.anim.fragment_exit,
                        R.anim.fragment_pop_enter, R.anim.fragment_pop_exit)
                .replace(containerViewId, fragmentAfter, tag)
                .addToBackStack(tag)
                .commit();
    }

    /**
     * {@link Activity#setContentView(int)} の引数に設定する、レイアウトリソースIDを返します。
     *
     * @return レイアウトリソースID
     */
    @LayoutRes
    protected abstract int getContentViewResourceId();

    /**
     * {@link Activity} 起動時に初期表示する {@link Fragment} を返します。
     *
     * @return {@link Fragment}
     */
    @NonNull
    protected abstract Fragment getFirstFragment();

    /**
     * {@link Fragment} を表示する親 {@link View} のIDを返します。
     *
     * @return {@link View} のID
     */
    protected abstract int getFragmentContainerId();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setContentView(this.getContentViewResourceId());

        this.updateActionBar(true);
        this.isFirstResume = true;

        this.getSupportFragmentManager().addOnBackStackChangedListener(
                this.backStackChangedListener);

        // 初期状態の場合、最初に表示する Fragment を配置する
        if (savedInstanceState == null) {
            final int containerId = this.getFragmentContainerId();
            final Fragment firstFragment = this.getFirstFragment();
            if (firstFragment instanceof BaseFragment) {
                BaseFragment.setContainerViewId((BaseFragment) firstFragment, containerId);
            }
            this.getSupportFragmentManager().beginTransaction()
                    .add(containerId, firstFragment, "Fragment")
                    .commit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (this.isFirstResume) {
            this.isFirstResume = false;
            this.updateActionBar(true);
        }
    }

    protected void setupActionBarOnFirstFragment(@NonNull ActionBar actionBar, boolean onCreate) {
        actionBar.setDisplayHomeAsUpEnabled(false);
    }

    protected void setupActionBarOnDescendantFragment(@NonNull ActionBar actionBar) {
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    protected void updateActionBar(boolean onCreate) {
        final ActionBar actionBar = this.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowHomeEnabled(false);
            actionBar.setDisplayShowTitleEnabled(true);
            if (this.getSupportFragmentManager().getBackStackEntryCount() > 0) {
                this.setupActionBarOnDescendantFragment(actionBar);
            } else {
                this.setupActionBarOnFirstFragment(actionBar, onCreate);
            }
        }
    }

    private final FragmentManager.OnBackStackChangedListener backStackChangedListener =
            () -> this.updateActionBar(false);

    @Override
    public boolean onSupportNavigateUp() {
        // 現在表示中のFragmentがOnNavigationUpListenerを実装している場合は、処理させる
        if (!this.callCurrentFragmentNavigationUp()) {
            // 実装していないか、リスナが処理を行わなかった場合は、前のFragmentへ遷移する
            this.getSupportFragmentManager().popBackStack();
        }
        return true;
    }

    private boolean callCurrentFragmentNavigationUp() {
        // 現在表示中のFragmentがOnNavigationUpListenerを実装している場合は、処理させる
        final Fragment currentFragment = this.getSupportFragmentManager()
                .findFragmentById(this.getFragmentContainerId());
        return (currentFragment instanceof OnNavigationUpListener
                && ((OnNavigationUpListener) currentFragment).onFragmentNavigationUp());
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed: start");

        // 現在表示中のFragmentがOnBackKeyListenerを実装している場合は、処理させる
        if (!this.callCurrentFragmentBackKeyPressed()) {
            // 実装していないか、リスナが処理を行わなかった場合は、デフォルトの処理を実行する
            super.onBackPressed();
        }
    }

    /**
     * 現在表示中の {@link Fragment} が {@link OnBackKeyListener} を実装している場合は実行する
     *
     * @return true if current {@link Fragment} implements {@link OnBackKeyListener} and
     *         {@link OnBackKeyListener#onBackKeyPressed()} returned true, otherwise false.
     */
    private boolean callCurrentFragmentBackKeyPressed() {
        // 現在表示中のFragmentがOnBackKeyListenerを実装している場合は、処理させる
        final Fragment currentFragment = this.getSupportFragmentManager()
                .findFragmentById(this.getFragmentContainerId());
        return (currentFragment instanceof OnBackKeyListener
                && ((OnBackKeyListener) currentFragment).onBackKeyPressed());
    }
}
