package com.githukudenis.wetha

import com.githukudenis.wetha.util.LocationListenerWorker
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.androidx.workmanager.dsl.worker
import org.koin.dsl.module

val appModule = module {
    viewModel {
        MainViewModel(get(), get(), get())
    }

    worker {
        LocationListenerWorker(androidContext(), get(), get(), get(), get())
    }
}