package oliveira.fabio.marvelapp.model.response

data class EventValueObject(
    var title: String?,
    var description: String?
) {
    companion object {
        const val COMICS_TAG = "Comics"
        const val EVENTS_TAG = "Events"
        const val STORIES_TAG = "Stories"
        const val SERIES_TAG = "Series"
    }
}