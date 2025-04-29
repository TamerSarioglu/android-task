package com.tamersarioglu.veroandroidtask.utils

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

inline fun <T> Flow<T>.collectLatestLifecycleFlow(
    lifecycleOwner: LifecycleOwner,
    crossinline collector: suspend (T) -> Unit
) {
    lifecycleOwner.lifecycleScope.launch {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            this@collectLatestLifecycleFlow.collectLatest {
                collector(it)
            }
        }
    }
}