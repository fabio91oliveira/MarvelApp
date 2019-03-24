package oliveira.fabio.marvelapp.di

import oliveira.fabio.marvelapp.network.api.MarvelApi
import oliveira.fabio.marvelapp.network.config.provideApi
import oliveira.fabio.marvelapp.util.Constants
import org.koin.dsl.module.module

val apiModule = module {
    single { provideApi(MarvelApi::class.java, Constants.API_URL) }
}