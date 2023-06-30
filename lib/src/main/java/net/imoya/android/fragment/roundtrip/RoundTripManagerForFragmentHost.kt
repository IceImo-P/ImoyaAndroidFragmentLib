package net.imoya.android.fragment.roundtrip

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleOwner

/**
 * Round-trip (往復)ナビゲーションの "host" となる [Fragment] 用の [RoundTripManager]
 *
 * @param fragment "host" となる [Fragment]
 */
@Suppress("unused")
open class RoundTripManagerForFragmentHost(
    /**
     * "host" となる [Fragment]
     */
    protected val fragment: Fragment
) : RoundTripManager() {
    override val fragmentManager: FragmentManager
        get() = fragment.parentFragmentManager

    override val lifecycleOwner: LifecycleOwner
        get() = fragment.viewLifecycleOwner
}