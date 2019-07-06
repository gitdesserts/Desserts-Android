package com.example.desserts.view

import android.support.v4.app.Fragment

abstract class BaseFragment : Fragment() {
    abstract fun title(): String
}