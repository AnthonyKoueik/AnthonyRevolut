package com.anthony.revolut.di

import com.anthony.revolut.MyApplication
import com.anthony.revolut.di.module.AppModule
import com.anthony.revolut.di.module.NetworkModule
import com.anthony.revolut.di.module.ViewModelModule
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton


/**
 * Created by Anthony Koueik on 12/5/2019.
 * KOA
 * anthony.koueik@gmail.com
 */
@Singleton
@Component(
    modules = [AndroidSupportInjectionModule::class, AppModule::class, ViewModelModule::class,
        NetworkModule::class]
)
interface AppComponent : AndroidInjector<MyApplication> {

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: MyApplication): Builder

        fun build(): AppComponent
    }
}