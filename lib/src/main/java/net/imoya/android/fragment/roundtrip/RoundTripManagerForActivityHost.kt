package net.imoya.android.fragment.roundtrip

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleOwner

/**
 * Round-trip (往復)ナビゲーションの "host" となる [AppCompatActivity] 用の [RoundTripManager]
 *
 * @param activity "host" となる [AppCompatActivity]
 */
@Suppress("unused")
open class RoundTripManagerForActivityHost(
    /**
     * "host" となる [AppCompatActivity]
     */
    @Suppress("MemberVisibilityCanBePrivate")
    protected val activity: AppCompatActivity
) : RoundTripManager() {
    override val fragmentManager: FragmentManager
        get() = activity.supportFragmentManager

    override val lifecycleOwner: LifecycleOwner
        get() = activity
}