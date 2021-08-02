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
import net.imoya.android.util.Log.d

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
    protected var appContext: Context? = null

    /**
     * この [Fragment] が配置されている場所へ、別の [Fragment] を配置します。
     *
     *
     * 画面の切り替え時、次のアニメーションを使用します。
     * アニメーションの内容をカスタマイズする場合は、
     * アプリケーション側のリソースで、下記のリソースを再定義してください:
     *
     *  * [R.anim.fragment_enter]
     *  * [R.anim.fragment_exit]
     *  * [R.anim.fragment_pop_enter]
     *  * [R.anim.fragment_pop_exit]
     *
     *
     * @param fragment 置き換え後の [Fragment]
     * @param tag      [Fragment] に設定するタグ
     */
    protected fun replaceThisTo(fragment: BaseFragment, tag: String?) {
        val containerViewId = getContainerViewId(this)
        setContainerViewId(fragment, containerViewId)
        this.parentFragmentManager.beginTransaction()
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
    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        appContext = activity.applicationContext
    }

    /**
     * [ActionBar] の表示非表示を設定します。
     *
     * @param visible 表示する場合true, 非表示とする場合false
     */
    protected fun setActionBarVisibility(visible: Boolean) {
        val activity: Activity? = this.activity
        d(TAG, "setActionBarVisibility: activity = $activity")
        if (activity is AppCompatActivity) {
            val actionBar = activity.supportActionBar
            d(TAG, "setActionBarVisibility: actionBar = $actionBar")
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
    protected fun setTitle(title: String?) {
        val activity: Activity? = this.activity
        if (activity != null) {
            activity.title = title
        }
    }

    /**
     * アクションバー上のタイトル文字列を設定します。
     *
     * @param titleId タイトル文字列のリソースID
     */
    protected fun setTitle(@StringRes titleId: Int) {
        this.setTitle(requireContext().getString(titleId))
    }

    companion object {
        protected val ARGUMENT_CONTAINER_VIEW_ID =
            BaseFragment::class.java.name + ".containerViewId"
        private const val TAG = "BaseFragment"

        /**
         * [Activity] に於いて、この [Fragment] が配置される親 [View] の
         * IDを設定します。
         * [BaseFragment] の実行前にこのメソッドを呼び出すことにより、
         * [.replaceThisTo] メソッドを利用可能となります。
         *
         * @param fragment        ID を設定する [BaseFragment]
         * @param containerViewId 親 [View] のID
         */
        @JvmStatic
        fun setContainerViewId(fragment: BaseFragment, containerViewId: Int) {
            val argumentsBefore = fragment.arguments
            val arguments = argumentsBefore ?: Bundle()
            arguments.putInt(ARGUMENT_CONTAINER_VIEW_ID, containerViewId)
            if (argumentsBefore == null) {
                fragment.arguments = arguments
            }
        }

        /**
         * [Activity] に於いて、この [Fragment] が配置される親 [View] の
         * IDを取得します。
         *
         * @return 親 [View] のID
         */
        @JvmStatic
        protected fun getContainerViewId(fragment: BaseFragment): Int {
            return fragment.requireArguments().getInt(ARGUMENT_CONTAINER_VIEW_ID, 0)
        }
    }
}