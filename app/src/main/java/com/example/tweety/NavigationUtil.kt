package com.example.tweety

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import java.util.*
import kotlin.collections.HashMap

object NavigationUtil {


    private lateinit var mStack: HashMap<String, Stack<Fragment>>
    private lateinit var fragmentManager: FragmentManager
    private lateinit var previousFragment: String

    fun pushFragment(fragmentManager: FragmentManager?, fragmentContainer: Int,  fragment: Fragment, fragmentTag: String) {
        fragmentManager?.let {
            it.beginTransaction()
                .addToBackStack(fragmentTag)
                .replace(fragmentContainer,fragment)
                .commit()
        }
    }
}