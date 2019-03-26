package oliveira.fabio.marvelapp.di

import oliveira.fabio.marvelapp.feature.characterdetails.viewmodel.CharacterDetailsViewModel
import oliveira.fabio.marvelapp.feature.characterslist.viewmodel.CharactersListViewModel
import oliveira.fabio.marvelapp.model.repository.CharactersRepository
import org.koin.android.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module
import org.mockito.Mockito

val unitTestModule = module {
    single { Mockito.mock(CharactersRepository::class.java) }
    viewModel { CharactersListViewModel(get()) }
    viewModel { CharacterDetailsViewModel(get()) }
}