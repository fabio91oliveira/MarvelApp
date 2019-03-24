package oliveira.fabio.marvelapp

import android.app.Application
import oliveira.fabio.marvelapp.di.apiModule
import oliveira.fabio.marvelapp.di.repositoryModule
import oliveira.fabio.marvelapp.di.viewModelModule
import org.koin.android.ext.android.startKoin

class MarvelApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin(this, listOf(apiModule, repositoryModule, viewModelModule))
    }
}