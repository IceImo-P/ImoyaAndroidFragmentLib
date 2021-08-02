package net.imoya.android.fragment

import android.app.Activity
import android.os.Bundle
import android.view.View
import androidx.annotation.LayoutRes
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import net.imoya.android.fragment.BaseFragment.Companion.setContainerViewId
import net.imoya.android.util.Log.d

/**
 * [Fragment] のナビゲーションの共通処理をある程度実装した状態の [Activity] です。
 *
 *
 *
 *  * [Activity] の画面全体に、1個の [Fragment] が表示される画面を前提とします。
 *  * デフォルトの実装では、BackStack へ [Fragment]
 * が追加された場合に、アクションバーへ戻るボタンを表示します。
 *  * 表示中の [Fragment] が [OnNavigationUpListener]
 * を実装している場合、アクションバーの戻るボタン押下時に
 * [OnNavigationUpListener.onFragmentNavigationUp] の既定の処理を実行します。
 *  * 表示中の [Fragment] が [OnBackKeyListener]
 * を実装している場合、アクションバーの戻るボタン押下時に
 * [OnBackKeyListener.onBackKeyPressed] の既定の処理を実行します。
 *
 */
@Suppress("unused")
abstract class BaseActivity : AppCompatActivity() {
    private var isFirstResume = false

    /**
     * この [Fragment] が配置されている場所へ、別の [Fragment] を配置します。
     *
     *
     * 画面の切り替え時、次のアニメーションを使用します。
     * アニメーションの内容をカスタマイズする場合はアプリケーション側のリソースで、下記のリソースを再定義してください:
     *
     *  * [R.anim.fragment_enter]
     *  * [R.anim.fragment_exit]
     *  * [R.anim.fragment_pop_enter]
     *  * [R.anim.fragment_pop_exit]
     *
     *
     * @param containerViewId [Fragment] を配置する [View]
     * @param fragmentAfter   置き換え後の [Fragment]
     * @param tag             [Fragment] に設定するタグ
     */
    protected fun replaceTo(containerViewId: Int, fragmentAfter: Fragment?, tag: String?) {
        if (fragmentAfter is BaseFragment) {
            setContainerViewId((fragmentAfter as BaseFragment?)!!, containerViewId)
        }
        this.supportFragmentManager.beginTransaction()
            .setCustomAnimations(
                R.anim.fragment_enter, R.anim.fragment_exit,
                R.anim.fragment_pop_enter, R.anim.fragment_pop_exit
            )
            .replace(containerViewId, fragmentAfter!!, tag)
            .addToBackStack(tag)
            .commit()
    }

    /**
     * [Activity.setContentView] の引数に設定する、レイアウトリソースIDを返します。
     *
     * @return レイアウトリソースID
     */
    @get:LayoutRes
    protected abstract val contentViewResourceId: Int

    /**
     * [Activity] 起動時に初期表示する [Fragment] を返します。
     *
     * @return [Fragment]
     */
    protected abstract val firstFragment: Fragment

    /**
     * [Fragment] を表示する親 [View] のIDを返します。
     *
     * @return [View] のID
     */
    protected abstract val fragmentContainerId: Int

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.setContentView(contentViewResourceId)
        updateActionBar(true)
        isFirstResume = true
        this.supportFragmentManager.addOnBackStackChangedListener(
            backStackChangedListener
        )

        // 初期状態の場合、最初に表示する Fragment を配置する
        if (savedInstanceState == null) {
            val containerId = fragmentContainerId
            val firstFragment = firstFragment
            if (firstFragment is BaseFragment) {
                setContainerViewId(firstFragment, containerId)
            }
            this.supportFragmentManager.beginTransaction()
                .add(containerId, firstFragment, "Fragment")
                .commit()
        }
    }

    override fun onResume() {
        super.onResume()
        if (isFirstResume) {
            isFirstResume = false
            updateActionBar(true)
        }
    }

    protected open fun setupActionBarOnFirstFragment(actionBar: ActionBar, onCreate: Boolean) {
        actionBar.setDisplayHomeAsUpEnabled(false)
    }

    protected open fun setupActionBarOnDescendantFragment(actionBar: ActionBar) {
        actionBar.setDisplayHomeAsUpEnabled(true)
    }

    @Suppress("MemberVisibilityCanBePrivate")
    protected fun updateActionBar(onCreate: Boolean) {
        val actionBar = this.supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayShowHomeEnabled(false)
            actionBar.setDisplayShowTitleEnabled(true)
            if (this.supportFragmentManager.backStackEntryCount > 0) {
                setupActionBarOnDescendantFragment(actionBar)
            } else {
                setupActionBarOnFirstFragment(actionBar, onCreate)
            }
        }
    }

    private val backStackChangedListener =
        FragmentManager.OnBackStackChangedListener { updateActionBar(false) }

    override fun onSupportNavigateUp(): Boolean {
        // 現在表示中のFragmentがOnNavigationUpListenerを実装している場合は、処理させる
        if (!callCurrentFragmentNavigationUp()) {
            // 実装していないか、リスナが処理を行わなかった場合は、前のFragmentへ遷移する
            this.supportFragmentManager.popBackStack()
        }
        return true
    }

    private fun callCurrentFragmentNavigationUp(): Boolean {
        // 現在表示中のFragmentがOnNavigationUpListenerを実装している場合は、処理させる
        val currentFragment = this.supportFragmentManager
            .findFragmentById(fragmentContainerId)
        return (currentFragment is OnNavigationUpListener
                && (currentFragment as OnNavigationUpListener).onFragmentNavigationUp())
    }

    override fun onBackPressed() {
        d(TAG, "onBackPressed: start")

        // 現在表示中のFragmentがOnBackKeyListenerを実装している場合は、処理させる
        if (!callCurrentFragmentBackKeyPressed()) {
            // 実装していないか、リスナが処理を行わなかった場合は、デフォルトの処理を実行する
            super.onBackPressed()
        }
    }

    /**
     * 現在表示中の [Fragment] が [OnBackKeyListener] を実装している場合は実行する
     *
     * @return true if current [Fragment] implements [OnBackKeyListener] and
     * [OnBackKeyListener.onBackKeyPressed] returned true, otherwise false.
     */
    private fun callCurrentFragmentBackKeyPressed(): Boolean {
        // 現在表示中のFragmentがOnBackKeyListenerを実装している場合は、処理させる
        val currentFragment = this.supportFragmentManager
            .findFragmentById(fragmentContainerId)
        return (currentFragment is OnBackKeyListener
                && (currentFragment as OnBackKeyListener).onBackKeyPressed())
    }

    companion object {
        private const val TAG = "VoiceClockMainScreen"
    }
}