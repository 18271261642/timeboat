package net.sgztech.timeboat.managerUtlis

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.device.ui.baseUi.baseFragment.BaseFragment
import net.sgztech.timeboat.config.Constants
import com.device.ui.baseUi.interfaces.FragmentOnTouchListener
import com.device.ui.baseUi.interfaces.WindowSizeChangedListener
import net.sgztech.timeboat.R
import org.apache.commons.lang3.StringUtils
import java.io.Serializable
import java.util.ArrayList
import java.util.HashMap

class FragmentSwitchManager(activity: AppCompatActivity?) {

    private var fragmentManager: FragmentManager? = null
    var currentFragment: Fragment? = null
    private val fragmentList: MutableList<BaseFragment>? = ArrayList()
    private val onTouchListenersMap: MutableMap<String, FragmentOnTouchListener> = HashMap()
    private val onWindowSizeChangedListeners: MutableList<WindowSizeChangedListener> = ArrayList()
    fun getOnTouchListenersMap(): Map<String, FragmentOnTouchListener> {
        return onTouchListenersMap
    }

    fun getOnWindowSizeChangedListeners(): List<WindowSizeChangedListener> {
        return onWindowSizeChangedListeners
    }

    fun add(fragment: BaseFragment?): FragmentSwitchManager {
        if (fragment == null) {
            return this
        }
        currentFragment = fragment
        fragmentList!!.add(fragment)
        return this
    }

    fun commit() {
        if (fragmentManager == null) {
            return
        }
        if (fragmentList == null || fragmentList.size == 0) {
            return
        }
        val fragmentTransaction = fragmentManager!!.beginTransaction()
        for (i in fragmentList.indices) {
            if (fragmentList[i].isAdded) {
                continue
            }
            fragmentTransaction.add(
                R.id.main_content,
                fragmentList[i],
                fragmentList[i].getLabel()
            )
        }
        for (fr in fragmentList) {
            if (fr.isMainPage()) {
                fragmentTransaction.show(fr)
                currentFragment = fr
            } else if (!fr.isHidden) {
                fragmentTransaction.hide(fr)
            }
        }
        fragmentTransaction.commitNowAllowingStateLoss()
    }

    fun showFragment(fragment: Fragment?) {
        if (fragmentManager == null) {
            return
        }
        if (currentFragment == null || fragment == null) {
            return
        }
        if (StringsUtils.equals(currentFragment!!.javaClass.name, fragment.javaClass.name)) {
            return
        }
        fragmentManager!!.beginTransaction().hide(currentFragment!!).show(fragment)
            .commitNowAllowingStateLoss()
        currentFragment = fragment
    }

//    fun showMainFragment(action: MapAction?) {
//        if (fragmentManager == null || action == null) {
//            return
//        }
//        val frgm = getFragmentByLabel(Constants.FRAGMENT_LABEL_MAIN) ?: return
//        val fragment: BaseFragment = frgm as BaseFragment
//        if (!fragment.isHidden() || fragment.isVisible()) {
//            fragment.exeMapAction(action)
//        } else {
//            val bundle = Bundle()
//            bundle.putSerializable("map_action", action)
//            fragment.setArguments(bundle)
//            fragmentManager.beginTransaction().hide(currentFragment!!).show(fragment)
//                .commitNowAllowingStateLoss()
//        }
//        currentFragment = fragment
//    }

    fun showMainFragment() {
        if (fragmentManager == null) {
            return
        }
        val fragment = getFragmentByLabel(Constants.FRAGMENT_LABEL_MAIN) ?: return
        if (fragment.isHidden || !fragment.isVisible) {
            fragmentManager!!.beginTransaction().hide(currentFragment!!).show(fragment)
                .commitNowAllowingStateLoss()
        }
        currentFragment = fragment
    }

    fun showFragment(fragment: Fragment?, attr: String?) {
        if (fragmentManager == null || fragment == null) {
            return
        }
        if (!StringUtils.isEmpty(attr)) {
            val bundle = Bundle()
            bundle.putString("attr", attr)
            fragment.arguments = bundle
        }
        fragmentManager!!.beginTransaction().hide(currentFragment!!).show(fragment)
            .commitNowAllowingStateLoss()
        currentFragment = fragment
    }

    fun showFragment(fragment: Fragment?, attr: Serializable?) {
        if (fragmentManager == null || fragment == null) {
            return
        }
        val bundle = Bundle()
        bundle.putSerializable("attr", attr)
        fragment.arguments = bundle
        fragmentManager!!.beginTransaction().hide(currentFragment!!).show(fragment)
            .commitNowAllowingStateLoss()
        currentFragment = fragment
    }

    fun getFragmentByLabel(tag: String?): Fragment? {
        return if (fragmentManager == null) {
            Fragment()
        } else fragmentManager!!.findFragmentByTag(tag)
    }

    val currentScene: String
        get() = if (currentFragment != null) {
            currentFragment!!.javaClass.simpleName
        } else StringUtils.EMPTY

    fun registerFragmentOnTouchListener(
        fragmentName: String,
        onTouchListener: FragmentOnTouchListener
    ) {
        onTouchListenersMap[fragmentName] = onTouchListener
    }

    fun unRegisterFragmentOnTouchListener(fragmentName: String) {
        onTouchListenersMap.remove(fragmentName)
    }

    fun registerWindowFocusChangedListener(onWindowSizeChangedListener: WindowSizeChangedListener) {
        onWindowSizeChangedListeners.add(onWindowSizeChangedListener)
    }

    companion object {
        private val TAG = FragmentSwitchManager::class.java.simpleName
    }

    init {
        if (activity != null && !activity.isFinishing) {
            fragmentManager = activity.supportFragmentManager
        }
    }
}