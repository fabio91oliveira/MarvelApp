package oliveira.fabio.marvelapp.di

import oliveira.fabio.marvelapp.feature.characterdetails.viewmodel.CharacterDetailsViewModel
import oliveira.fabio.marvelapp.feature.characterslist.viewmodel.CharactersFavoriteListViewModel
import oliveira.fabio.marvelapp.feature.characterslist.viewmodel.CharactersListViewModel
import org.koin.android.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module

val viewModelModule = module {
    viewModel { CharactersListViewModel(get()) }
    viewModel { CharacterDetailsViewModel(get()) }
}