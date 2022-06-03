/*
 * Copyright (C) 2022 IceImo-P
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.imoya.android.fragment

import android.app.Activity
import android.os.Bundle
import android.view.View
import androidx.annotation.LayoutRes
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

/**
 * [Fragment] のナビゲーション処理をある程度実装した状態の [AppCompatActivity] です。
 *
 *  * [AppCompatActivity] の画面全体に、1個の [Fragment] が表示される画面を前提とします。
 *  * デフォルトの実装では、BackStack へ [Fragment] が追加された場合に、アクションバーへ戻るボタンを表示します。
 *  * 表示中の [Fragment] が [OnNavigationUpListener] を実装している場合、アクションバーの戻るボタン押下時に
 * [OnNavigationUpListener.onFragmentNavigationUp] の既定の処理を実行します。
 *  * 表示中の [Fragment] が [OnBackKeyListener] を実装している場合、アクションバーの戻るボタン押下時に
 * [OnBackKeyListener.onBackKeyPressed] の既定の処理を実行します。
 */
@Suppress("unused")
abstract class BaseActivity : AppCompatActivity() {
    /**
     * [onCreate] の実行後、最初に [onResume] が処理を開始するタイミングまでは true, その他の場合は false
     */
    private var isFirstResume = false

    /**
     * この [Fragment] が配置されている場所へ、別の [Fragment] を配置します。
     *
     * 画面の切り替え時、次のアニメーションを使用します。
     * アニメーションの内容をカスタマイズする場合はアプリケーション側のリソースで、下記のリソースを再定義してください:
     *
     *  * [R.anim.fragment_enter]
     *  * [R.anim.fragment_exit]
     *  * [R.anim.fragment_pop_enter]
     *  * [R.anim.fragment_pop_exit]
     *
     * @param containerViewId [Fragment] を配置する [View]
     * @param fragmentAfter   置き換え後の [Fragment]
     * @param tag             [Fragment] に設定するタグ
     */
    protected fun replaceTo(containerViewId: Int, fragmentAfter: Fragment, tag: String?) {
        if (fragmentAfter is BaseFragment) {
            fragmentAfter.containerViewId = containerViewId
        }
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(
                R.anim.fragment_enter, R.anim.fragment_exit,
                R.anim.fragment_pop_enter, R.anim.fragment_pop_exit
            )
            .replace(containerViewId, fragmentAfter, tag)
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
        setContentView(contentViewResourceId)
        updateActionBar(true)
        isFirstResume = true
        supportFragmentManager.addOnBackStackChangedListener(
            backStackChangedListener
        )

        // 初期状態の場合、最初に表示する Fragment を配置する
        if (savedInstanceState == null) {
            val containerId = fragmentContainerId
            val firstFragment = firstFragment
            if (firstFragment is BaseFragment) {
                firstFragment.containerViewId = containerId
            }
            supportFragmentManager.beginTransaction()
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

    /**
     * [AppCompatActivity] に初期表示する [Fragment] を表示開始時にコールされ、
     * [ActionBar] の設定を行うメソッド
     *
     * @param actionBar [ActionBar]
     * @param onCreate [AppCompatActivity] 生成・再生成時は true, その他の場合は false
     */
    protected open fun setupActionBarOnFirstFragment(actionBar: ActionBar, onCreate: Boolean) {
        actionBar.setDisplayHomeAsUpEnabled(false)
    }

    /**
     * [AppCompatActivity] に初期表示でない [Fragment] を表示開始時にコールされ、
     * [ActionBar] の設定を行うメソッド
     *
     * @param actionBar [ActionBar]
     */
    protected open fun setupActionBarOnDescendantFragment(actionBar: ActionBar) {
        actionBar.setDisplayHomeAsUpEnabled(true)
    }

    /**
     * [ActionBar] の表示を更新します。
     *
     * @param onCreate [AppCompatActivity] 生成・再生成時は true, その他の場合は false
     */
    protected open fun updateActionBar(onCreate: Boolean) {
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayShowHomeEnabled(false)
            actionBar.setDisplayShowTitleEnabled(true)
            if (supportFragmentManager.backStackEntryCount > 0) {
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
            supportFragmentManager.popBackStack()
        }
        return true
    }

    /**
     * アクションバーの戻るボタン押下時の既定の処理
     *
     * 現在表示中の [Fragment] が [OnNavigationUpListener] を実装している場合は実行します。
     *
     * @return true if current [Fragment] implements [OnNavigationUpListener] and
     * [OnNavigationUpListener.onFragmentNavigationUp] returned true, otherwise false.
     */
    private fun callCurrentFragmentNavigationUp(): Boolean {
        // 現在表示中のFragmentがOnNavigationUpListenerを実装している場合は、処理させる
        val currentFragment = supportFragmentManager.findFragmentById(fragmentContainerId)
        return (currentFragment is OnNavigationUpListener
                && (currentFragment as OnNavigationUpListener).onFragmentNavigationUp())
    }

    override fun onBackPressed() {
        FragmentLog.v(TAG, "onBackPressed: start")

        // 現在表示中のFragmentがOnBackKeyListenerを実装している場合は、処理させる
        if (!callCurrentFragmentBackKeyPressed()) {
            // 実装していないか、リスナが処理を行わなかった場合は、デフォルトの処理を実行する
            FragmentLog.v(TAG, "onBackPressed: default onBackPressed process")
            super.onBackPressed()
        }
    }

    /**
     * 端末戻るキー押下時の既定の処理
     *
     * 現在表示中の [Fragment] が [OnBackKeyListener] を実装している場合は実行します。
     *
     * @return true if current [Fragment] implements [OnBackKeyListener] and
     * [OnBackKeyListener.onBackKeyPressed] returned true, otherwise false.
     */
    private fun callCurrentFragmentBackKeyPressed(): Boolean {
        // 現在表示中のFragmentがOnBackKeyListenerを実装している場合は、処理させる
        val currentFragment = supportFragmentManager.findFragmentById(fragmentContainerId)
        return (currentFragment is OnBackKeyListener
                && (currentFragment as OnBackKeyListener).onBackKeyPressed())
    }

    companion object {
        /**
         * Tag for log
         */
        private const val TAG = "BaseActivity"
    }
}