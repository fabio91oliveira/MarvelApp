package oliveira.fabio.marvelapp.model.response

import java.io.Serializable

data class Four<out A, out B, out C, out D>(
    public val first: A,
    public val second: B,
    public val third: C,
    public val fourth: D
) : Serializable