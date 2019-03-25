package oliveira.fabio.marvelapp.model.persistence

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "character")
data class Character(
    var name: String,
    var isFavorite: Boolean,
    var description: String,
    var resourceURI: String,
    var urlImage: String
) : Serializable {
    @PrimaryKey(autoGenerate = false)
    var id: Long = 0
}