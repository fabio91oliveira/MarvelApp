package oliveira.fabio.marvelapp.di

import androidx.room.Room
import oliveira.fabio.marvelapp.model.persistence.Database
import org.koin.dsl.module.module

val daoModule = module {
    single {
        Room.databaseBuilder(
            get(),
            Database::class.java,
            "MarvelApp.db"
        )
            .build()
    }
    single { get<Database>().characterResultDao() }
}