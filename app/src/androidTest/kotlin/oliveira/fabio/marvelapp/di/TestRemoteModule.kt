package oliveira.fabio.marvelapp.di

import oliveira.fabio.marvelapp.base.TestUrl
import oliveira.fabio.marvelapp.network.api.MarvelApi
import oliveira.fabio.marvelapp.network.config.provideApi
import org.koin.dsl.module.module

val testRemoteModule = module {
    single(override = true) { provideApi(MarvelApi::class.java, TestUrl.urlTest) }
}