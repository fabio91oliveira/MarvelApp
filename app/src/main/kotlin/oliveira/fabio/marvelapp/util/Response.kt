package oliveira.fabio.marvelapp.util

data class Response<out T>(val statusEnum: StatusEnum, val data: T?, val message: String?) {
    companion object {
        fun <T> success(data: T?) = Response(
            StatusEnum.SUCCESS,
            data,
            null
        )
        fun <T> error(msg: String? = null, data: T? = null) = Response(
            StatusEnum.ERROR,
            data,
            msg
        )
    }

    enum class StatusEnum {
        SUCCESS,
        ERROR
    }
}