package com.example.desserts.view

import androidx.fragment.app.Fragment

abstract class BaseFragment : androidx.fragment.app.Fragment() {
    abstract fun title(): String
}