package oliveira.fabio.marvelapp.model.vo

import java.io.Serializable

data class Four<out A, out B, out C, out D>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D
) : Serializable