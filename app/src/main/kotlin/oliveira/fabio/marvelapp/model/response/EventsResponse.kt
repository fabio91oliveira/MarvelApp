package oliveira.fabio.marvelapp.model.response

data class EventsResponse(
    val attributionHTML: String?,
    val attributionText: String?,
    val code: String?,
    val copyright: String?,
    val `data`: Data?,
    val etag: String?,
    val status: String?
) {
    data class Data(
        val count: String?,
        val limit: String?,
        val offset: String?,
        val results: List<Result?>?,
        val total: String?
    ) {
        data class Result(
            val characters: Characters?,
            val comics: Comics?,
            val creators: Creators?,
            val description: String?,
            val end: String?,
            val id: String?,
            val modified: String?,
            val next: Next?,
            val previous: Previous?,
            val resourceURI: String?,
            val series: Series?,
            val start: String?,
            val stories: Stories?,
            val thumbnail: Thumbnail?,
            val title: String?,
            val urls: List<Url?>?
        ) {
            data class Characters(
                val available: String?,
                val collectionURI: String?,
                val items: List<Item?>?,
                val returned: String?
            ) {
                data class Item(
                    val name: String?,
                    val resourceURI: String?,
                    val role: String?
                )
            }

            data class Creators(
                val available: String?,
                val collectionURI: String?,
                val items: List<Item?>?,
                val returned: String?
            ) {
                data class Item(
                    val name: String?,
                    val resourceURI: String?,
                    val role: String?
                )
            }

            data class Previous(
                val name: String?,
                val resourceURI: String?
            )

            data class Next(
                val name: String?,
                val resourceURI: String?
            )

            data class Series(
                val available: String?,
                val collectionURI: String?,
                val items: List<Item?>?,
                val returned: String?
            ) {
                data class Item(
                    val name: String?,
                    val resourceURI: String?
                )
            }

            data class Thumbnail(
                val extension: String?,
                val path: String?
            )

            data class Comics(
                val available: String?,
                val collectionURI: String?,
                val items: List<Item?>?,
                val returned: String?
            ) {
                data class Item(
                    val name: String?,
                    val resourceURI: String?
                )
            }

            data class Stories(
                val available: String?,
                val collectionURI: String?,
                val items: List<Item?>?,
                val returned: String?
            ) {
                data class Item(
                    val name: String?,
                    val resourceURI: String?,
                    val type: String?
                )
            }

            data class Url(
                val type: String?,
                val url: String?
            )
        }
    }
}