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

import android.app.ActionBar
import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

/**
 * ナビゲーション前提の [Fragment] です。
 * [BaseActivity] と組み合わせて使用することを想定しています。
 */
@Suppress("unused")
abstract class BaseFragment : Fragment() {
    /**
     * [Application] の [Context]
     */
    @Suppress("MemberVisibilityCanBePrivate")
    protected lateinit var appContext: Context

    /**
     * この [Fragment] が配置されている場所へ、別の [Fragment] を配置します。
     *
     * 画面の切り替え時、次のアニメーションを使用します。
     * アニメーションの内容をカスタマイズする場合は、
     * アプリケーション側のリソースで下記のリソースを再定義してください:
     *
     *  * [R.anim.fragment_enter]
     *  * [R.anim.fragment_exit]
     *  * [R.anim.fragment_pop_enter]
     *  * [R.anim.fragment_pop_exit]
     *
     * @param fragment 置き換え後の [Fragment]
     * @param tag      [Fragment] に設定するタグ
     */
    protected open fun replaceThisTo(fragment: BaseFragment, tag: String?) {
        val containerViewId = this.containerViewId
        fragment.containerViewId = containerViewId
        parentFragmentManager.beginTransaction()
            .setCustomAnimations(
                R.anim.fragment_enter, R.anim.fragment_exit,
                R.anim.fragment_pop_enter, R.anim.fragment_pop_exit
            )
            .replace(containerViewId, fragment, tag)
            .addToBackStack(tag)
            .commit()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        appContext = context.applicationContext
    }

    @Suppress("deprecation")
    @Deprecated("Deprecated in Java")
    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        appContext = activity.applicationContext
    }

    /**
     * [ActionBar] の表示非表示を設定します。
     *
     * @param visible 表示する場合true, 非表示とする場合false
     */
    protected open fun setActionBarVisibility(visible: Boolean) {
        val activity: Activity? = this.activity
        FragmentLog.v(TAG) { "setActionBarVisibility: activity = $activity" }
        if (activity is AppCompatActivity) {
            val actionBar = activity.supportActionBar
            FragmentLog.v(TAG) { "setActionBarVisibility: actionBar = $actionBar" }
            if (actionBar != null) {
                if (visible) {
                    actionBar.show()
                } else {
                    actionBar.hide()
                }
            }
        }
    }

    /**
     * アクションバー上のタイトル文字列を設定します。
     *
     * @param title タイトル文字列
     */
    @Suppress("MemberVisibilityCanBePrivate")
    protected fun setTitle(title: CharSequence?) {
        this.activity?.title = title ?: ""
    }

    /**
     * アクションバー上のタイトル文字列を設定します。
     *
     * @param titleId タイトル文字列のリソースID
     */
    protected fun setTitle(@StringRes titleId: Int) {
        this.setTitle(requireContext().getString(titleId))
    }

    /**
     * [Activity] に於いて、この [Fragment] が配置される親 [View] の ID
     */
    var containerViewId: Int
        get() = requireArguments().getInt(ARGUMENT_CONTAINER_VIEW_ID, 0)
        set(containerViewId) {
            val argumentsBefore = this.arguments
            val arguments = argumentsBefore ?: Bundle()
            arguments.putInt(ARGUMENT_CONTAINER_VIEW_ID, containerViewId)
            if (argumentsBefore == null) {
                setArguments(arguments)
            }
        }

    companion object {
        /**
         * Arguments key: この [Fragment] が配置される親 [View] の ID
         */
        protected val ARGUMENT_CONTAINER_VIEW_ID =
            BaseFragment::class.java.name + ".containerViewId"

        /**
         * Tag for log
         */
        private const val TAG = "BaseFragment"
    }
}