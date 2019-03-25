package oliveira.fabio.marvelapp.model.persistence

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import io.reactivex.Flowable

@Dao
interface CharacterDao {
    @Insert(onConflict = REPLACE)
    fun addFavorite(character: Character): Long

    @Delete
    fun deleteFavorite(character: Character): Int

    @Query("SELECT * FROM character")
    fun findAllFavorites(): Flowable<List<Character>>
}