package oliveira.fabio.marvelapp.model.persistence

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [(Character::class)], version = 1, exportSchema = false)
abstract class Database : RoomDatabase() {
    abstract fun characterResultDao(): CharacterDao
}