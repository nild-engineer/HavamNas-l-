package com.example.hava1.examples.counter

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class CounterViewModel : ViewModel() {

    private val _count = MutableStateFlow(0)
    val count = _count.asStateFlow()

    fun increase() {
        _count.update { it + 1 }
    }
}
