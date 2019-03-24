package oliveira.fabio.marvelapp.util.extensions

import java.math.BigInteger
import java.security.MessageDigest

private const val MD5_ALGORITHM = "MD5"

fun String.toMD5(): String {
    val md = MessageDigest.getInstance(MD5_ALGORITHM)
    return BigInteger(1, md.digest(toByteArray())).toString(16).padStart(32, '0')
}