package oliveira.fabio.marvelapp.model.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

data class CharactersResponse(
    val attributionHTML: String,
    val attributionText: String,
    val code: String,
    val copyright: String,
    val `data`: Data,
    val etag: String,
    val status: String
) {
    data class Data(
        val count: String,
        val limit: String,
        val offset: String,
        var results: MutableList<Result>,
        val total: String
    ) {
        @Parcelize
        data class Result(
            var isFavorite: Boolean = false,
            val comics: Comics,
            val description: String,
            val events: Events,
            val id: String,
            val modified: String,
            val name: String,
            @SerializedName("resourceURI") val resourceURI: String,
            val series: Series,
            val stories: Stories,
            val thumbnail: Thumbnail,
            val urls: List<Url>
        ) : Parcelable {
            @Parcelize
            data class Url(
                val type: String,
                val url: String
            ) : Parcelable

            @Parcelize
            data class Comics(
                val available: String,
                @SerializedName("collectionURI") val collectionURI: String,
                val items: List<Item>,
                val returned: String
            ) : Parcelable

            @Parcelize
            data class Stories(
                val available: String,
                @SerializedName("collectionURI") val collectionURI: String,
                val items: List<Item>,
                val returned: String
            ) : Parcelable

            @Parcelize
            data class Events(
                val available: String,
                @SerializedName("collectionURI") val collectionURI: String,
                val items: List<Item>,
                val returned: String
            ) : Parcelable

            @Parcelize
            data class Series(
                val available: String,
                @SerializedName("collectionURI") val collectionURI: String,
                val items: List<Item>,
                val returned: String
            ) : Parcelable

            @Parcelize
            data class Thumbnail(
                val extension: String,
                val path: String
            ) : Parcelable {
                fun getFullImageUrl() = "$path.$extension"
            }

            @Parcelize
            data class Item(
                val name: String,
                @SerializedName("resourceURI") val resourceURI: String
            ) : Parcelable
        }
    }
}