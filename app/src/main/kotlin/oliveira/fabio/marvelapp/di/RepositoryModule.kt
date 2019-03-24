package oliveira.fabio.marvelapp.di

import oliveira.fabio.marvelapp.model.repository.CharactersRepository
import org.koin.dsl.module.module

val repositoryModule = module {
    single { CharactersRepository(get()) }
}